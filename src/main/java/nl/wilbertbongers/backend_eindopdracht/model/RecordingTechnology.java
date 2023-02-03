package nl.wilbertbongers.backend_eindopdracht.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
@Setter
@Getter
@Entity
@Table(name = "recording_technologies")
public class RecordingTechnology {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="rec_tech_id")
    private long id;

    @Column(unique=true)
    private String slug;

    private String description;
    private int samplerate;
    private int bitdepth;

    @OneToMany(mappedBy="recordingTechnology")
    private List<Recording> recordingList;
}
