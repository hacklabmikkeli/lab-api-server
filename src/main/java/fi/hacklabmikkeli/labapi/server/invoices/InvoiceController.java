package fi.hacklabmikkeli.labapi.server.invoices;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.hacklabmikkeli.labapi.server.persistence.dao.InvoiceDAO;
import fi.hacklabmikkeli.labapi.server.persistence.model.Invoice;

/**
 * Controller for invoice related operations
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class InvoiceController {

  @Inject
  private InvoiceDAO invoiceDAO;

  /**
   * Creates new invoice
   * @param amount invoice amount
   * @param due invoices due date 
   * @param message message to the recipient
   * @param paid is invoice paid
   * @param userId id of user the invoice is targetet to 
   * 
   * @return created invoice
   */
  public Invoice createInvoice(long amount, OffsetDateTime due, String message, boolean paid, UUID userId) {
    return invoiceDAO.create(UUID.randomUUID(), amount, due, message, paid, userId);
  }

  /**
   * Finds invoice with id
   * @param id invoice id
   * 
   * @return invoice or null if not found
   */
  public Invoice findInvoice(UUID id) {
    return invoiceDAO.findById(id);
  }

  /**
   * Lists invoices
   * @param userId user id (optional)
   * @param firstResult first result to return (optional)
   * @param maxResults max number of results to return (optinal)
   * 
   * @return list of invoices 
   */
  public List<Invoice> listInvoices(UUID userId, Long firstResult, Long maxResults) {
    if (userId == null) {
      return invoiceDAO.listInvoicesOrderCreatedDesc(firstResult, maxResults);
    }

    return invoiceDAO.listByUserId(userId, firstResult, maxResults);
  }

  /**
   * Updates invoice
   * @param invoice invoice to update
   * @param amount new amount
   * @param due new due date
   * @param message new message
   * @param paid new paid
   * @param userId new user id
   */
  public Invoice updateInvoice(Invoice invoice, long amount, OffsetDateTime due, String message, boolean paid, UUID userId) {
    invoiceDAO.updateAmount(invoice, amount);
    invoiceDAO.updateDue(invoice, due);
    invoiceDAO.updateMessage(invoice, message);
    invoiceDAO.updatePaid(invoice, paid);
    invoiceDAO.updateUserId(invoice, userId);
    return invoice;
  }

  /**
   * Deletes invoice
   * @param invoice Invoice to delete 
   */
  public void deleteInvoice(Invoice invoice) {
    invoiceDAO.delete(invoice);
  }

}
