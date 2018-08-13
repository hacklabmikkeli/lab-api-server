package fi.hacklabmikkeli.labapi.server.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * @author Heikki Kurhinen
 * 
 * JPA entity for storing invoices
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Invoice {

  @Id
  @Type(type="org.hibernate.type.PostgresUUIDType")
  private UUID id;

  @Type(type="org.hibernate.type.PostgresUUIDType")
  @Column (nullable = false)
  private UUID userId;

  @Lob
  @Column (nullable = true)
  private String message;

  @Column (nullable = false)
  private long amount;

  @Column (nullable = false)
  private boolean paid;

  @Column (nullable = false)
  private OffsetDateTime due;

  @Column (nullable = false)
  private OffsetDateTime createdAt;

  @Column (nullable = false)
  private OffsetDateTime modifiedAt;

  /**
   * @return the id
   */
  public UUID getId() {
    return id;
  }

  /**
   * @param id the id to set
   */
  public void setId(UUID id) {
    this.id = id;
  }

  /**
   * @return the userId
   */
  public UUID getUserId() {
    return userId;
  }

  /**
   * @param userId the userId to set
   */
  public void setUserId(UUID userId) {
    this.userId = userId;
  }

  /**
   * @return the message
   */
  public String getMessage() {
    return message;
  }

  /**
   * @param message the message to set
   */
  public void setMessage(String message) {
    this.message = message;
  }

  /**
   * @return the amount
   */
  public long getAmount() {
    return amount;
  }

  /**
   * @param amount the amount to set
   */
  public void setAmount(long amount) {
    this.amount = amount;
  }

  /**
   * @return the paid
   */
  public boolean isPaid() {
    return paid;
  }

  /**
   * @param paid the paid to set
   */
  public void setPaid(boolean paid) {
    this.paid = paid;
  }

  /**
   * @return the due
   */
  public OffsetDateTime getDue() {
    return due;
  }

  /**
   * @param due the due to set
   */
  public void setDue(OffsetDateTime due) {
    this.due = due;
  }

  /**
   * @return the createdAt
   */
  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  /**
   * @param createdAt the createdAt to set
   */
  public void setCreatedAt(OffsetDateTime createdAt) {
    this.createdAt = createdAt;
  }

  /**
   * @return the modifiedAt
   */
  public OffsetDateTime getModifiedAt() {
    return modifiedAt;
  }

  /**
   * @param modifiedAt the modifiedAt to set
   */
  public void setModifiedAt(OffsetDateTime modifiedAt) {
    this.modifiedAt = modifiedAt;
  }

  @PrePersist
  public void onCreate() {
    setCreatedAt(OffsetDateTime.now());
    setModifiedAt(OffsetDateTime.now());
  }
  
  @PreUpdate
  public void onUpdate() {
    setModifiedAt(OffsetDateTime.now());
  }
  
}
