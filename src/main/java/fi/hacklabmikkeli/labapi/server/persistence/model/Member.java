package fi.hacklabmikkeli.labapi.server.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

/**
 * @author Heikki Kurhinen
 * 
 * JPA entity for storing members
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class Member {

  @Id
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column (nullable = true)
  private String stripeCustomerId;

  @Column (nullable = false)
  private MemberStatus status;

  @Column (nullable = false)
  private OffsetDateTime createdAt;

  @Column (nullable = true)
  private OffsetDateTime approvedAt;

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
   * @return the stripeCustomerId
   */
  public String getStripeCustomerId() {
    return stripeCustomerId;
  }

  /**
   * @param stripeCustomerId the stripeCustomerId to set
   */
  public void setStripeCustomerId(String stripeCustomerId) {
    this.stripeCustomerId = stripeCustomerId;
  }

  /**
   * @return the status
   */
  public MemberStatus getStatus() {
    return status;
  }

  /**
   * @param status the status to set
   */
  public void setStatus(MemberStatus status) {
    this.status = status;
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
   * @return the approvedAt
   */
  public OffsetDateTime getApprovedAt() {
    return approvedAt;
  }

  /**
   * @param approvedAt the approvedAt to set
   */
  public void setApprovedAt(OffsetDateTime approvedAt) {
    this.approvedAt = approvedAt;
  }

  @PrePersist
  public void onCreate() {
    setCreatedAt(OffsetDateTime.now());
  }
}
