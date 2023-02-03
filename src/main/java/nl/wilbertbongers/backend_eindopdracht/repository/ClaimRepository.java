package nl.wilbertbongers.backend_eindopdracht.repository;

import nl.wilbertbongers.backend_eindopdracht.model.Claim;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface ClaimRepository extends CrudRepository<Claim, Long> {

    @Query("SELECT c FROM Claim c WHERE c.request.id = ?1")
    Optional<Claim> findClaimByRequestId(Long id);
}