package org.sitmun.plugin.core.domain;


import static org.sitmun.plugin.core.domain.Constants.IDENTIFIER;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.TableGenerator;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

/**
 * Type of territorial entities.
 */
@Entity
@Table(name = "STM_TER_TYP", uniqueConstraints = {
    @UniqueConstraint(name = "STM_TET_NOM_UK", columnNames = {"TET_NAME"})})
public class TerritoryType {

  /**
   * Unique identifier.
   */
  @TableGenerator(
      name = "STM_TIPOGRP_GEN",
      table = "STM_SEQUENCE",
      pkColumnName = "SEQ_NAME",
      valueColumnName = "SEQ_COUNT",
      pkColumnValue = "TET_ID",
      allocationSize = 1)
  @Id
  @GeneratedValue(strategy = GenerationType.TABLE, generator = "STM_TIPOGRP_GEN")
  @Column(name = "TET_ID")
  private Integer id;

  /**
   * Name.
   */
  @Column(name = "TET_NAME", length = IDENTIFIER)
  @NotBlank
  private String name;

  public TerritoryType() {
  }

  private TerritoryType(Integer id, @NotBlank String name) {
    this.id = id;
    this.name = name;
  }

  public static Builder builder() {
    return new Builder();
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public static class Builder {
    private Integer id;
    private @NotBlank String name;

    public Builder setId(Integer id) {
      this.id = id;
      return this;
    }

    public Builder setName(@NotBlank String name) {
      this.name = name;
      return this;
    }

    public TerritoryType build() {
      return new TerritoryType(id, name);
    }
  }
}
