package org.sitmun.plugin.core.domain;


import lombok.*;
import org.sitmun.plugin.core.constraints.CodeList;
import org.sitmun.plugin.core.constraints.CodeLists;
import org.sitmun.plugin.core.constraints.HttpURL;
import org.sitmun.plugin.core.constraints.SpatialReferenceSystem;
import org.sitmun.plugin.core.converters.StringListAttributeConverter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.sitmun.plugin.core.domain.Constants.IDENTIFIER;

/**
 * Service.
 */
@Entity
@Table(name = "STM_SERVICE")
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Service {

  /**
   * Unique identifier.
   */
  @TableGenerator(
    name = "STM_SERVICE_GEN",
    table = "STM_SEQUENCE",
    pkColumnName = "SEQ_NAME",
    valueColumnName = "SEQ_COUNT",
    pkColumnValue = "SER_ID",
    allocationSize = 1)
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "STM_SERVICE_GEN")
  @Column(name = "SER_ID")
  private Integer id;

  /**
   * Service name.
   */
  @Column(name = "SER_NAME", length = IDENTIFIER)
  @NotBlank
  private String name;

  /**
   * Service description.
   */
  @Column(name = "SER_ABSTRACT", length = 250)
  private String description;

  /**
   * Service endpoint.
   */
  @Column(name = "SER_URL", length = 250)
  @NotNull
  @HttpURL
  private String serviceURL;

  /**
   * List of SRS supported by this service.
   */
  @Column(name = "SER_PROJECTS", length = 1000)
  @Convert(converter = StringListAttributeConverter.class)
  @SpatialReferenceSystem
  private List<String> supportedSRS;

  /**
   * Legend endpoint.
   */
  @Column(name = "SER_LEGEND", length = 250)
  @HttpURL
  private String legendURL;

  /**
   * Get information endpoint.
   */
  @Column(name = "SER_INFOURL", length = 250)
  @HttpURL
  private String getInformationURL;

  /**
   * Created date.
   */
  @Column(name = "SER_CREATED")
  @Temporal(TemporalType.TIMESTAMP)
  private Date createdDate;

  /**
   * Service type.
   * The protocol that can be used to request the service.
   */
  @Column(name = "SER_PROTOCOL", length = IDENTIFIER)
  @NotNull
  @CodeList(CodeLists.SERVICE_TYPE)
  private String type;

  /**
   * Native protocol.
   * Required when SITMUN acts as reverse proxy and the origin protocol has a protocol
   * different from the protocol declared in {@link #type}.
   */
  @Column(name = "SER_NAT_PROT", length = IDENTIFIER)
  @CodeList(CodeLists.SERVICE_NATIVE_PROTOCOL)
  private String nativeProtocol;

  /**
   * <code>true</code> if the service is blocked and cannot be used.
   */
  @NotNull
  @Column(name = "SER_BLOCKED")
  private Boolean blocked;

  /**
   * Layers provided by this service.
   */
  @OneToMany(mappedBy = "service", orphanRemoval = true, fetch = FetchType.LAZY)
  private Set<Cartography> layers;

  /**
   * Service parameters.
   */
  @OneToMany(mappedBy = "service", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<ServiceParameter> parameters = new HashSet<>();


  @Override
  public boolean equals(Object o) {
    if (this == o) return true;

    if (!(o instanceof Service))
      return false;

    Service other = (Service) o;

    return id != null &&
      id.equals(other.getId());
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
