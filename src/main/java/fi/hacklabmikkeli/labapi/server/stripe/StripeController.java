package fi.hacklabmikkeli.labapi.server.stripe;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.Plan;
import com.stripe.model.PlanCollection;
import com.stripe.model.Product;
import com.stripe.model.ProductCollection;

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

  public Customer createCustomer(String email, String keycloakUserId) {
    Map<String, Object> customerParams = new HashMap<String, Object>();
    customerParams.put("email", email);

    Map<String, Object> customerMeta = new HashMap<String, Object>();
    customerMeta.put("keycloak_id", keycloakUserId);
    customerParams.put("metadata", customerMeta);

    try {
      return Customer.create(customerParams);
    } catch (StripeException e) {
      logger.error("Error creating stripe customer", e);
    }

    return null;
  }

  public List<Product> listProducts() {
    Map<String, Object> productParams = new HashMap<String, Object>();
    productParams.put("active", "true");

    try {
      ProductCollection products = Product.list(productParams);
      return products.getData();
    } catch (StripeException e) {
      logger.error("Error listing products from stripe", e);
    }

    return null;
  }

  public List<Plan> listPlans(String productId) {
    if (productId == null) {
      logger.error("Listing plans without product id is not supported");
      return null;
    }

    Map<String, Object> planParams = new HashMap<String, Object>();
    planParams.put("product", productId);

    try {
      PlanCollection plans = Plan.list(planParams);
      return plans.getData();
    } catch (StripeException e) {
      logger.error("Error listing plans from stripe", e);
    }

    return null;
  }

}