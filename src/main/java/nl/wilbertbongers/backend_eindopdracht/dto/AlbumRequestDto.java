package nl.wilbertbongers.backend_eindopdracht.dto;

import lombok.Getter;
import lombok.Setter;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;


import javax.validation.constraints.NotEmpty;
import java.util.Date;
import java.util.List;

@Getter
@Setter
public class AlbumRequestDto {
    private Long id;
    @NotEmpty
    private String title;
    @NotEmpty
    private String artist;

    private int discAmount;
    private Date requestDate;
    private Date dateLastChanged;
    private StateType state;
    private boolean claimed;
    private List<String> recordings;

    public AlbumRequestDto() {

    }
    public AlbumRequestDto(long id, String title, String artist, int discAmount, Date requestDate, Date dateLastChanged, StateType state, boolean claimed, List<String> recordings) {
        this.id = id;
        this.title = title;
        this.artist = artist;
        this.discAmount = discAmount;
        this.requestDate = requestDate;
        this.dateLastChanged = dateLastChanged;
        this.state = state;
        this.claimed = claimed;
        this.recordings = recordings;
    }
}
