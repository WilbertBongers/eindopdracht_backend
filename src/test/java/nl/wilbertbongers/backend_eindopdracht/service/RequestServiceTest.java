package nl.wilbertbongers.backend_eindopdracht.service;

import nl.wilbertbongers.backend_eindopdracht.dto.*;
import nl.wilbertbongers.backend_eindopdracht.model.*;
import nl.wilbertbongers.backend_eindopdracht.repository.*;
import nl.wilbertbongers.backend_eindopdracht.security.CustomUserDetails;
import nl.wilbertbongers.backend_eindopdracht.util.OrderType;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithUserDetails;

import java.sql.Date;
import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @Mock
    RequestRepository requestRepository;

    @Mock
    UserRepository userRepository;
    @Mock
    ClaimRepository claimRepository;
    @Mock
    RecordingRepository recordingRepository;
    @Mock
    QualityRepository qualityRepository;
    @InjectMocks
    RequestService requestService;

    @Captor
    ArgumentCaptor<Request> argumentRequestCaptor;

    @Captor
    ArgumentCaptor<Claim> argumentClaimCaptor;

    @Captor
    ArgumentCaptor<Quality> argumentQualityCaptor;

    @Captor
    ArgumentCaptor<Recording> argumentRecordingCaptor;


    //Volgorde tests
    //Arrange
    //Act
    //Assert

    Request request1;
    Request request2;
    Request request3;

    Album album1;
    Album album2;
    Album album3;

    AlbumRequestDto arDto1;
    AlbumRequestDto arDto2;
    AlbumRequestDto arDto3;
    QualityDto qualDto;
    Quality qual;
    User user;
    UserDto userDto;
    Role role;
    Claim claim;
    Date now;
    boolean isClaimed;
    List<String> list;
    Authentication auth;
    SecurityContext securityContext;

    @BeforeEach
    void setUp() {
        now = Date.valueOf(LocalDate.now());
        list = new ArrayList<>();
        isClaimed = false;
        album1 = new Album(1L, "Purple rain", "Prince", 1, new ArrayList<Recording>());
        album2 = new Album(2L, "Do you like my tight sweater", "Moloko", 1, new ArrayList<Recording>());
        album3 = new Album(3L, "Kind of blue", "Miles Davis", 1, new ArrayList<Recording>());
        request1 = new Request(1l, StateType.NEW,now,now,album1,new ArrayList<Claim>());
        request2 = new Request(2l, StateType.RECORD,now,now,album2,new ArrayList<Claim>());
        request3 = new Request(3l, StateType.QUALITY,now,now,album3,new ArrayList<Claim>());
        arDto1 = new AlbumRequestDto(1L,"Purple rain" ,"Prince",1, now,now, StateType.NEW, isClaimed, list);
        arDto2 = new AlbumRequestDto(2L,"Do you like my tight sweater" ,"Moloko",1, now, now,StateType.RECORD, isClaimed, list);
        arDto3 = new AlbumRequestDto(3L,"Kind of blue" ,"Miles Davis",1, now, now, StateType.QUALITY, isClaimed, list);
        qualDto = new QualityDto(0l, 50, "Fabelhaft", 1);
        qual = new Quality(0l, 50, "Fabelhaft", 1, new Recording());
        role = new Role();
        user = new User(
                1L,
                "admin@wilbertbongers.nl",
                "test123",
                "admin",
                Date.valueOf("2001-01-01"),
                "wegisweg",
                10,
                "1234ab",
                "Rotterdam",
                "Nederland",
                "0101234567",
                true,
                new ArrayList<Claim>(),
                new ArrayList<Recording>(),
                role
                );
        userDto = new UserDto(
                1L,
                "admin@wilbertbongers.nl",
                "test123",
                "admin",
                Date.valueOf("2001-01-01"),
                "wegisweg",
                10,
                "1234ab",
                "Rotterdam",
                "Nederland",
                "0101234567",
                "ADMIN"
        );
        claim = new Claim(0l, user, request2);

        auth = mock(Authentication.class);
        securityContext = mock(SecurityContext.class);
        SecurityContextHolder.setContext(securityContext);

    }

    @Test
    void getRequests() {
        String order = "id";
        Sort.Direction direction = Sort.Direction.valueOf("ASC");
        when(requestRepository.findAll(Sort.by(direction, order))).thenReturn(List.of(request1, request2));

        List<Request> requests = requestRepository.findAll(Sort.by(direction, order));
        List<AlbumRequestDto> dtos = requestService.getRequests(Optional.empty(), Optional.empty());

        assertEquals(requests.get(0).getId(),dtos.get(0).getId());
        assertEquals(requests.get(0).getState(),dtos.get(0).getState());
        assertEquals(requests.get(0).getDateCreated(),dtos.get(0).getRequestDate());
        assertEquals(requests.get(0).getDateLastChanged(),dtos.get(0).getDateLastChanged());
        assertEquals(requests.get(0).getAlbum().getArtist(),dtos.get(0).getArtist());
        assertEquals(requests.get(0).getAlbum().getTitle(),dtos.get(0).getTitle());
        assertEquals(requests.get(0).getAlbum().getDiscAmount(),dtos.get(0).getDiscAmount());
        assertEquals(requests.get(1).getId(),dtos.get(1).getId());
        assertEquals(requests.get(1).getState(),dtos.get(1).getState());
        assertEquals(requests.get(1).getDateCreated(),dtos.get(1).getRequestDate());
        assertEquals(requests.get(1).getDateLastChanged(),dtos.get(1).getDateLastChanged());
        assertEquals(requests.get(1).getAlbum().getArtist(),dtos.get(1).getArtist());
        assertEquals(requests.get(1).getAlbum().getTitle(),dtos.get(1).getTitle());
        assertEquals(requests.get(1).getAlbum().getDiscAmount(),dtos.get(1).getDiscAmount());
    }

    @Test
    void getUnclaimedRequests() {
        when(requestRepository.findUnclaimedRequests()).thenReturn(List.of(request1, request2));

        List<Request> requests = requestRepository.findUnclaimedRequests();
        List<AlbumRequestDto> dtos = requestService.getUnclaimedRequests();

        assertEquals(requests.get(0).getId(),dtos.get(0).getId());
        assertEquals(requests.get(0).getState(),dtos.get(0).getState());
        assertEquals(requests.get(0).getDateCreated(),dtos.get(0).getRequestDate());
        assertEquals(requests.get(0).getDateLastChanged(),dtos.get(0).getDateLastChanged());
        assertEquals(requests.get(0).getAlbum().getArtist(),dtos.get(0).getArtist());
        assertEquals(requests.get(0).getAlbum().getTitle(),dtos.get(0).getTitle());
        assertEquals(requests.get(0).getAlbum().getDiscAmount(),dtos.get(0).getDiscAmount());
        assertEquals(requests.get(1).getId(),dtos.get(1).getId());
        assertEquals(requests.get(1).getState(),dtos.get(1).getState());
        assertEquals(requests.get(1).getDateCreated(),dtos.get(1).getRequestDate());
        assertEquals(requests.get(1).getDateLastChanged(),dtos.get(1).getDateLastChanged());
        assertEquals(requests.get(1).getAlbum().getArtist(),dtos.get(1).getArtist());
        assertEquals(requests.get(1).getAlbum().getTitle(),dtos.get(1).getTitle());
        assertEquals(requests.get(1).getAlbum().getDiscAmount(),dtos.get(1).getDiscAmount());
    }

    @Test
    void GetClaimedRequests() {
        when(requestRepository.findClaimedRequests()).thenReturn(List.of(request1, request2));

        List<Request> requests = requestRepository.findClaimedRequests();
        List<AlbumRequestDto> dtos = requestService.getClaimedRequests();

        assertEquals(requests.get(0).getId(),dtos.get(0).getId());
        assertEquals(requests.get(0).getState(),dtos.get(0).getState());
        assertEquals(requests.get(0).getDateCreated(),dtos.get(0).getRequestDate());
        assertEquals(requests.get(0).getDateLastChanged(),dtos.get(0).getDateLastChanged());
        assertEquals(requests.get(0).getAlbum().getArtist(),dtos.get(0).getArtist());
        assertEquals(requests.get(0).getAlbum().getTitle(),dtos.get(0).getTitle());
        assertEquals(requests.get(0).getAlbum().getDiscAmount(),dtos.get(0).getDiscAmount());
        assertEquals(requests.get(1).getId(),dtos.get(1).getId());
        assertEquals(requests.get(1).getState(),dtos.get(1).getState());
        assertEquals(requests.get(1).getDateCreated(),dtos.get(1).getRequestDate());
        assertEquals(requests.get(1).getDateLastChanged(),dtos.get(1).getDateLastChanged());
        assertEquals(requests.get(1).getAlbum().getArtist(),dtos.get(1).getArtist());
        assertEquals(requests.get(1).getAlbum().getTitle(),dtos.get(1).getTitle());
        assertEquals(requests.get(1).getAlbum().getDiscAmount(),dtos.get(1).getDiscAmount());
    }

    @Test
    void getRequestsByState() {

        StateType state = StateType.NEW;
        when(requestRepository.findRequestByState(state, Sort.by(Sort.Direction.ASC, "id"))).thenReturn(List.of(request1));

        List<Request> requests = requestRepository.findRequestByState(state, Sort.by(Sort.Direction.ASC, "id"));
        List<AlbumRequestDto> dtos = requestService.getRequestsByState(state, Optional.empty());

        assertEquals(requests.get(0).getId(),dtos.get(0).getId());
        assertEquals(requests.get(0).getState(),dtos.get(0).getState());
        assertEquals(requests.get(0).getDateCreated(),dtos.get(0).getRequestDate());
        assertEquals(requests.get(0).getDateLastChanged(),dtos.get(0).getDateLastChanged());
        assertEquals(requests.get(0).getAlbum().getArtist(),dtos.get(0).getArtist());
        assertEquals(requests.get(0).getAlbum().getTitle(),dtos.get(0).getTitle());
        assertEquals(requests.get(0).getAlbum().getDiscAmount(),dtos.get(0).getDiscAmount());
    }

    @Test
    void getAllRequestsForQC() {
        String order = "id";
        Sort.Direction direction = Sort.Direction.valueOf("ASC");

        Set<GrantedAuthority> authorities = new HashSet<>();
        CustomUserDetails principal = new CustomUserDetails("admin@wilbertbongers", "aPassword", true,authorities);

        when(userRepository.findByUsername(principal.getUsername())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findRequestsForQC(user)).thenReturn(List.of(request1, request2));
        when(auth.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(auth);

        Optional<User> userOpt = userRepository.findByUsername("admin@wilbertbongers");
        List<Request> requests = requestRepository.findRequestsForQC(user);
        List<AlbumRequestDto> dtos = requestService.getAllRequestsForQC();

        assertEquals(requests.get(0).getId(),dtos.get(0).getId());
        assertEquals(requests.get(0).getState(),dtos.get(0).getState());
        assertEquals(requests.get(0).getDateCreated(),dtos.get(0).getRequestDate());
        assertEquals(requests.get(0).getDateLastChanged(),dtos.get(0).getDateLastChanged());
        assertEquals(requests.get(0).getAlbum().getArtist(),dtos.get(0).getArtist());
        assertEquals(requests.get(0).getAlbum().getTitle(),dtos.get(0).getTitle());
        assertEquals(requests.get(0).getAlbum().getDiscAmount(),dtos.get(0).getDiscAmount());
        assertEquals(requests.get(1).getId(),dtos.get(1).getId());
        assertEquals(requests.get(1).getState(),dtos.get(1).getState());
        assertEquals(requests.get(1).getDateCreated(),dtos.get(1).getRequestDate());
        assertEquals(requests.get(1).getDateLastChanged(),dtos.get(1).getDateLastChanged());
        assertEquals(requests.get(1).getAlbum().getArtist(),dtos.get(1).getArtist());
        assertEquals(requests.get(1).getAlbum().getTitle(),dtos.get(1).getTitle());
        assertEquals(requests.get(1).getAlbum().getDiscAmount(),dtos.get(1).getDiscAmount());
    }

    @Test
    void getRequestById() {
        when(requestRepository.findById(3l)).thenReturn(Optional.ofNullable(request3));

        Request request = requestRepository.findById(3l).get();
        AlbumRequestDto dto = requestService.getRequestById(3l).get();

        assertEquals(request.getId(),dto.getId());
        assertEquals(request.getState(),dto.getState());
        assertEquals(request.getDateCreated(),dto.getRequestDate());
        assertEquals(request.getDateLastChanged(),dto.getDateLastChanged());
        assertEquals(request.getAlbum().getArtist(),dto.getArtist());
        assertEquals(request.getAlbum().getTitle(),dto.getTitle());
        assertEquals(request.getAlbum().getDiscAmount(),dto.getDiscAmount());
    }

    @Test
    void getRequestByIdReturnsEmpty() {
        when(requestRepository.findById(3l)).thenReturn(Optional.empty());
        Optional<AlbumRequestDto> dto = requestService.getRequestById(3l);

        assertEquals(dto,Optional.empty());
    }

    @Test
    void getClaimByRequestId() {
        List<Claim> list = new ArrayList<>();
        list.add(claim);
        request2.setClaim(list);

        when(requestRepository.findById(2L)).thenReturn(Optional.ofNullable(request2));

        Request request = requestRepository.findById(2L).get();
        ClaimDto dto = requestService.getClaimByRequestId(2l).get();

        assertEquals(request.getClaim().get(0).getId(),dto.getId());
        assertEquals(request.getClaim().get(0).getRequest().getId(),dto.getRequestId());
        assertEquals(request.getClaim().get(0).getUser().getId(),dto.getUserId());
    }

    @Test
    void getClaimByRequestIdReturnsEmptyRequest() {
        when(requestRepository.findById(3l)).thenReturn(Optional.empty());
        Optional<ClaimDto> dto = requestService.getClaimByRequestId(3l);

        assertEquals(dto,Optional.empty());
    }

    @Test
    void getClaimByRequestIdReturnsEmptyClaim() {
        when(requestRepository.findById(3l)).thenReturn(Optional.ofNullable(request3));
        Optional<ClaimDto> dto = requestService.getClaimByRequestId(3l);

        assertEquals(dto,Optional.empty());
    }

    @Test
    void createClaim() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        CustomUserDetails principal = new CustomUserDetails("admin@wilbertbongers", "aPassword", true,authorities);

        when(userRepository.findByUsername(principal.getUsername())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.ofNullable(request1));
        when(auth.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(auth);
        lenient().when(claimRepository.save(claim)).thenReturn(claim);

        requestService.createClaim(1l);

        verify(requestRepository, times(1)).save(argumentRequestCaptor.capture());
        Request request = argumentRequestCaptor.getValue();

        verify(claimRepository, times(1)).save(argumentClaimCaptor.capture());
        Claim claim = argumentClaimCaptor.getValue();

        assertEquals(claim.getRequest().getState(),StateType.RECORD);
        assertEquals(claim.getUser(),user);
        assertEquals(claim.getRequest(),request);
    }

    @Test
    void createClaimThrowsNoInstanceOf() {

        when(auth.getPrincipal()).thenThrow(RuntimeException.class);
        when(securityContext.getAuthentication()).thenReturn(auth);

        assertThrows(RuntimeException.class, () -> {
            requestService.createClaim(1l);
        });
    }

    @Test
    void createClaimThrowsCannotCreate() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        CustomUserDetails principal = new CustomUserDetails("admin@wilbertbongers", "aPassword", true,authorities);

        when(userRepository.findByUsername(principal.getUsername())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.ofNullable(request1));
        when(auth.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(auth);
        when(claimRepository.save(claim)).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> {
            requestService.createClaim(1l);
        });
    }

    @Test
    void createClaimThrowsRequestNotFound() {
        Set<GrantedAuthority> authorities = new HashSet<>();
        CustomUserDetails principal = new CustomUserDetails("admin@wilbertbongers", "aPassword", true,authorities);

        when(userRepository.findByUsername(principal.getUsername())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.findById(1L)).thenReturn(Optional.empty());
        when(auth.getPrincipal()).thenReturn(principal);
        when(securityContext.getAuthentication()).thenReturn(auth);

        assertThrows(RuntimeException.class, () -> {
            requestService.createClaim(1l);
        });
    }

    @Test
    void createRequest() {
        lenient().when(requestRepository.save(request2)).thenReturn(request2);
        requestService.createRequest(arDto2);

        verify(requestRepository, times(1)).save(argumentRequestCaptor.capture());
        Request request = argumentRequestCaptor.getValue();

        assertEquals(request.getId(), 0);
        assertEquals(request.getState(), StateType.NEW);
        assertEquals(request.getDateCreated(), arDto2.getRequestDate());
        assertEquals(request.getDateLastChanged(), arDto2.getDateLastChanged());
        assertEquals(request.getAlbum().getTitle(), arDto2.getTitle());
        assertEquals(request.getAlbum().getArtist(), arDto2.getArtist());
        assertEquals(request.getAlbum().getDiscAmount(), arDto2.getDiscAmount());
    }

    @Test
    void createRequestThrowsFailedToCreate() {
        when(requestRepository.save(request2)).thenReturn(request2);

        assertThrows(RuntimeException.class, () -> {
            requestService.createRequest(arDto2);
        });
    }

    @Test
    void createQuality() {
        Recording rec = new Recording();

        when(requestRepository.findById(3l)).thenReturn(Optional.ofNullable(request3));
        lenient().when(qualityRepository.save(qual)).thenReturn(qual);
        when(recordingRepository.findRecordingsByAlbumAndDiscnumber(3l,1)).thenReturn(Optional.of(rec));

        requestService.createQuality(qualDto, 3l);

        verify(requestRepository, times(1)).save(argumentRequestCaptor.capture());
        Request request = argumentRequestCaptor.getValue();

        verify(qualityRepository, times(1)).save(argumentQualityCaptor.capture());
        Quality quality = argumentQualityCaptor.getValue();

        verify(recordingRepository, times(1)).save(argumentRecordingCaptor.capture());
        Recording recording = argumentRecordingCaptor.getValue();

        assertEquals(request.getState(), StateType.FINISHED);
        assertEquals(quality.getPoints(), qualDto.getPoints());
        assertEquals(quality.getDiscnumber(), qualDto.getDiscnumber());
        assertEquals(quality.getMessage(), qualDto.getMessage());
    }

    @Test
    void createQualityThrowsNoRecording() {
        when(requestRepository.findById(3l)).thenReturn(Optional.ofNullable(request3));
        lenient().when(qualityRepository.save(qual)).thenReturn(qual);
        when(recordingRepository.findRecordingsByAlbumAndDiscnumber(3l,1)).thenReturn(Optional.empty());


        assertThrows(RuntimeException.class, () -> {
            requestService.createQuality(qualDto, 3l);
        });

    }

    @Test
    void createQualityThrowsNoRequest() {
        when(requestRepository.findById(3l)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            requestService.createQuality(qualDto, 3l);
        });
    }

    @Test
    void deleteClaim() {
        List<Claim> list = new ArrayList<>();
        list.add(claim);
        request2.setClaim(list);

        lenient().when(requestRepository.findById(2L)).thenReturn(Optional.ofNullable(request2));

        requestService.deleteClaim(2L);
        verify(claimRepository).deleteById(0L);
    }

    @Test
    void deleteClaimThrowsNoClaimFound() {

        lenient().when(requestRepository.findById(2L)).thenReturn(Optional.ofNullable(request2));

        assertThrows(RuntimeException.class, () -> {
            requestService.deleteClaim(2L);
        });

    }

    @Test
    void deleteClaimThrowsError() {
        List<Claim> list = new ArrayList<>();
        list.add(claim);

        request2.setClaim(list);
        lenient().when(requestRepository.findById(2L)).thenReturn(Optional.ofNullable(request2));
        doThrow(new RuntimeException()).when(claimRepository).deleteById(0l);

        assertThrows(RuntimeException.class, () -> {
            requestService.deleteClaim(2L);
        });
    }

    @Test
    void alterRequest() {
        String newTitle = "Kind of blues";
        AlbumDto album = new AlbumDto();
        album.setTitle(newTitle);
        when(requestRepository.findById(2l)).thenReturn(Optional.ofNullable(request2));
        lenient().when(requestRepository.save(request2)).thenReturn(request2);
        requestService.alterRequest(2l, album);

        verify(requestRepository, times(1)).save(argumentRequestCaptor.capture());
        Request request = argumentRequestCaptor.getValue();

        assertEquals(request.getId(), arDto2.getId());
        assertEquals(request.getState(), arDto2.getState());
        assertEquals(request.getDateCreated(), arDto2.getRequestDate());
        assertEquals(request.getDateLastChanged(), arDto2.getDateLastChanged());
        assertEquals(request.getAlbum().getTitle(), newTitle);
        assertEquals(request.getAlbum().getArtist(), arDto2.getArtist());
        assertEquals(request.getAlbum().getDiscAmount(), arDto2.getDiscAmount());
    }
    @Test
    void alterRequestThrowsError() {
        String newTitle = "Kind of blues";
        AlbumDto album = new AlbumDto();
        album.setTitle(newTitle);
        when(requestRepository.findById(3l)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> {
            requestService.alterRequest(3l, album);
        });
    }
    @Test
    void alterRequestThrowsNoRequestFound() {
        String newTitle = "Kind of blues";
        AlbumDto album = new AlbumDto();
        album.setTitle(newTitle);
        when(requestRepository.findById(2L)).thenReturn(Optional.ofNullable(request2));

        doThrow(new RuntimeException()).when(requestRepository).save(request2);

        assertThrows(RuntimeException.class, () -> {
            requestService.alterRequest(3l, album);
        });
    }
}