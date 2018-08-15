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

/**
 * @author Heikki Kurhinen
 * 
 * JPA entity for storing door actions
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
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
   * @return the door
   */
  public Door getDoor() {
    return door;
  }

  /**
   * @param door the door to set
   */
  public void setDoor(Door door) {
    this.door = door;
  }

  /**
   * @return the type
   */
  public DoorActionType getType() {
    return type;
  }

  /**
   * @param type the type to set
   */
  public void setType(DoorActionType type) {
    this.type = type;
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

  @PrePersist
  public void onCreate() {
    setCreatedAt(OffsetDateTime.now());
  }
}
