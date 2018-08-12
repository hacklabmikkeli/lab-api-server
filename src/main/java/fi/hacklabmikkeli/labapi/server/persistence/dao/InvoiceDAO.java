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

import fi.hacklabmikkeli.labapi.server.persistence.model.Invoice;
import fi.hacklabmikkeli.labapi.server.persistence.model.Invoice_;

/**
 * @author Heikki Kurhinen
 * 
 * DAO class for Invoice entity
 */
@ApplicationScoped
public class InvoiceDAO extends AbstractDAO<Invoice> {

  /**
   * Creates and persists new invoice entity
   * 
   * @param id id
   * @param amount amount in smallest possible currency unit
   * @param due due date
   * @param message message to receiver
   * @param paid is invoice paid
   * @param userId user that invoice is targetet towards
   * 
   * @return created Invoice entity
   */
  public Invoice create(UUID id, long amount, OffsetDateTime due, String message, boolean paid, UUID userId) {
    Invoice invoice = new Invoice();

    invoice.setId(id);
    invoice.setAmount(amount);
    invoice.setDue(due);
    invoice.setMessage(message);
    invoice.setPaid(paid);
    invoice.setUserId(userId);

    return persist(invoice);
  }

  /**
   * List invoices ordered by createdAt Desc
   * 
   * @param firstResult first result to return (optional)
   * @param maxResults  max number of results to return (optional)
   * 
   * @return List of invoices invoices are order by descending created date
   */
  public List<Invoice> listInvoicesOrderCreatedDesc(Long firstResult, Long maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Invoice> criteria = criteriaBuilder.createQuery(Invoice.class);
    Root<Invoice> root = criteria.from(Invoice.class);
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.desc(root.get(Invoice_.createdAt)));

    TypedQuery<Invoice> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult.intValue());
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults.intValue());
    }

    return query.getResultList();
  }

  /**
   * List invoices by userId
   * 
   * @param userId User id to filter invoices by
   * @param firstResult first result to return (optional)
   * @param maxResults  max number of results to return (optional)
   * 
   * @return List of invoices filtered by userId invoices are order by descending created date
   */
  public List<Invoice> listByUserId(UUID userId, Long firstResult, Long maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Invoice> criteria = criteriaBuilder.createQuery(Invoice.class);
    Root<Invoice> root = criteria.from(Invoice.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(Invoice_.userId), userId));
    criteria.orderBy(criteriaBuilder.desc(root.get(Invoice_.createdAt)));

    TypedQuery<Invoice> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult.intValue());
    }
    
    if (maxResults != null) {
      query.setMaxResults(maxResults.intValue());
    }

    return query.getResultList();
  }

  /**
   * Updates invoices amount
   * 
   * @param invoice invoice to update
   * @param amount new amount
   * @return updated invoice
   */
  public Invoice updateAmount(Invoice invoice, long amount) {
    invoice.setAmount(amount);
    return persist(invoice);
  }

  /**
   * Updates invoices due date
   * 
   * @param invoice invoice to update
   * @param due new due date 
   * @return updated invoice
   */
  public Invoice updateDue(Invoice invoice, OffsetDateTime due) {
    invoice.setDue(due);
    return persist(invoice);
  }

  /**
   * Updates invoices message
   * 
   * @param invoice invoice to update
   * @param message new message
   * @return updated invoice
   */
  public Invoice updateMessage(Invoice invoice, String message) {
    invoice.setMessage(message);
    return persist(invoice);
  }

  /**
   * Updates invoices paid status 
   * 
   * @param invoice invoice to update
   * @param paid new paid status
   * @return updated invoice
   */
  public Invoice updatePaid(Invoice invoice, boolean paid) {
    invoice.setPaid(paid);
    return persist(invoice);
  }

  /**
   * Updates invoices user id
   * 
   * @param invoice invoice to update
   * @param userId new user id 
   * @return updated invoice
   */
  public Invoice updateUserId(Invoice invoice, UUID userId) {
    invoice.setUserId(userId);
    return persist(invoice);
  }

}
