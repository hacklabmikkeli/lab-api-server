package fi.hacklabmikkeli.labapi.server.rest;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import fi.hacklabmikkeli.labapi.server.invoices.InvoiceController;
import fi.hacklabmikkeli.labapi.server.rest.model.Invoice;
import fi.hacklabmikkeli.labapi.server.rest.translate.InvoiceTranslator;

/**
 * Invoices REST service implementation
 * 
 * @author Heikki Kurhinen
 */
@RequestScoped
@Stateful
public class InvoicesApiImpl extends AbstractApi implements InvoicesApi {

  @Inject
  private InvoiceController invoiceController;

  private InvoiceTranslator invoiceTranslator;

  @Override
  public Response createInvoice(@Valid Invoice payload) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    long amount = payload.getAmount();
    OffsetDateTime due = payload.getDue();
    String message = payload.getMessage();
    boolean paid = payload.isPaid();
    UUID userId = payload.getUserId();

    return createOk(invoiceTranslator.translateInvoice(invoiceController.createInvoice(amount, due, message, paid, userId)));
  }

  @Override
  public Response deleteInvoice(UUID invoiceId) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Invoice invoiceEntity = invoiceController.findInvoice(invoiceId);
    if (invoiceEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    invoiceController.deleteInvoice(invoiceEntity);
    return createNoContent();
  }

  @Override
  public Response findInvoice(UUID invoiceId) throws Exception {
    fi.hacklabmikkeli.labapi.server.persistence.model.Invoice invoiceEntity = invoiceController.findInvoice(invoiceId);
    if (invoiceEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    if (!isRealmAdmin() && !invoiceEntity.getUserId().equals(getLoggerUserId())) {
      return createForbidden(UNAUTHORIZED);
    }

    return createOk(invoiceTranslator.translateInvoice(invoiceEntity));
  }

  @Override
  public Response listInvoices(UUID userId, Long firstResult, Long maxResults) throws Exception {
    if (!isRealmAdmin() && (userId == null || !userId.equals(getLoggerUserId()))) {
      return createForbidden(UNAUTHORIZED);
    }

    return createOk(invoiceTranslator.translateInvoices(invoiceController.listInvoices(userId, firstResult, maxResults)));
  }

  @Override
  public Response updateInvoice(UUID invoiceId, @Valid Invoice payload) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Invoice invoiceEntity = invoiceController.findInvoice(invoiceId);
    if (invoiceEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    long amount = payload.getAmount();
    OffsetDateTime due = payload.getDue();
    String message = payload.getMessage();
    boolean paid = payload.isPaid();
    UUID userId = payload.getUserId();

    return createOk(invoiceTranslator.translateInvoice(invoiceController.updateInvoice(invoiceEntity, amount, due, message, paid, userId)));
  }

}