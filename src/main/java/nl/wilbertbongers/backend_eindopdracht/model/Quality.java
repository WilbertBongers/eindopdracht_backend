package nl.wilbertbongers.backend_eindopdracht.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
@Setter
@Getter
@Entity
@Table(name = "qualities")
public class Quality {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="quality_id")
    private long id;
    private int points;
    private String message;
    private int discnumber;
    @OneToOne(mappedBy="quality")
    private Recording recording;

    @ManyToOne()
    @JoinColumn(name="user_id", nullable=false)
    private User user;

    public Quality() {}
    public Quality(long id, int points, String message, int discnumber, Recording recording) {
        this.id = id;
        this.points = points;
        this.message = message;
        this.discnumber = discnumber;
        this.recording = recording;
    }
}
