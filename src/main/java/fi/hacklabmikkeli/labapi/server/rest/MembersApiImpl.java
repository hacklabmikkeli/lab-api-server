package fi.hacklabmikkeli.labapi.server.rest;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.StringUtils;

import fi.hacklabmikkeli.labapi.server.keycloak.KeycloakAdminController;
import fi.hacklabmikkeli.labapi.server.members.MemberController;
import fi.hacklabmikkeli.labapi.server.persistence.model.MemberStatus;
import fi.hacklabmikkeli.labapi.server.rest.model.Member;
import fi.hacklabmikkeli.labapi.server.rest.model.Member.StatusEnum;
import fi.hacklabmikkeli.labapi.server.rest.model.MemberAction;
import fi.hacklabmikkeli.labapi.server.rest.translate.MemberTranslator;
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
  private StripeController stripeController;

  @Inject
  private KeycloakAdminController keycloakAdminController;

  @Override
  public Response createMember(@Valid Member payload) throws Exception {
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
  public Response findMember(UUID memberId) throws Exception {
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
  public Response listMembers(String status, Long firstResult, Long maxResults) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    MemberStatus memberStatus = EnumUtils.getEnum(MemberStatus.class, status);
    return createOk(memberTranslator.translateMembers(memberController.listMembers(memberStatus, firstResult, maxResults)));
  }

  @Override
  public Response updateMember(UUID memberId, @Valid Member payload) throws Exception {
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
  public Response deleteMember(UUID memberId) throws Exception {
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
  public Response createMemberAction(UUID memberId, @Valid MemberAction payload) throws Exception {
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
        keycloakAdminController.addRealmRole(userId, "member");
        String stripeCustomerId = stripeController.createCustomer(getLoggerUserEmail(), userId.toString()); 
        memberController.updateMember(memberEntity, stripeCustomerId, MemberStatus.MEMBER, OffsetDateTime.now());
      break;
      default:
        return createBadRequest("Unkown user action");

    }

    return createNoContent();
  }
}