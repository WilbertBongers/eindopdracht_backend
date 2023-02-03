package nl.wilbertbongers.backend_eindopdracht.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "albums")
public class Album {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="album_id")
    private long id;

    private String title;
    private String artist;

    @Column(name = "disc_amount")
    private int discAmount;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn( name = "request_id" )
    private Request request;

    @OneToMany(mappedBy = "album")
    private List<Recording> recordingList;

    public Album() {}
    public Album(
            long id,
            String title,
            String artist,
            int discAmount,
            List<Recording> recordingList) {
        this.id =id;
        this.title = title;
        this.artist = artist;
        this.discAmount = discAmount;
        this.recordingList = recordingList;
    }
}
