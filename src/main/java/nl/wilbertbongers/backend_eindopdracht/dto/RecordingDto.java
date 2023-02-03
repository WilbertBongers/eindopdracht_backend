package nl.wilbertbongers.backend_eindopdracht.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Setter
@Getter
public class RecordingDto {
    public MultipartFile file;
    public int discnumber;
    public String recordingtechnology;

    public RecordingDto() {}
    public RecordingDto( MultipartFile file, int discnumber, String recordingtechnology) {
        this.file = file;
        this.discnumber = discnumber;
        this.recordingtechnology = recordingtechnology;
    }

}
