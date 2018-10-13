package fi.hacklabmikkeli.labapi.server.persistence.model;

import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import lombok.Data;

/**
 * @author Heikki Kurhinen
 * 
 * JPA entity for storing announcement recipients
 */
@Entity
@Cacheable(true)
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Data
public class AnnouncementRecipient {

  @Id
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(columnDefinition = "BINARY(16)", nullable = false)
  private UUID recipientId;

  @ManyToOne(optional = false)
  private Announcement announcement;

}
