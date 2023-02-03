package nl.wilbertbongers.backend_eindopdracht.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="claim_id")
    private long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    User user;

    @ManyToOne
    @JoinColumn(name = "request_id")
    Request request;

    public Claim() {}
    public Claim(long id, User user, Request request) {
        this.id = id;
        this.user = user;
        this.request = request;
    }
}
