package nl.wilbertbongers.backend_eindopdracht.repository;

import nl.wilbertbongers.backend_eindopdracht.model.Request;
import nl.wilbertbongers.backend_eindopdracht.model.User;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findAll(Sort sort);
    @Query("SELECT r FROM Request r WHERE r.state = ?1")
    List<Request> findRequestByState(StateType state, Sort sort);
    @Query("SELECT r FROM Request r WHERE r.claim.size = 0 AND r.state='NEW'")
    List<Request> findUnclaimedRequests();
    @Query("SELECT r FROM Request r WHERE r.album.recordingList IS NOT EMPTY AND r.state = 'QUALITY' AND r.album.id NOT IN (SELECT DISTINCT a.album.id FROM Recording a WHERE a.user = ?1 )")
    List<Request> findRequestsForQC(User user);
    @Query("SELECT r FROM Request r WHERE r.claim.size > 0 AND r.state='NEW'")
    List<Request> findClaimedRequests();
}
