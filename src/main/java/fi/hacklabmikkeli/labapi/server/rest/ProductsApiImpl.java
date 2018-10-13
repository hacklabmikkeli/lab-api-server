package fi.hacklabmikkeli.labapi.server.rest;

import java.util.List;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import fi.hacklabmikkeli.labapi.server.rest.translate.StripeTranslator;
import fi.hacklabmikkeli.labapi.server.stripe.StripeController;

/**
 * Products REST service implementation
 * 
 * @author Heikki Kurhinen
 */
@RequestScoped
@Stateful
public class ProductsApiImpl extends AbstractApi implements ProductsApi {

  @Inject
  private StripeTranslator stripeTranslator;

  @Inject
  private StripeController stripeController;

  @Override
  public Response listProductPlans(String productId, Long firstResult, Long maxResults) {
    List<com.stripe.model.Plan> planModels = stripeController.listPlans(productId);
    if (planModels == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(stripeTranslator.translatePlans(planModels));
  }

  @Override
  public Response listProducts(Long firstResult, Long maxResults) {
    List<com.stripe.model.Product> productModels = stripeController.listProducts();
    if (productModels == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(stripeTranslator.translateProducts(productModels));
  }

}