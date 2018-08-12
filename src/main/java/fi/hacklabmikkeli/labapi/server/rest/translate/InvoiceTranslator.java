package fi.hacklabmikkeli.labapi.server.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import fi.hacklabmikkeli.labapi.server.rest.model.Invoice;

/**
 * Translates Invoice related entites to REST entities
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class InvoiceTranslator extends AbstractTranslator {

  /**
   * Translates Invoice entity to Invoice REST model
   * 
   * @param invoiceEntity Invoice JPA entity
   * @return translated Invoice REST model
   */
  public Invoice translateInvoice(fi.hacklabmikkeli.labapi.server.persistence.model.Invoice invoiceEntity) {
    Invoice invoice = new Invoice();
    invoice.setId(invoiceEntity.getId());
    invoice.setAmount(invoiceEntity.getAmount());
    invoice.setCreated(invoiceEntity.getCreatedAt());
    invoice.setDue(invoiceEntity.getDue());
    invoice.setMessage(invoiceEntity.getMessage());
    invoice.setPaid(invoiceEntity.isPaid());
    invoice.setUserId(invoiceEntity.getUserId());
    return invoice;
  }

  /**
   * Tranlastes list of Invoice entities into Invoice REST model
   *
   * @param invoiceEntities Invoice JPA entities 
   *  
   * @return list of translated Invoice REST models
   */
  public List<Invoice> translateInvoices(List<fi.hacklabmikkeli.labapi.server.persistence.model.Invoice> invoiceEntities) {
    return invoiceEntities.stream().map(this::translateInvoice).collect(Collectors.toList());
  }
}