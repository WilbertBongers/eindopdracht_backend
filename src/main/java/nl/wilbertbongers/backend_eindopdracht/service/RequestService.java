package nl.wilbertbongers.backend_eindopdracht.service;

import nl.wilbertbongers.backend_eindopdracht.dto.AlbumDto;
import nl.wilbertbongers.backend_eindopdracht.dto.AlbumRequestDto;
import nl.wilbertbongers.backend_eindopdracht.dto.ClaimDto;
import nl.wilbertbongers.backend_eindopdracht.dto.QualityDto;
import nl.wilbertbongers.backend_eindopdracht.model.*;
import nl.wilbertbongers.backend_eindopdracht.repository.*;
import nl.wilbertbongers.backend_eindopdracht.security.CustomUserDetails;
import nl.wilbertbongers.backend_eindopdracht.util.ClassUtils;
import nl.wilbertbongers.backend_eindopdracht.util.OrderType;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RequestService {

    private final RequestRepository requestRepository;
    private final AlbumRepository albumRepository;
    private final ClaimRepository claimRepository;
    private final UserRepository userRepository;
    private final QualityRepository qualityRepository;
    private final RecordingRepository recordingRepository;

    public RequestService(
            RequestRepository requestRepository,
            AlbumRepository albumRepository,
            ClaimRepository claimRepository,
            UserRepository userRepository,
            QualityRepository qualityRepository,
            RecordingRepository recordingRepository) {

        this.requestRepository = requestRepository;
        this.albumRepository = albumRepository;
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
        this.qualityRepository = qualityRepository;
        this.recordingRepository = recordingRepository;
    }
    public List<AlbumRequestDto> getRequests(Optional<String> orderOpt,  Optional<String> directionOpt) {

        String order = orderOpt.isPresent() ? orderOpt.get() : "id";
        Sort.Direction direction = Sort.Direction.valueOf("ASC");
        List<Request> requestList = requestRepository.findAll(Sort.by(direction, order));

        return ClassUtils.createAlbumRequestDtoList(requestList);
    }
    public List<AlbumRequestDto> getUnclaimedRequests() {

        List<Request> requestList = requestRepository.findUnclaimedRequests();

        return ClassUtils.createAlbumRequestDtoList(requestList);
    }
    public List<AlbumRequestDto> getClaimedRequests() {

        List<Request> requestList = requestRepository.findClaimedRequests();
        return ClassUtils.createAlbumRequestDtoList(requestList);
    }
    public List<AlbumRequestDto> getRequestsByState(StateType state, Optional<OrderType> order) {

        List<Request> requestList = requestRepository.findRequestByState(state, Sort.by(Sort.Direction.ASC, "id"));

        return ClassUtils.createAlbumRequestDtoList(requestList);
    }
    public List<AlbumRequestDto> getAllRequestsForQC() {

        List<Request> requestList = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth.getPrincipal() instanceof CustomUserDetails) {
            UserDetails ud = (UserDetails) auth.getPrincipal();

            Optional<User> userOpt = userRepository.findByUsername(ud.getUsername());
            if(userOpt.isPresent()) {
                requestList = requestRepository.findRequestsForQC(userOpt.get());
            }
        }
        return ClassUtils.createAlbumRequestDtoList(requestList);
    }
    public Optional<AlbumRequestDto> getRequestById(long id) {

        Optional<Request> requestOpt = requestRepository.findById(id);

        if(requestOpt.isPresent()) {

            return Optional.of(ClassUtils.createSingleAlbumRequestDto(requestOpt.get()));
        } else return Optional.empty();
    }
    public Optional<ClaimDto> getClaimByRequestId(long id) {
        List<Claim> claim = null;
        Optional<Request> requestOpt = requestRepository.findById(id);
        if(requestOpt.isPresent()) {
            claim = requestOpt.get().getClaim();
            if (claim.size() == 1) {
                ClaimDto claimDto = new ClaimDto();
                claimDto.setId(claim.get(0).getId());
                claimDto.setRequestId(claim.get(0).getRequest().getId());
                claimDto.setUserId(claim.get(0).getUser().getId());
                return Optional.of(claimDto);
            } else {
                return Optional.empty();
            }
        }
        else {
            return Optional.empty();
        }
    }
    public ClaimDto createClaim(long id) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object a = auth.getPrincipal();
        if (a instanceof CustomUserDetails) {
            UserDetails ud = (UserDetails) auth.getPrincipal();
            Optional<Request> requestOpt = requestRepository.findById(id);
            Optional<User> user = userRepository.findByUsername(ud.getUsername());
            if (requestOpt.isPresent() && user.isPresent() && requestOpt.get().getClaim().size()==0) {
                Request request = requestOpt.get();
                try {
                    StateType newState = StateType.RECORD;
                    request.setState(newState);
                    request.setDateLastChanged(Date.valueOf(LocalDate.now()));
                    requestRepository.save(request);

                    Claim claim = new Claim();
                    claim.setRequest(request);
                    claim.setUser(user.get());
                    claimRepository.save(claim);
                    return ClassUtils.createClaimDto(claim);
                } catch (Exception e) {
                    throw new RuntimeException("Cannot create Claim");
                }
            } else {
                throw new RuntimeException("Request not found");
            }
        } else {
            throw new RuntimeException("No instance of CustomUserdetail class");
        }
    }
    public AlbumRequestDto createRequest(AlbumRequestDto ARdto) {
        try {
            Album album = new Album();
            Request request = new Request();

            album.setArtist(ARdto.getArtist());
            album.setTitle(ARdto.getTitle());
            album.setDiscAmount(ARdto.getDiscAmount());
            album.setRecordingList(new ArrayList<Recording>());

            request.setState(StateType.NEW);
            request.setDateCreated(Date.valueOf(LocalDate.now()));
            request.setDateLastChanged(Date.valueOf(LocalDate.now()));
            request.setAlbum(album);
            request.setClaim(new ArrayList<Claim>());
            requestRepository.save(request);
            return ClassUtils.createSingleAlbumRequestDto(request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Request");
        }
    }
    public QualityDto createQuality(QualityDto qualityDto, long id) {
        QualityDto returnQuality = null;
        Optional<User> userOpt = Optional.empty();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Object a = auth.getPrincipal();
        if (a instanceof CustomUserDetails) {
            UserDetails ud = (UserDetails) auth.getPrincipal();
            userOpt = userRepository.findByUsername(ud.getUsername());
        }
        Optional<Request> requestOpt = requestRepository.findById(id);

        if (requestOpt.isPresent() && userOpt.isPresent()) {
            Request request = requestOpt.get();
            Quality quality = new Quality();
            quality.setPoints(qualityDto.getPoints());
            quality.setMessage(qualityDto.getMessage());
            quality.setDiscnumber(qualityDto.getDiscnumber());
            quality.setUser(userOpt.get());
            qualityRepository.save(quality);
            returnQuality = ClassUtils.createQualityDto(quality);
            Optional<Recording> recordOpt = recordingRepository.findRecordingsByAlbumAndDiscnumber(request.getAlbum().getId(),qualityDto.getDiscnumber());
            if (recordOpt.isPresent()) {
                Recording record = recordOpt.get();
                record.setQuality(quality);
                recordingRepository.save(record);
            } else {
                throw new RuntimeException("No recording found");
            }
            if (recordingRepository.findRecordingByAlbumAndNoQC(request.getAlbum().getId()).size() == 0 ) {
                // check als er meerdere discs zijn of alle recordings een claim hebben
                StateType newState = StateType.FINISHED;
                request.setState(newState);
                request.setDateLastChanged(Date.valueOf(LocalDate.now()));
                requestRepository.save(request);
            }
        } else {
            throw new RuntimeException("No Request found");
        }
        return returnQuality;
    }
    public boolean deleteClaim(long id) {
        Optional<Request> requestOpt = requestRepository.findById(id);
        Claim claim = requestOpt.get().getClaim().get(0);

        if(requestOpt.isPresent() && claim != null) {
            try {
                claimRepository.deleteById(claim.getId());
                return true;
            } catch (Exception e) {
                throw new RuntimeException("Error Deleting Claim");
            }
        } else {
            throw new RuntimeException("No Claim found");
        }
    }
    public AlbumRequestDto alterRequest(long id, AlbumDto albumDto) {
        Optional<Request> requestOpt = requestRepository.findById(id);

        if(requestOpt.isPresent()) {
            try {
                Request request = requestOpt.get();
                Album album = request.getAlbum();
                if (albumDto.getArtist() != album.getArtist() && albumDto.getArtist() != null) {
                    album.setArtist(albumDto.getArtist());
                }
                if (albumDto.getTitle() != album.getTitle() && albumDto.getTitle() != null) {
                    album.setTitle(albumDto.getTitle());
                }
                if (albumDto.getDiscAmount() != album.getDiscAmount() && albumDto.getDiscAmount() != 0) {
                    album.setDiscAmount(albumDto.getDiscAmount());
                }
                request.setAlbum(album);
                return ClassUtils.createSingleAlbumRequestDto(requestRepository.save(request));

            } catch (Exception e) {
                throw new RuntimeException("Error Updating Request");
            }
        } else {
            throw new RuntimeException("No Request found");
        }
    }
}
