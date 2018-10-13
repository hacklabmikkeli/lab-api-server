package fi.hacklabmikkeli.labapi.server.rest.translate;

import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import fi.hacklabmikkeli.labapi.server.rest.model.Member;
import fi.hacklabmikkeli.labapi.server.rest.model.Plan;
import fi.hacklabmikkeli.labapi.server.rest.model.Product;
import fi.hacklabmikkeli.labapi.server.rest.model.Member.StatusEnum;

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