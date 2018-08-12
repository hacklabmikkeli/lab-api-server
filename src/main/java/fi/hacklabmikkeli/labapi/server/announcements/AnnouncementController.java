package fi.hacklabmikkeli.labapi.server.announcements;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import fi.hacklabmikkeli.labapi.server.persistence.dao.AnnouncementDAO;
import fi.hacklabmikkeli.labapi.server.persistence.dao.AnnouncementRecipientDAO;
import fi.hacklabmikkeli.labapi.server.persistence.model.Announcement;
import fi.hacklabmikkeli.labapi.server.persistence.model.AnnouncementRecipient;
import fi.hacklabmikkeli.labapi.server.persistence.model.AnnouncementType;

/**
 * Controller for announcement related operations
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class AnnouncementController {

  @Inject
  private AnnouncementDAO announcementDAO;

  @Inject
  private AnnouncementRecipientDAO announcementRecipientDAO;

  /**
   * Creates new announcement
   *
   * @param content Content of the announcement
   * @param type Type of the announcement
   * 
   * @return created announcement
   */
  public Announcement createAnnouncement(String content, AnnouncementType type) {
    return announcementDAO.create(UUID.randomUUID(), content, type);
  }

  /**
   * Creates new announcement recipient
   * 
   * @param announcement announcement that recipient is for
   * @param recipientId User id of the recipient
   *
   * @return created announcement reciepient 
   */
  public AnnouncementRecipient createAnnouncementRecipient(Announcement announcement, UUID recipientId) {
    return announcementRecipientDAO.create(UUID.randomUUID(), announcement, recipientId);
  }

  /**
   * Lists recipients of annouincement
   * 
   * @param announcement announcement
   *
   * @return list of announcements recipients 
   */
  public List<AnnouncementRecipient> listAnnouncementRecipients(Announcement announcement) {
    return announcementRecipientDAO.listByAnnouncement(announcement);
  }

  /**
   * Finds announcement with id
   * 
   * @param id announcement id
   * 
   * @return announcement or null if not found
   */
  public Announcement findAnnouncement(UUID id) {
    return announcementDAO.findById(id);
  }

  /**
   * Lists announcements
   * 
   * @param firstResult first result to return (optional)
   * @param maxResults max number of results to return (optinal)
   * 
   * @return list of announcements 
   */
  public List<Announcement> listAnnouncements(Long firstResult, Long maxResults) {
    return announcementDAO.listAnnouncementsOrderCreatedDesc(firstResult, maxResults);
  }

  /**
   * Deletes announcement
   * 
   * @param announcement Announcement to delete 
   */
  public void deleteAnnouncement(Announcement announcement) {
    listAnnouncementRecipients(announcement).stream().forEach(announcementRecipientDAO::delete);
    announcementDAO.delete(announcement);
  }

}
