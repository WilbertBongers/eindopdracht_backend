package nl.wilbertbongers.backend_eindopdracht.repository;

import nl.wilbertbongers.backend_eindopdracht.model.Album;
import nl.wilbertbongers.backend_eindopdracht.model.Recording;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AlbumRepository extends CrudRepository<Album, Long> {
    @Query("SELECT a FROM Album a WHERE a.request.id = ?1")
    Optional<Album> findAlbumByRequest(Long id);
}
