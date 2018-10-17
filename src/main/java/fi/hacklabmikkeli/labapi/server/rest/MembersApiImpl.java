package fi.hacklabmikkeli.labapi.server.rest;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import com.stripe.model.Customer;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import fi.hacklabmikkeli.labapi.server.keycloak.KeycloakAdminController;
import fi.hacklabmikkeli.labapi.server.members.MemberController;
import fi.hacklabmikkeli.labapi.server.persistence.model.MemberStatus;
import fi.hacklabmikkeli.labapi.server.rest.model.Card;
import fi.hacklabmikkeli.labapi.server.rest.model.Member;
import fi.hacklabmikkeli.labapi.server.rest.model.Member.StatusEnum;
import fi.hacklabmikkeli.labapi.server.rest.model.MemberAction;
import fi.hacklabmikkeli.labapi.server.rest.model.Subscription;
import fi.hacklabmikkeli.labapi.server.rest.translate.MemberTranslator;
import fi.hacklabmikkeli.labapi.server.rest.translate.StripeTranslator;
import fi.hacklabmikkeli.labapi.server.stripe.StripeController;

/**
 * Members REST service implementation
 * 
 * @author Heikki Kurhinen
 */
@RequestScoped
@Stateful
public class MembersApiImpl extends AbstractApi implements MembersApi {

  @Inject
  private MemberController memberController;

  @Inject
  private MemberTranslator memberTranslator;

  @Inject
  private StripeTranslator stripeTranslator;

  @Inject
  private StripeController stripeController;

  @Inject
  private KeycloakAdminController keycloakAdminController;

  @Override
  public Response createMember(@Valid Member payload) {
    if (!isRealmAdmin() && payload.getStatus() != StatusEnum.PENDING) {
      return createForbidden(UNAUTHORIZED);
    }

    UUID userId = payload.getId();
    if (userId == null) {
      userId = getLoggerUserId();
    }

    if (!isRealmAdmin() && !userId.equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    MemberStatus status = translateEnum(MemberStatus.class, payload.getStatus());
    String stripeCustomerId = payload.getStripeCustomerId();
    OffsetDateTime approvedAt = payload.getApproved();

    if (status != MemberStatus.PENDING && StringUtils.isBlank(stripeCustomerId)) {
      return createBadRequest("Approved members must have valid stripe customer id");
    }

    if (status == MemberStatus.SPACE_USER) {
      //TODO: check from stripe that user has valid payment source, create bad request if not
    }

    return createOk(memberTranslator.translateMember(memberController.createMember(userId, status, stripeCustomerId, approvedAt)));
  }

  @Override
  public Response findMember(UUID memberId) {
    if (!isRealmAdmin() && !memberId.equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    return createOk(memberTranslator.translateMember(memberEntity));
  }

  @Override
  public Response listMembers(String status, Long firstResult, Long maxResults) {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    MemberStatus memberStatus = EnumUtils.getEnum(MemberStatus.class, status);
    return createOk(memberTranslator.translateMembers(memberController.listMembers(memberStatus, firstResult, maxResults)));
  }

  @Override
  public Response updateMember(UUID memberId, @Valid Member payload) {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    StatusEnum status = payload.getStatus();
    String stripeCustomerId = payload.getStripeCustomerId();
    OffsetDateTime approvedAt = payload.getApproved();

    return createOk(memberTranslator.translateMember(memberController.updateMember(memberEntity, stripeCustomerId, translateEnum(MemberStatus.class, status), approvedAt)));
  }

  @Override
  public Response deleteMember(UUID memberId) {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    } 

    memberController.deleteMember(memberEntity);
    return createNoContent();
  }

  @Override
  public Response createMemberAction(UUID memberId, @Valid MemberAction payload) {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (memberEntity.getStatus() == MemberStatus.LEFT) {
      createBadRequest("User has left the association");
    }

    switch (payload.getType()) {
      case APPROVE:
        if (memberEntity.getStatus() != MemberStatus.PENDING) {
          return createBadRequest("User is already approved");
        }

        UUID userId = memberEntity.getId();
        Customer stripeCustomer = stripeController.createCustomer(getLoggerUserEmail(), userId.toString());
        if (stripeCustomer == null) {
          return createInternalServerError("Unable to create stripe customer");
        }

        keycloakAdminController.addRealmRole(userId, "member");
        memberController.updateMember(memberEntity, stripeCustomer.getId(), MemberStatus.MEMBER, OffsetDateTime.now());
      break;
      default:
        return createBadRequest("Unkown user action");

    }

    return createNoContent();
  }

  @Override
  public Response createMemberCard(UUID memberId, @Valid Card payload) {
    if (payload.getSourceToken() == null) {
      return createBadRequest("Cards can only be created by providing source token issued by stripe");
    }

    if (!memberId.equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (memberEntity.getStripeCustomerId() == null) {
      return createBadRequest("Cannot create card without stripe customer id");
    }

    com.stripe.model.Card cardModel = stripeController.createCard(memberEntity.getStripeCustomerId(), payload.getSourceToken());
    if (cardModel == null) {
      return createInternalServerError("Error creating card");
    }

    return createOk(stripeTranslator.translateCard(cardModel));
  }

  @Override
  public Response createMemberSubscription(UUID memberId, @Valid Subscription payload) {
    if (!memberId.equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (memberEntity.getStripeCustomerId() == null) {
      return createBadRequest("Cannot create subscription without stripe customer id");
    }

    com.stripe.model.Subscription subscription = stripeController.createSubscription(memberEntity.getStripeCustomerId(), payload.getPlanId());
    
    if (subscription == null) {
      return createInternalServerError("Error creating subscription");
    }

    return createOk(stripeTranslator.translateSubscription(subscription));
  }

  @Override
  public Response deleteMemberCard(UUID memberId, String cardId) {
    if (!memberId.equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (memberEntity.getStripeCustomerId() == null) {
      return createBadRequest("Cannot create card without stripe customer id");
    }

    boolean deleted = stripeController.deleteCard(memberEntity.getStripeCustomerId(), cardId);
    if (deleted) {
      return createNoContent();
    }

    return createInternalServerError("Error deleting card");
  }

  @Override
  public Response findMemberCards(UUID memberId) {
    if (!memberId.equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (memberEntity.getStripeCustomerId() == null) {
      return createBadRequest("Cannot list cards without stripe customer id");
    }

    List<com.stripe.model.Card> cardModels = stripeController.listCards(memberEntity.getStripeCustomerId());
    if (cardModels == null) {
      return createInternalServerError("Error listing cards");
    }

    return createOk(stripeTranslator.translateCards(cardModels));
  }

  @Override
  public Response findMemberSubscriptions(UUID memberId) {
    if (!memberId.equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (memberEntity.getStripeCustomerId() == null) {
      return createBadRequest("Cannot list subscriptions without stripe customer id");
    }

    List<com.stripe.model.Subscription> subscriptions = stripeController.listSubscriptions(memberEntity.getStripeCustomerId());
    if (subscriptions == null) {
      return createInternalServerError("Error listing subscriptions");
    }

    return createOk(stripeTranslator.translateSubscriptions(subscriptions));
  }

  @Override
  public Response updateMemberSubscription(UUID memberId, String subscriptionId, @Valid Subscription payload) {
    if (!memberId.equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity = memberController.findMember(memberId);
    if (memberEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (memberEntity.getStripeCustomerId() == null) {
      return createBadRequest("Cannot update subscription without stripe customer id");
    }

    com.stripe.model.Subscription subscription = stripeController.updateSubscription(memberEntity.getStripeCustomerId(), subscriptionId, payload.CancelAtPeriodEnd());
    if (subscription == null) {
      return createInternalServerError("Error updating subscription");
    }

    return createOk(stripeTranslator.translateSubscription(subscription));
  }
}