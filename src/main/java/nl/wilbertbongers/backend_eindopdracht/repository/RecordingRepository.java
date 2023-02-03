package nl.wilbertbongers.backend_eindopdracht.repository;

import nl.wilbertbongers.backend_eindopdracht.model.Recording;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface RecordingRepository extends CrudRepository<Recording, Long> {
    @Query("SELECT r FROM Recording r WHERE r.album.id = ?1 AND r.discnumber = ?2 ")
    Optional<Recording> findRecordingsByAlbumAndDiscnumber(Long album_id, int discnumber);

    @Query("SELECT r FROM Recording r WHERE r.album.id = ?1 AND r.quality IS NULL ")
    List<Recording> findRecordingByAlbumAndNoQC(Long album_id);
}
