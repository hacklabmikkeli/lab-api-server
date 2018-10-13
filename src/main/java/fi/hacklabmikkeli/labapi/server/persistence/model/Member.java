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

import lombok.Data;

/**
 * @author Heikki Kurhinen
 * 
 * JPA entity for storing members
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Data
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

  @PrePersist
  public void onCreate() {
    setCreatedAt(OffsetDateTime.now());
  }
}
