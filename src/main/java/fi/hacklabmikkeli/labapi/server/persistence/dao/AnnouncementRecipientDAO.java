package fi.hacklabmikkeli.labapi.server.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.hacklabmikkeli.labapi.server.persistence.model.Announcement;
import fi.hacklabmikkeli.labapi.server.persistence.model.AnnouncementRecipient;
import fi.hacklabmikkeli.labapi.server.persistence.model.AnnouncementRecipient_;

/**
 * @author Heikki Kurhinen
 * 
 * DAO class for AnnouncementRecipient entity
 */
@ApplicationScoped
public class AnnouncementRecipientDAO extends AbstractDAO<AnnouncementRecipient> {

  /**
   * Creates and persists new announcementRecipient entity
   * 
   * @param id id
   * @param announcement Annoncement the recipient is related to
   * @param recipientId Announcement recipients user id
   * 
   * @return created AnnouncementRecipient entity
   */
  public AnnouncementRecipient create(UUID id, Announcement announcement, UUID recipientId) {
    AnnouncementRecipient announcementRecipient = new AnnouncementRecipient();

    announcementRecipient.setId(id);
    announcementRecipient.setAnnouncement(announcement);
    announcementRecipient.setRecipientId(recipientId);

    return persist(announcementRecipient);
  }

  /**
   * List announcementRecipients by announcement
   * 
   * @param announcement annoncement to list recipients by
   * 
   * @return List of announcementRecipients of announcement
   */
  public List<AnnouncementRecipient> listByAnnouncement(Announcement announcement) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<AnnouncementRecipient> criteria = criteriaBuilder.createQuery(AnnouncementRecipient.class);
    Root<AnnouncementRecipient> root = criteria.from(AnnouncementRecipient.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(AnnouncementRecipient_.announcement), announcement));

    TypedQuery<AnnouncementRecipient> query = entityManager.createQuery(criteria);

    return query.getResultList();
  }
}
