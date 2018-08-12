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
import fi.hacklabmikkeli.labapi.server.persistence.model.AnnouncementType;
import fi.hacklabmikkeli.labapi.server.persistence.model.Announcement_;

/**
 * DAO class for Announcement entity
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class AnnouncementDAO extends AbstractDAO<Announcement> {

  /**
   * Creates and persists new announcement entity
   * 
   * @param id id
   * @param content announcement content
   * @param type announcement type
   * 
   * @return created Announcement entity
   */
  public Announcement create(UUID id, String content, AnnouncementType type) {
    Announcement announcement = new Announcement();

    announcement.setId(id);
    announcement.setContent(content);
    announcement.setType(type);

    return persist(announcement);
  }

  /**
   * List announcements ordered by createdAt Desc
   * 
   * @param firstResult first result to return (optional)
   * @param maxResults  max number of results to return (optional)
   * 
   * @return List of announcements announcements are order by descending created date
   */
  public List<Announcement> listAnnouncementsOrderCreatedDesc(Long firstResult, Long maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<Announcement> criteria = criteriaBuilder.createQuery(Announcement.class);
    Root<Announcement> root = criteria.from(Announcement.class);
    criteria.select(root);
    criteria.orderBy(criteriaBuilder.desc(root.get(Announcement_.createdAt)));

    TypedQuery<Announcement> query = entityManager.createQuery(criteria);

    if (firstResult != null) {
      query.setFirstResult(firstResult.intValue());
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults.intValue());
    }

    return query.getResultList();
  }

  /**
   * Updates announcements content
   * 
   * @param announcement announcement to update
   * @param content new content
   * @return updated announcement
   */
  public Announcement updateContent(Announcement announcement, String content) {
    announcement.setContent(content);
    return persist(announcement);
  }

  /**
   * Updates announcements type
   * 
   * @param announcement announcement to update
   * @param type new type
   * @return updated announcement
   */
  public Announcement updateType(Announcement announcement, AnnouncementType type) {
    announcement.setType(type);
    return persist(announcement);
  }

}
