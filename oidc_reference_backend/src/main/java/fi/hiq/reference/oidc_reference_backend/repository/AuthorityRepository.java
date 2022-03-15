package fi.hiq.reference.oidc_reference_backend.repository;

import fi.hiq.reference.oidc_reference_backend.entity.Authority;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

}
