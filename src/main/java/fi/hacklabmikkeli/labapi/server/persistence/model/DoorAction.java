package fi.hacklabmikkeli.labapi.server.persistence.model;

import java.time.OffsetDateTime;
import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

/**
 * @author Heikki Kurhinen
 * 
 * JPA entity for storing door actions
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Data
public class DoorAction {

  @Id
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column (columnDefinition = "BINARY(16)", nullable = false)
  private UUID userId;

  @Column (nullable = false)
  private DoorActionType type;

  @ManyToOne(optional = false)
  private Door door;

  @Column (nullable = false)
  private OffsetDateTime createdAt;

  @PrePersist
  public void onCreate() {
    setCreatedAt(OffsetDateTime.now());
  }
}
