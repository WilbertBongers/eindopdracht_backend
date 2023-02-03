package nl.wilbertbongers.backend_eindopdracht.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
@Setter
@Getter
@Entity
@Table(name = "recordings")
public class Recording {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="recording_id")
    private long id;
    private String filelocation;
    private int discnumber;

    @ManyToOne
    @JoinColumn(name="album_id", nullable=false)
    private Album album;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @OneToOne
    @JoinColumn(name="quality_id")
    private Quality quality;

    @ManyToOne
    @JoinColumn(name="recording_technology_id")
    private	RecordingTechnology recordingTechnology;
}
