package fi.hacklabmikkeli.labapi.server.persistence.model;

import java.util.UUID;

import javax.persistence.Cacheable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.NotEmpty;

import lombok.Data;

/**
 * JPA entity for storing system wide settings
 * 

 */
@Entity
@Cacheable(true)
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "settingKey" }) })
@Cache(usage = CacheConcurrencyStrategy.TRANSACTIONAL)
@Data
public class SystemSetting {

  @Id
  @Column(columnDefinition = "BINARY(16)")
  private UUID id;

  @Column(nullable = false, name = "settingKey")
  @NotNull
  @NotEmpty
  private String key;

  @Column(nullable = false)
  @NotNull
  @NotEmpty
  @Lob
  @Type(type = "org.hibernate.type.TextType")
  private String value;

}
