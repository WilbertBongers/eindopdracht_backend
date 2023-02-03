package nl.wilbertbongers.backend_eindopdracht.dto;

import lombok.Getter;
import lombok.Setter;
import nl.wilbertbongers.backend_eindopdracht.model.Recording;

import java.util.List;

@Getter
@Setter
public class QualityDto {
    private long id;
    private int points;
    private String message;
    private int discnumber;

    public QualityDto(long id, int points, String message, int discnumber) {
        this.id = id;
        this.points = points;
        this.message = message;
        this.discnumber = discnumber;
    }

    public QualityDto() {

    }
}
