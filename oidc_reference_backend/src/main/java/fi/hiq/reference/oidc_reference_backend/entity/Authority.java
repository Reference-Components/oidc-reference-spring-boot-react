package fi.hiq.reference.oidc_reference_backend.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Table(name = "authority")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class Authority {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "authority_id")
  Long authorityId;

  @NotNull
  @Column(name = "authority_string")
  String authorityString;

  @ManyToOne
  @JoinColumn(name = "user_id", nullable = false)
  User user;
}
