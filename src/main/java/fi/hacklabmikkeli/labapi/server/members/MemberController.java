package fi.hacklabmikkeli.labapi.server.members;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.hacklabmikkeli.labapi.server.persistence.dao.MemberDAO;
import fi.hacklabmikkeli.labapi.server.persistence.model.Member;
import fi.hacklabmikkeli.labapi.server.persistence.model.MemberStatus;

/**
 * Controller for member related operations
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class MemberController {

  @Inject
  private MemberDAO memberDAO;

  /**
   * Creates new member
   * 
   * @param userId users keycloak id
   * @param status users status
   * @param stripeCustomerId users stripe customer id (optional)
   * @param approvedAt time user was approved (optional)
   * 
   * @return created member
   */
  public Member createMember(UUID userId, MemberStatus status, String stripeCustomerId, OffsetDateTime approvedAt) {
    return memberDAO.create(userId, status, stripeCustomerId, approvedAt);
  }

  /**
   * Finds member with id
   * 
   * @param id member id
   * 
   * @return member or null if not found
   */
  public Member findMember(UUID id) {
    return memberDAO.findById(id);
  }

  /**
   * Finds member with stripe customer id
   * 
   * @param stripeCustomerId stripe customer id
   * 
   * @return member or null if not found
   */
  public Member findMemberByStripeId(String stripeCustomerId) {
    return memberDAO.findByStripeCustomerId(stripeCustomerId);
  }

  /**
   * Lists members
   *
   * @param status user status (optional)
   * @param firstResult first result to return (optional)
   * @param maxResults max number of results to return (optinal)
   * 
   * @return list of members 
   */
  public List<Member> listMembers(MemberStatus status, Long firstResult, Long maxResults) {
    if (status == null) {
      return memberDAO.listAll(firstResult, maxResults);
    }

    return memberDAO.listByStatus(status, firstResult, maxResults);
  }

  /**
   * Updates member
   * 
   * @param member member to update
   * @param stripeCustomerId new stripe customer id
   * @param status new member status
   * @param approvedAt new time user was approved
   * 
   * @return updated member
   */
  public Member updateMember(Member member, String stripeCustomerId, MemberStatus status, OffsetDateTime approvedAt) {
    memberDAO.updateStripeCustomerId(member, stripeCustomerId);
    memberDAO.updateStatus(member, status);
    memberDAO.updateApprovedAt(member, approvedAt);
    return member;
  }

  /**
   * Updates member status
   * 
   * @param member member to update
   * @param status new status
   * 
   * @return updated member
   */
  public Member updateStatus(Member member, MemberStatus status) {
    return memberDAO.updateStatus(member, status);
  }

  /**
   * Deletes member
   * 
   * @param member Member to delete 
   */
  public void deleteMember(Member member) {
    memberDAO.delete(member);
  }

}
