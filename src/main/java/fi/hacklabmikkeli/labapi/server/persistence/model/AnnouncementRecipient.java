package fi.hacklabmikkeli.labapi.server.persistence.model;

import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;

/**
 * @author Heikki Kurhinen
 * 
 * JPA entity for storing announcement recipients
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
public class AnnouncementRecipient {

  @Id
  @Type(type="org.hibernate.type.PostgresUUIDType")
  private UUID id;

  @Type(type="org.hibernate.type.PostgresUUIDType")
  @Column(nullable = false)
  private UUID recipientId;

  @ManyToOne(optional = false)
  private Announcement announcement;

   /**
   * @return the id
   */
  public UUID getId() {
    return id;
  }

  /**
   * @return the recipientId
   */
  public UUID getRecipientId() {
    return recipientId;
  }

  /**
   * @param recipientId the recipientId to set
   */
  public void setRecipientId(UUID recipientId) {
    this.recipientId = recipientId;
  }

  /**
   * @return the announcement
   */
  public Announcement getAnnouncement() {
    return announcement;
  }

  /**
   * @param announcement the announcement to set
   */
  public void setAnnouncement(Announcement announcement) {
    this.announcement = announcement;
  }

  /**
   * @param id the id to set
   */
  public void setId(UUID id) {
    this.id = id;
  }
}
