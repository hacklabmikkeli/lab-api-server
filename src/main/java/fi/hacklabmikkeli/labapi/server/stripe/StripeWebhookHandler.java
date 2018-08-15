package fi.hacklabmikkeli.labapi.server.stripe;

import java.io.IOException;
import java.util.stream.Collectors;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;

import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.net.ApiResource;
import com.stripe.net.Webhook;

import org.slf4j.Logger;

import fi.hacklabmikkeli.labapi.server.keycloak.KeycloakAdminController;
import fi.hacklabmikkeli.labapi.server.members.MemberController;
import fi.hacklabmikkeli.labapi.server.persistence.model.Member;
import fi.hacklabmikkeli.labapi.server.persistence.model.MemberStatus;
import fi.hacklabmikkeli.labapi.server.settings.SystemSettingController;
import fi.hacklabmikkeli.labapi.server.webhooks.WebhookException;
import fi.hacklabmikkeli.labapi.server.webhooks.WebhookHandler;

/**
 * Class that handles webhooks from stripe
 * 
 * @author Heikki Kurhinen
 */
@RequestScoped
public class StripeWebhookHandler implements WebhookHandler {

  @Inject
  private MemberController memberController;

  @Inject
  private SystemSettingController systemSettingController;

  @Inject
  private KeycloakAdminController keycloakAdminController;

  @Inject
  private Logger logger;

  @Override
  public String getType() {
    return StripeConsts.STRIPE_WEBHOOK_TYPE;
  }

  @Override
  public void handle(HttpServletRequest request) throws WebhookException {
    String payload = null;
    String signatureHeader = request.getHeader(StripeConsts.STRIPE_SIGNATURE_HEADER);
    try {
      payload = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
    } catch (IOException e) {
      throw new WebhookException(e);
    }

    Event event = null;
    try {
      event = Webhook.constructEvent(payload, signatureHeader, systemSettingController.getSettingValue(StripeConsts.STRIPE_SIGNING_KEY_SETTING));
    } catch (SignatureVerificationException e) {
      throw new WebhookException(e);
    }

    if (inLiveMode() != event.getLivemode()) {
      throw new WebhookException("Operation mode mismatch");
    }

    switch(event.getType()) {
      case "customer.subscription.created":
        handleCustomSubscriptionCreated(event);
        return;
      case "customer.subscription.deleted":
      case "customer.subscription.updated":
        handleCustomSubscriptionUpdated(event);
        return;
      default:
        logger.info("received webhook from stripe with type {}", event.getType());
    }
  }

  /**
   * Handles webhook with type customer.subscription.created
   * 
   * @param event webhook event
   */
  private void handleCustomSubscriptionCreated(Event event) {
    StripeObject data = event.getData().getObject();
    Subscription subscription = ApiResource.GSON.fromJson(data.toJson(), Subscription.class);
    String stripeCustomerId = subscription.getCustomer();
    Member member = memberController.findMemberByStripeId(stripeCustomerId);
    if (member == null) {
      logger.error("Received customer.subscription.created with customer id {} but member was not found", stripeCustomerId);
      return;
    }

    keycloakAdminController.addRealmRole(member.getId(), "spaceuser");
    memberController.updateStatus(member, MemberStatus.SPACE_USER);
  }

  /**
   * Handles webhook with type customer.subscription.updated
   * 
   * @param event webhook event
   */
  private void handleCustomSubscriptionUpdated(Event event) {
    StripeObject data = event.getData().getObject();
    Subscription subscription = ApiResource.GSON.fromJson(data.toJson(), Subscription.class);
    String status = subscription.getStatus();
    if (!"canceled".equals(status) && !"unpaid".equals(status)) {
      return;
    }

    String stripeCustomerId = subscription.getCustomer();
    Member member = memberController.findMemberByStripeId(stripeCustomerId);
    if (member == null) {
      logger.error("Received customer.subscription.updated with customer id {} and status {} but member was not found", stripeCustomerId, status);
      return;
    }

    keycloakAdminController.removeRealmRole(member.getId(), "spaceuser");
    memberController.updateStatus(member, MemberStatus.MEMBER);
  }

  /**
   * Returns if stripe has been configured to run in live mode or not
   */
  private boolean inLiveMode() {
    return systemSettingController.getSettingValueBoolean(StripeConsts.STRIPE_LIVE_MODE_SETTING);
  }

}