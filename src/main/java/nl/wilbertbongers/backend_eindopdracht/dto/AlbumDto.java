package nl.wilbertbongers.backend_eindopdracht.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class AlbumDto {
    String title;
    String artist;
    int discAmount;

    public AlbumDto() {}
    public AlbumDto(String title, String artist, int discAmount) {
        this.title = title;
        this.artist = artist;
        this.discAmount = discAmount;
    }
}
