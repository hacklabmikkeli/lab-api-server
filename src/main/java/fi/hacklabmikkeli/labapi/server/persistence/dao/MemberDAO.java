package fi.hacklabmikkeli.labapi.server.persistence.dao;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.apache.commons.lang3.StringUtils;

import fi.hacklabmikkeli.labapi.server.persistence.model.Member;
import fi.hacklabmikkeli.labapi.server.persistence.model.MemberStatus;
import fi.hacklabmikkeli.labapi.server.persistence.model.Member_;

/**
 * @author Heikki Kurhinen
 * 
 * DAO class for Member entity
 */
@ApplicationScoped
public class MemberDAO extends AbstractDAO<Member> {

  /**
   * Creates and persists new member entity
   * 
   * @param id id users keycloak id
   * @param status member status
   * @param stripeCustomerId customer id in stripe (optional)
   * @param approvedAt time the user was approved (optional)
   * 
   * @return created Member entity
   */
  public Member create(UUID id, MemberStatus status, String stripeCustomerId, OffsetDateTime approvedAt) {
    Member member = new Member();

    member.setId(id);
    member.setStatus(status);
    member.setStripeCustomerId(stripeCustomerId);
    member.setApprovedAt(approvedAt);

    return persist(member);
  }

  /**
   * Finds member by stripe customer id
   * 
   * @param stripeCustomerId stripe customer id
   * 
   * @return member of null if not found
   */
  public Member findByStripeCustomerId(String stripeCustomerId) {
    if (StringUtils.isBlank(stripeCustomerId)) {
      return null;
    }

    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Member> criteria = criteriaBuilder.createQuery(Member.class);
    Root<Member> root = criteria.from(Member.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Member_.stripeCustomerId), stripeCustomerId));

    TypedQuery<Member> query = entityManager.createQuery(criteria);

    return query.getSingleResult();
  }

  /**
   * List members by status
   * 
   * @param status status to filter members by
   * @param firstResult first result to return (optional)
   * @param maxResults  max number of results to return (optional)
   * 
   * @return List of members filtered by status. Members are order by descending created date
   */
  public List<Member> listByStatus(MemberStatus status, Long firstResult, Long maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Member> criteria = criteriaBuilder.createQuery(Member.class);
    Root<Member> root = criteria.from(Member.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Member_.status), status));
    criteria.orderBy(criteriaBuilder.desc(root.get(Member_.createdAt)));

    TypedQuery<Member> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult.intValue());
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults.intValue());
    }

    return query.getResultList();
  }

  /**
   * Updates members status
   * 
   * @param member member to update
   * @param status new member status
   * 
   * @return updated member
   */
  public Member updateStatus(Member member, MemberStatus status) {
    member.setStatus(status);
    return persist(member);
  }

  /**
   * Updates members stripe customer id
   * 
   * @param member member to update
   * @param stripeCustomerId new stripe customer id
   * 
   * @return updated member
   */
  public Member updateStripeCustomerId(Member member, String stripeCustomerId) {
    member.setStripeCustomerId(stripeCustomerId);
    return persist(member);
  }

  /**
   * Updates members approved at
   * 
   * @param member member to update
   * @param approvedAt new approved at
   * 
   * @return updated member
   */
  public Member updateApprovedAt(Member member, OffsetDateTime approvedAt) {
    member.setApprovedAt(approvedAt);
    return persist(member);
  }
}
