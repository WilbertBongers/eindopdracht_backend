package nl.wilbertbongers.backend_eindopdracht.repository;

import nl.wilbertbongers.backend_eindopdracht.model.RecordingTechnology;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
@Repository
public interface RecordingTechnologyRepository extends JpaRepository<RecordingTechnology, Long> {
    @Query("SELECT r FROM RecordingTechnology r WHERE r.slug = ?1")
    Optional<RecordingTechnology> findBySlug(String slug);
}
