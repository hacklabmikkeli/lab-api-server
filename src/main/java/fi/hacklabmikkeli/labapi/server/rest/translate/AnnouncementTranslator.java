package fi.hacklabmikkeli.labapi.server.rest.translate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import fi.hacklabmikkeli.labapi.server.persistence.model.AnnouncementRecipient;
import fi.hacklabmikkeli.labapi.server.rest.model.Announcement;
import fi.hacklabmikkeli.labapi.server.rest.model.Announcement.TypeEnum;

/**
 * Translates Announcement related entites to REST entities
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class AnnouncementTranslator extends AbstractTranslator {

  /**
   * Translates Announcement entity to Announcement REST model
   * 
   * @param announcementEntity Announcement JPA entity
   * @param announcementRecipient JPA entities
   * @return translated Announcement REST model
   */
  public Announcement translateAnnouncement(
      fi.hacklabmikkeli.labapi.server.persistence.model.Announcement announcementEntity, 
      List<AnnouncementRecipient> announcementRecipientEntities) {

    Announcement announcement = new Announcement();
    announcement.setId(announcementEntity.getId());
    announcement.setContent(announcementEntity.getContent());
    announcement.setType(translateEnum(TypeEnum.class, announcementEntity.getType()));
    announcement.setRecipients(announcementRecipientEntities.stream().map(AnnouncementRecipient::getRecipientId).collect(Collectors.toList()));
    return announcement;
  }
}