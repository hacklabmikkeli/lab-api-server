package fi.hacklabmikkeli.labapi.server.stripe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Card;
import com.stripe.model.Customer;
import com.stripe.model.ExternalAccountCollection;
import com.stripe.model.Plan;
import com.stripe.model.PlanCollection;
import com.stripe.model.Product;
import com.stripe.model.ProductCollection;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;

import org.slf4j.Logger;

import fi.hacklabmikkeli.labapi.server.settings.SystemSettingController;

@ApplicationScoped
public class StripeController {

  @Inject
  private SystemSettingController systemSettingController;

  @Inject
  private Logger logger;

  @PostConstruct
  public void init() {
    Stripe.apiKey = systemSettingController.getSettingValue(StripeConsts.STRIPE_API_KEY_SETTING);
  }

  /**
   * Crates new customer to stripe
   * 
   * @param email User email
   * @param keycloakUserId user keycloak id
   * @return created customer
   */
  public Customer createCustomer(String email, String keycloakUserId) {
    Map<String, Object> customerParams = new HashMap<>();
    customerParams.put("email", email);

    Map<String, Object> customerMeta = new HashMap<>();
    customerMeta.put("keycloak_id", keycloakUserId);
    customerParams.put("metadata", customerMeta);

    try {
      return Customer.create(customerParams);
    } catch (StripeException e) {
      logger.error("Error creating stripe customer", e);
    }

    return null;
  }

  /**
   * Lists active products from stripe
   * 
   * @return List of active products
   */
  public List<Product> listProducts() {
    Map<String, Object> productParams = new HashMap<>();
    productParams.put("active", "true");

    try {
      ProductCollection products = Product.list(productParams);
      return products.getData();
    } catch (StripeException e) {
      logger.error("Error listing products from stripe", e);
    }

    return null;
  }

  /**
   * Lists plans for single product from stripe
   * 
   * @param productId product id
   * @return list of plans for product
   */
  public List<Plan> listPlans(String productId) {
    if (productId == null) {
      logger.error("Listing plans without product id is not supported");
      return null;
    }

    Map<String, Object> planParams = new HashMap<>();
    planParams.put("product", productId);

    try {
      PlanCollection plans = Plan.list(planParams);
      return plans.getData();
    } catch (StripeException e) {
      logger.error("Error listing plans from stripe", e);
    }

    return null;
  }

  /**
   * Creates new card to stripe
   * 
   * @param customerId customer id
   * @param sourceToken source token
   * @return created card
   */
  public Card createCard(String customerId, String sourceToken) {
    try {
      Customer customer = Customer.retrieve(customerId);
      if (customer == null) {
        return null;
      }

      Map<String, Object> params = new HashMap<>();
      params.put("source", sourceToken);

      return (Card) customer.getSources().create(params);
    } catch (StripeException e) {
      logger.error("Error creating card to stripe", e);
    }

    return null;
  }

  /**
   * Lists customers cards from stripe
   * 
   * @param customerId customer id
   * @return List of customers cards
   */
  public List<Card> listCards(String customerId) {
    try {
      Customer customer = Customer.retrieve(customerId);
      if (customer == null) {
        return null;
      }

      Map<String, Object> params = new HashMap<>();
      params.put("object", "card");

      ExternalAccountCollection sources = customer.getSources().list(params);
      return sources.getData().stream().map(externalAccount -> (Card) externalAccount).collect(Collectors.toList());
    } catch (StripeException e) {
      logger.error("Error listing cards from stripe", e);
    }

    return null;
  }

  /**
   * Deletes card from stripe
   * 
   * @param customerId customer id
   * @param cardId card id
   * @return true if deletion was successful false otherwise
   */
  public boolean deleteCard(String customerId, String cardId) {
    try {
      Customer customer = Customer.retrieve(customerId);
      if (customer == null) {
        return false;
      }

      customer.getSources().retrieve(cardId).delete();
      return true;
    } catch (StripeException e) {
      logger.error("Error listing cards from stripe", e);
    }

    return false;
  }

  /**
   * Creates new subscription
   * 
   * @param customerId customer id
   * @param planId plan id
   * @return created subscription
   */
  public Subscription createSubscription(String customerId, String planId) {
    Map<String, Object> item = new HashMap<>();
    item.put("plan", planId);

    Map<String, Object> items = new HashMap<>();
    items.put("0", item);

    Map<String, Object> params = new HashMap<>();
    params.put("customer", customerId);
    params.put("items", items);

    try {
      return Subscription.create(params);
    } catch (StripeException e) {
      logger.error("Error creating subscription", e);
    }

    return null;
  }

  /**
   * Update customers subscription
   * 
   * @param customerId customer id
   * @param subscriptionId subscription id
   * @param cancelAtPeriodEnd cancel at the end of billing period
   * @return updated subscription
   */
  public Subscription updateSubscription(String customerId, String subscriptionId, boolean cancelAtPeriodEnd) {
    try {
      Subscription sub = Subscription.retrieve(subscriptionId);
      if (sub == null) {
        logger.error("Failed to find subscription");
        return null;
      }

      if (!sub.getCustomer().equals(customerId)) {
        logger.error("Tried to update subscription with not maching customer id. refusing...");
        return null;
      }

      Map<String, Object> updateParams = new HashMap<String, Object>();
      updateParams.put("cancel_at_period_end", cancelAtPeriodEnd);
      return sub.update(updateParams);
    } catch (StripeException e) {
      logger.error("Error updating subscription");
    }
    return null;
  }

  /**
   * Lists customers subscriptions
   * 
   * @param customerId customer id
   * @return list of customers subscriptions
   */
  public List<Subscription> listSubscriptions(String customerId) {
    try {
      Map<String, Object> params = new HashMap<String, Object>();
      params.put("customer", customerId);
      SubscriptionCollection subscriptions = Subscription.list(params);
      return subscriptions.getData();
    } catch (StripeException e) {
      logger.error("Error listing subscriptions");
    }

    return null;
  }

}