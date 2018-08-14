package fi.hacklabmikkeli.labapi.server.rest.translate;

import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;

import fi.hacklabmikkeli.labapi.server.rest.model.Member;
import fi.hacklabmikkeli.labapi.server.rest.model.Member.StatusEnum;

/**
 * Translates Member related entites to REST entities
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class MemberTranslator extends AbstractTranslator {

  /**
   * Translates Member entity to Member REST model
   * 
   * @param memberEntity Member JPA entity
   * @return translated Member REST model
   */
  public Member translateMember(fi.hacklabmikkeli.labapi.server.persistence.model.Member memberEntity) {
    Member member = new Member();
    member.setId(memberEntity.getId());
    member.setStatus(translateEnum(StatusEnum.class, memberEntity.getStatus()));
    member.setStripeCustomerId(memberEntity.getStripeCustomerId());
    member.setApproved(memberEntity.getApprovedAt());
    member.setCreated(memberEntity.getCreatedAt());
    return member;
  }

  /**
   * Tranlastes list of Member entities into Member REST model
   *
   * @param memberEntities Member JPA entities 
   *  
   * @return list of translated Member REST models
   */
  public List<Member> translateMembers(List<fi.hacklabmikkeli.labapi.server.persistence.model.Member> memberEntities) {
    return memberEntities.stream().map(this::translateMember).collect(Collectors.toList());
  }
}