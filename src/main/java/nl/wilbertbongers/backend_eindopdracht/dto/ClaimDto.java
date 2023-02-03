package nl.wilbertbongers.backend_eindopdracht.dto;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

@Getter
@Setter
public class ClaimDto {


    private long id;

    private long requestId;
    private long userId;

    public ClaimDto() {}
    public ClaimDto(long id, long requestId, long userId ) {
        this.id = id;
        this.userId = userId;
        this.requestId = requestId;
    }

}
