package nl.wilbertbongers.backend_eindopdracht.controller;

import nl.wilbertbongers.backend_eindopdracht.dto.*;
import nl.wilbertbongers.backend_eindopdracht.model.Claim;
import nl.wilbertbongers.backend_eindopdracht.service.RecordingService;
import nl.wilbertbongers.backend_eindopdracht.service.RequestService;
import nl.wilbertbongers.backend_eindopdracht.service.UserService;
import nl.wilbertbongers.backend_eindopdracht.util.EnumValidator;
import nl.wilbertbongers.backend_eindopdracht.util.OrderType;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
public class RequestController {

    private final RequestService requestService;
    private final UserService userService;
    private final RecordingService recordingService;

    public RequestController (
            RequestService requestService,
            UserService userService,
            RecordingService recordingService
    ) {
        this.requestService = requestService;
        this.userService = userService;
        this.recordingService = recordingService;
    }

//region Get mappings
    @GetMapping("/requests")
    public List<AlbumRequestDto> getAllRequests(@RequestParam(required = false) String order , @RequestParam(required = false) String direction) {

        return requestService.getRequests(
                Optional.ofNullable(order), Optional.ofNullable(direction)
        );
    }
    @GetMapping("/requests/state/{state}")
    public List<AlbumRequestDto> getAllRequestsByState(@RequestParam(required = false) OrderType order, @PathVariable String state) throws ResponseStatusException {

        if(EnumValidator.StateTypeValidator(state)) {
            return requestService.getRequestsByState(StateType.valueOf(state.toUpperCase()),Optional.ofNullable(order));
        } else {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Parameter is not correct type");
        }
    }
    @GetMapping("/requests/claimed")
    public List<AlbumRequestDto> getAllRequestsCurrentlyClaimed(@RequestParam(required = false) OrderType order) {

        return requestService.getClaimedRequests();
    }
    @GetMapping("/requests/worklist")
    public List<AlbumRequestDto> getAllRequestsForWorklist(@RequestParam(required = false) OrderType order) {

        return requestService.getUnclaimedRequests();
    }
    @GetMapping("/requests/qualitylist")
    public List<AlbumRequestDto> getAllRequestsForQualitylist(@RequestParam(required = false) OrderType order) {

            return requestService.getAllRequestsForQC();
    }
    @GetMapping("/requests/{id}")
    public ResponseEntity<AlbumRequestDto> getRequestById(@PathVariable long id) throws ResponseStatusException{
        Optional<AlbumRequestDto> result = requestService.getRequestById(id);
        if (result.isPresent()) {
            return new ResponseEntity<AlbumRequestDto>(result.get(), HttpStatus.OK);
        } else throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Request " +id+ " Not Found");
    }
    @GetMapping("/requests/{id}/claim")
    public ResponseEntity<ClaimDto> getClaimByRequestId(@PathVariable long id) throws ResponseStatusException {
        Optional<ClaimDto> result = requestService.getClaimByRequestId(id);
        if (result.isPresent()) {
            return new ResponseEntity<ClaimDto>(result.get(), HttpStatus.OK);
        } else throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Request " +id+ " Not Found");
    }

//endregion
//region Post mappings
    @PostMapping("/requests")
    public ResponseEntity<String> postNewRequest(
            @Valid @RequestBody AlbumRequestDto albumRequestDto,
            BindingResult br) {

        if (br.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            for (FieldError fe : br.getFieldErrors()) {
                sb.append(fe.getField() + ": ");
                sb.append(fe.getDefaultMessage());
                sb.append("\n");
            }
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, sb.toString());
        }
        else {
            String message = "Request created!";
            try {
                AlbumRequestDto created = requestService.createRequest(albumRequestDto);
                URI uri = URI.create(
                        ServletUriComponentsBuilder
                                .fromCurrentContextPath()
                                .path("/requests/" + created.getId()).toUriString());
                return ResponseEntity.created(uri).body(message);
            } catch (Exception e) {
                message = "Creating Request failed" + " Error: " + e.getMessage();
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
            }
        }
    }
    @PostMapping("/requests/batch-import")
    public ResponseEntity<String> postBatchImportRequests(
            @Valid @RequestBody List<AlbumRequestDto> albumRequestDtoList) {

        Long createdId = null;
        String message = "Request batch imported!";
        try {

            for (AlbumRequestDto ar : albumRequestDtoList) {
                createdId = requestService.createRequest(ar).getId();
            }
            URI uri = URI.create(
                    ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("/requests/" + createdId).toUriString());
            return ResponseEntity.created(uri).body(message);
        } catch (Exception e) {
            message = "Creating Claim failed" + " Error: " + e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
    @PostMapping("/requests/claim")
    public ResponseEntity<String> postClaimRequest(
            @Valid @RequestBody ClaimDto claimDto) {

        String message = "Request claimed!";
        try {
            ClaimDto newClaimDto = requestService.createClaim(claimDto.getRequestId());
            URI uri = URI.create(
                    ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("/requests/claim" + newClaimDto.getId()).toUriString());
            return ResponseEntity.created(uri).body(message);
        } catch (Exception e) {
            message = "Creating Claim failed" + " Error: " + e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
    @PostMapping("/requests/{id}/quality")
    public ResponseEntity<String> postQualityRequest(
            @RequestBody QualityDto qualityDto,
            @PathVariable long id) {
        String message = "Recording has been Quality controlled!";
        try {

            QualityDto qualDto = requestService.createQuality(qualityDto, id);
            URI uri = URI.create(
                    ServletUriComponentsBuilder
                            .fromCurrentContextPath()
                            .path("/requests/" + id + "/quality/" + qualDto.getId()).toUriString());
            return ResponseEntity.created(uri).body(message);
        } catch (Exception e) {
            message = "Creating Quality failed" + " Error: " + e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
    @PostMapping("/requests/{id}/recording")
    public ResponseEntity<String> postRecordingByRequestAndDiscnumber(
            @RequestParam(value = "file") MultipartFile file,
            @RequestParam(value = "discnumber") int discnumber,
            @RequestParam(value = "recordingtechnology") String recordingtechnology,
            @PathVariable long id) throws ResponseStatusException{
        String message = null;
        try {
            RecordingDto recordingDto = new RecordingDto(file, discnumber, recordingtechnology);
            recordingService.create(id, recordingDto);
            message = "Uploaded the file successfully";
            return ResponseEntity.status(HttpStatus.OK).body(message);
        } catch (Exception e) {
            message = "Could not upload the file. Error: " + e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
//endregion
//region Put mappings
    @PutMapping("/requests/{id}")
    public ResponseEntity<AlbumRequestDto> alterRequest(
            @Valid @RequestBody AlbumDto albumDto,
            @PathVariable long id) {
        String message = "";
        try {
            AlbumRequestDto alb = requestService.alterRequest(id, albumDto);
            ResponseEntity<AlbumRequestDto> res = new ResponseEntity<AlbumRequestDto>(alb, HttpStatus.OK);
            return res;
        } catch (Exception e) {
            message = "Could not update the Request. Error: " + e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
//endregion
//region Delete mappings
    @DeleteMapping("/requests/{id}/claim")
    public ResponseEntity<String> deleteClaimRequest(@PathVariable long id) {

        try {
            requestService.deleteClaim(id);
            return ResponseEntity.ok().body("Claim deleted!");
        } catch (Exception e) {
            String message = "Could not delete Claim: " + id + ". Error: " + e.getMessage();
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
        }
    }
//endregion
}
