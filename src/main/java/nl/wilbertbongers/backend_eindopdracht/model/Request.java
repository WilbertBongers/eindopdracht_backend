package nl.wilbertbongers.backend_eindopdracht.model;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;

import javax.persistence.*;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "requests")
public class Request {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="request_id")
    private long id;

    @Enumerated(EnumType.STRING)
    @Column(name="state_type")
    private StateType state;

    private Date dateCreated;
    private Date dateLastChanged;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "album_id", referencedColumnName = "album_id")
    private Album album;

    @OneToMany(mappedBy = "request")
    List<Claim> claim;

    public Request() {}

    public Request(
            long id,
            StateType state,
            Date dateCreated,
            Date dateLastChanged,
            Album album,
            List<Claim> claim) {
        this.id = id;
        this.state = state;
        this.dateCreated = dateCreated;
        this.dateLastChanged = dateLastChanged;
        this.album = album;
        this.claim = claim;
    }
}
