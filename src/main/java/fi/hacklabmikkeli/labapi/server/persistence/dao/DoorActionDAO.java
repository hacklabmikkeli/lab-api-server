package fi.hacklabmikkeli.labapi.server.persistence.dao;

import java.util.List;
import java.util.UUID;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import fi.hacklabmikkeli.labapi.server.persistence.model.Door;
import fi.hacklabmikkeli.labapi.server.persistence.model.DoorAction;
import fi.hacklabmikkeli.labapi.server.persistence.model.DoorActionType;
import fi.hacklabmikkeli.labapi.server.persistence.model.DoorAction_;

/**
 * DAO class for DoorAction entity
 * 
 * @author Heikki Kurhinen
 */
@ApplicationScoped
public class DoorActionDAO extends AbstractDAO<DoorAction> {

  /**
   * Creates and persists new doorAction entity
   * 
   * @param id id
   * @param door Door the action is related to
   * @param type Type of the door action
   * @param userId Id of the user who initiated the action
   * 
   * @return created DoorAction entity
   */
  public DoorAction create(UUID id, Door door, DoorActionType type, UUID userId) {
    DoorAction doorAction = new DoorAction();

    doorAction.setId(id);
    doorAction.setDoor(door);
    doorAction.setType(type);
    doorAction.setUserId(userId);

    return persist(doorAction);
  }

  /**
   * List doorActions by door
   * 
   * @param door Door to list the actions by
   * @param firstResult First result to return (optional)
   * @param maxResults Max number of results to return (optional) 
   * 
   * @return List of doorActions of door
   */
  public List<DoorAction> listByDoor(Door door, Long firstResult, Long maxResults) {
    EntityManager entityManager = getEntityManager();

    CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
    CriteriaQuery<DoorAction> criteria = criteriaBuilder.createQuery(DoorAction.class);
    Root<DoorAction> root = criteria.from(DoorAction.class);
    criteria.select(root);
    criteria.where(criteriaBuilder.equal(root.get(DoorAction_.door), door));

    TypedQuery<DoorAction> query = entityManager.createQuery(criteria);

    criteria.orderBy(criteriaBuilder.desc(root.get(DoorAction_.createdAt)));

    if (firstResult != null) {
      query.setFirstResult(firstResult.intValue());
    }

    if (maxResults != null) {
      query.setMaxResults(maxResults.intValue());
    }
    return query.getResultList();
  }
}
