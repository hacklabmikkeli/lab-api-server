package fi.hacklabmikkeli.labapi.server.rest.translate;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import fi.hacklabmikkeli.labapi.server.rest.model.Card;
import fi.hacklabmikkeli.labapi.server.rest.model.Plan;
import fi.hacklabmikkeli.labapi.server.rest.model.Product;
import fi.hacklabmikkeli.labapi.server.rest.model.Subscription;

/**
 * Translates Stripe entites to lab api REST entities
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class StripeTranslator extends AbstractTranslator {


  /**
   * Translates Stripe product into lab api product rest entity
   * 
   * @param productModel Stripe product
   * @return translated product rest entity
   */
  public Product translateProduct(com.stripe.model.Product productModel) {
    if (productModel == null) {
      return null;
    }

    Product result = new Product();
    result.setId(productModel.getId());
    result.setName(productModel.getName());

    return result;
  }

  /**
   * Translates list of Stripe products into lab api rest entities
   * 
   * @param productModels List of stripe products
   * @return list of translated lab api rest entities
   */
  public List<Product> translateProducts(List<com.stripe.model.Product> productModels) {
    return productModels.stream().map(this::translateProduct).collect(Collectors.toList());
  }

  /**
   * Translates Stripe plan into lab api rest plan
   * 
   * @param planModel Stripe plan
   * @return translated lab api rest plan
   */
  public Plan translatePlan(com.stripe.model.Plan planModel) {
    if (planModel == null) {
      return null;
    }

    Plan result = new Plan();
    result.setId(planModel.getId());
    result.setAmount(translateAmount(planModel.getAmount()));
    result.setInterval(planModel.getInterval());
    result.setIntervalCount(planModel.getIntervalCount());

    return result;
  }

  /**
   * Translates list of Stripe plans into list of lab api rest plans
   * 
   * @param planModels list of stripe plans
   * @return List of translated lab api rest plans
   */
  public List<Plan> translatePlans(List<com.stripe.model.Plan> planModels) {
    return planModels.stream().map(this::translatePlan).collect(Collectors.toList());
  }

  /**
   * Translates Stripe card into lab api rest card
   * 
   * @param cardModel Stripe card
   * @return translated lab api rest card
   */
  public Card translateCard(com.stripe.model.Card cardModel) {
    if (cardModel == null) {
      return null;
    }

    Card result = new Card();
    result.setBrand(cardModel.getBrand());
    result.setId(cardModel.getId());
    result.setLast4(cardModel.getLast4());
    
    return result;
  }

  /**
   * Translates list of Stripe cards into lab api rest cards
   * 
   * @param cardModels List of Stripe cards
   * @return List of translated lab api rest cards
   */
  public List<Card> translateCards(List<com.stripe.model.Card> cardModels) {
    return cardModels.stream().map(this::translateCard).collect(Collectors.toList());
  }

  /**
   * Translates Stripe subscription into lab api rest subscription
   * 
   * @param subscriptionModel Stripe subscription model
   * @return translated lap api rest subscription
   */
  public Subscription translateSubscription(com.stripe.model.Subscription subscriptionModel) {
    Subscription result = new Subscription();
    result.setCancelAtPeriodEnd(subscriptionModel.getCancelAtPeriodEnd());
    result.setId(subscriptionModel.getId());
    result.setPlanId(subscriptionModel.getPlan().getId());
    
    return result;
  }

  /**
   * Translates list of Stripe subscriptions into lab api rest subcscriptions
   * 
   * @param subscriptionModels list of String subsscriptions
   * @return list of translated lab api rest subscriptions
   */
  public List<Subscription> translateSubscriptions(List<com.stripe.model.Subscription> subscriptionModels) {
    return subscriptionModels.stream().map(this::translateSubscription).collect(Collectors.toList());
  }

  /**
   * Converts amount in cents into string formatted with 2 decimals
   * 
   * @param amount amount in cents
   * @return amount as string formatted with 2 decimals
   */
  private String translateAmount(Long amount) {
    if (amount == null) {
      return "0.00";
    }

    DecimalFormat df = new DecimalFormat("#.00");
    return df.format((amount / 100d));
  }

}