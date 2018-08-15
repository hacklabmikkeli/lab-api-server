package fi.hacklabmikkeli.labapi.server.stripe;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;

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

  public String createCustomer(String email, String keycloakUserId) {
    Map<String, Object> customerParams = new HashMap<String, Object>();
    customerParams.put("email", email);

    Map<String, Object> customerMeta = new HashMap<String, Object>();
    customerMeta.put("keycloak_id", keycloakUserId);
    customerParams.put("metadata", customerMeta);

    try {
      Customer customer = Customer.create(customerParams);
      return customer.getId();
    } catch (StripeException e) {
      logger.error("Error creating stripe customer", e);
    }

    return null;
  }

}