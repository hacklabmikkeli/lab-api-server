package fi.hacklabmikkeli.labapi.server.rest;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.ejb.Stateful;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.validation.Valid;
import javax.ws.rs.core.Response;

import fi.hacklabmikkeli.labapi.server.announcements.AnnouncementController;
import fi.hacklabmikkeli.labapi.server.persistence.model.AnnouncementRecipient;
import fi.hacklabmikkeli.labapi.server.persistence.model.AnnouncementType;
import fi.hacklabmikkeli.labapi.server.rest.model.Announcement;
import fi.hacklabmikkeli.labapi.server.rest.translate.AnnouncementTranslator;

/**
 * Announcements REST service implementation
 * 
 * @author Heikki Kurhinen
 */
@RequestScoped
@Stateful
public class AnnouncementsApiImpl extends AbstractApi implements AnnouncementsApi {

  @Inject
  private AnnouncementController announcementController;

  @Inject
  private AnnouncementTranslator announcementTranslator;

  @Override
  public Response createAnnouncement(@Valid Announcement payload) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    switch (payload.getType()) {
      case MAIL:
        return handleMailAnnouncement(payload);
      case SLACK:
        return handleSlackAnnouncement(payload);
      default:
        return createBadRequest("Unknown announcement type");
    }
  }

  @Override
  public Response deleteAnnouncement(UUID announcementId) throws Exception {
    if (!isRealmAdmin()) {
      return createForbidden(UNAUTHORIZED);
    }

    fi.hacklabmikkeli.labapi.server.persistence.model.Announcement announcementEntity = announcementController.findAnnouncement(announcementId);
    if (announcementEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    announcementController.deleteAnnouncement(announcementEntity);
    return createNoContent();
  }

  @Override
  public Response findAnnouncement(UUID announcementId) throws Exception {
    fi.hacklabmikkeli.labapi.server.persistence.model.Announcement announcementEntity = announcementController.findAnnouncement(announcementId);
    if (announcementEntity == null) {
      return createNotFound(NOT_FOUND_MESSAGE);
    }

    List<AnnouncementRecipient> recipients = announcementController.listAnnouncementRecipients(announcementEntity);
    return createOk(announcementTranslator.translateAnnouncement(announcementEntity, recipients));
  }

  @Override
  public Response listAnnouncements(Long firstResult, Long maxResults) throws Exception {
    List<fi.hacklabmikkeli.labapi.server.persistence.model.Announcement> announcementEntities = announcementController.listAnnouncements(firstResult, maxResults);
    return createOk(announcementEntities.stream()
      .map(announcementEntity -> 
        announcementTranslator.translateAnnouncement(announcementEntity, announcementController.listAnnouncementRecipients(announcementEntity))
      )
      .collect(Collectors.toList()));
  }

  /**
   * Handles creation of MAIL type announcement
   * 
   * @param Announcement REST model
   * 
   * @return response
   */
  private Response handleMailAnnouncement(Announcement announcement) {
    //TODO: handle sending emails
    fi.hacklabmikkeli.labapi.server.persistence.model.Announcement announcementEntity = announcementController.createAnnouncement(announcement.getContent(), AnnouncementType.MAIL);
    List<AnnouncementRecipient> recipients = createAnnouncementRecipients(announcementEntity, announcement.getRecipients());
    return createOk(announcementTranslator.translateAnnouncement(announcementEntity, recipients));
  }

  /**
   * Handles creation of SLACK type announcement
   * 
   * @param Announcement REST model
   * 
   * @return response
   */
  private Response handleSlackAnnouncement(Announcement announcement) {
    //TODO: handle Slack notifications
    fi.hacklabmikkeli.labapi.server.persistence.model.Announcement announcementEntity = announcementController.createAnnouncement(announcement.getContent(), AnnouncementType.MAIL);
    List<AnnouncementRecipient> recipients = createAnnouncementRecipients(announcementEntity, announcement.getRecipients());
    return createOk(announcementTranslator.translateAnnouncement(announcementEntity, recipients));
  }

  /**
   * Creates AnnouncementRecipient JPA entities
   * 
   * @param announcementEntity JPA entity recipients are connected to
   * @param recipientIds User ids of the recipients
   * 
   * @return list of AnnouncementRecipient JPA entities
   */
  private List<AnnouncementRecipient> createAnnouncementRecipients(
      fi.hacklabmikkeli.labapi.server.persistence.model.Announcement announcementEntity,
      List<UUID> recipientIds) {

    return recipientIds.stream()
      .map(recipientId -> announcementController.createAnnouncementRecipient(announcementEntity, recipientId))
      .collect(Collectors.toList());
  }
}