package nl.wilbertbongers.backend_eindopdracht.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import nl.wilbertbongers.backend_eindopdracht.dto.*;
import nl.wilbertbongers.backend_eindopdracht.security.JwtRequestFilter;
import nl.wilbertbongers.backend_eindopdracht.service.RecordingService;
import nl.wilbertbongers.backend_eindopdracht.service.RequestService;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.isNotNull;
import static org.mockito.BDDMockito.given;

import nl.wilbertbongers.backend_eindopdracht.service.UserService;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(SpringExtension.class)
@WebMvcTest(RequestController.class)
@AutoConfigureMockMvc(addFilters = false)
class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper mapper;
    @MockBean
    private RequestService requestService;
    @MockBean
    private UserService userService;
    @MockBean
    private RecordingService recordingService;
    @MockBean
    private JwtRequestFilter jwtRequestFilter;
    AlbumRequestDto arDto1;
    AlbumRequestDto arDto2;
    AlbumRequestDto arDto3;
    AlbumRequestDto alteredAR;
    AlbumDto albumDto;
    ClaimDto claimDto;
    QualityDto qualityDto;

    static MockMultipartFile file = new MockMultipartFile("test.MP3","test.MP3", "audio/mpeg", "/resources/test.MP3".getBytes());
    static RecordingDto recordingDto = new RecordingDto(file,1,"16-44");

    Date now;
    List<String> list;
    boolean isClaimed;

    @BeforeEach

    public void setUp() {
        now = Date.valueOf(LocalDate.now());
        list = new ArrayList<>();
        isClaimed = false;
        list.add("test1");
        list.add("test2");
        arDto1 = new AlbumRequestDto(1L,"Purple rain" ,"Prince",1, now,now, StateType.NEW, isClaimed, list);
        arDto2 = new AlbumRequestDto(2L,"Do you like my tight sweater" ,"Moloko",1, now, now,StateType.RECORD, isClaimed, list);
        arDto3 = new AlbumRequestDto(3L,"Kind of blue" ,"Miles Davis",1, now, now, StateType.QUALITY, isClaimed, list);
        alteredAR = new AlbumRequestDto(3L,"Kind of blues" ,"Miles Davis",1, now, now, StateType.QUALITY, isClaimed, list);
        albumDto = new AlbumDto();
        claimDto = new ClaimDto(1l,1L,1L);
        qualityDto = new QualityDto(1L,50,"fabelhaft",1);

    }
    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void getAllRequests() throws Exception{
        given(requestService.getRequests(Optional.empty(), Optional.empty())).willReturn(List.of(arDto1, arDto2,arDto3));

        ResultActions resultActions = mockMvc.perform(get("/requests"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("1"))
                .andExpect(jsonPath("$[0].title").value("Purple rain"))
                .andExpect(jsonPath("$[0].artist").value( "Prince"))
                .andExpect(jsonPath("$[0].discAmount").value(Integer.valueOf(1)))
                .andExpect(jsonPath("$[0].requestDate").value(now.toString()))
                .andExpect(jsonPath("$[0].dateLastChanged").value( now.toString()))
                .andExpect(jsonPath("$[0].state").value( StateType.NEW.toString()))
                .andExpect(jsonPath("$[0].claimed").value(isClaimed))
                .andExpect(jsonPath("$[0].recordings").value(list))
                .andExpect(jsonPath("$[1].id").value("2"))
                .andExpect(jsonPath("$[1].title").value("Do you like my tight sweater"))
                .andExpect(jsonPath("$[1].artist").value( "Moloko"))
                .andExpect(jsonPath("$[1].discAmount").value(Integer.valueOf(1)))
                .andExpect(jsonPath("$[1].requestDate").value(now.toString()))
                .andExpect(jsonPath("$[1].dateLastChanged").value( now.toString()))
                .andExpect(jsonPath("$[1].state").value( StateType.RECORD.toString()))
                .andExpect(jsonPath("$[1].claimed").value(isClaimed))
                .andExpect(jsonPath("$[1].recordings").value(list));
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void getAllRequestsByState() throws Exception{
        given(requestService.getRequestsByState(StateType.valueOf("RECORD"), Optional.empty())).willReturn(List.of(arDto2));
        ResultActions resultActions = mockMvc.perform(get("/requests/state/record"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[0].title").value("Do you like my tight sweater"))
                .andExpect(jsonPath("$[0].artist").value( "Moloko"))
                .andExpect(jsonPath("$[0].discAmount").value(Integer.valueOf(1)))
                .andExpect(jsonPath("$[0].requestDate").value(now.toString()))
                .andExpect(jsonPath("$[0].dateLastChanged").value( now.toString()))
                .andExpect(jsonPath("$[0].state").value( StateType.RECORD.toString()))
                .andExpect(jsonPath("$[0].claimed").value(isClaimed))
                .andExpect(jsonPath("$[0].recordings").value(list));
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void getAllRequestsCurrentlyClaimed() throws Exception{
        given(requestService.getClaimedRequests()).willReturn(List.of(arDto2));
        ResultActions resultActions = mockMvc.perform(get("/requests/claimed"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[0].title").value("Do you like my tight sweater"))
                .andExpect(jsonPath("$[0].artist").value( "Moloko"))
                .andExpect(jsonPath("$[0].discAmount").value(Integer.valueOf(1)))
                .andExpect(jsonPath("$[0].requestDate").value(now.toString()))
                .andExpect(jsonPath("$[0].dateLastChanged").value( now.toString()))
                .andExpect(jsonPath("$[0].state").value( StateType.RECORD.toString()))
                .andExpect(jsonPath("$[0].claimed").value(isClaimed))
                .andExpect(jsonPath("$[0].recordings").value(list));
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void getAllRequestsForWorklist() throws Exception{
        given(requestService.getUnclaimedRequests()).willReturn(List.of(arDto2));
        ResultActions resultActions = mockMvc.perform(get("/requests/worklist"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("2"))
                .andExpect(jsonPath("$[0].title").value("Do you like my tight sweater"))
                .andExpect(jsonPath("$[0].artist").value( "Moloko"))
                .andExpect(jsonPath("$[0].discAmount").value(Integer.valueOf(1)))
                .andExpect(jsonPath("$[0].requestDate").value(now.toString()))
                .andExpect(jsonPath("$[0].dateLastChanged").value( now.toString()))
                .andExpect(jsonPath("$[0].state").value( StateType.RECORD.toString()))
                .andExpect(jsonPath("$[0].claimed").value(isClaimed))
                .andExpect(jsonPath("$[0].recordings").value(list));
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void getAllRequestsForQualitylist() throws Exception{

        given(requestService.getAllRequestsForQC()).willReturn(List.of(arDto3));
        ResultActions resultActions = mockMvc.perform(get("/requests/qualitylist"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value("3"))
                .andExpect(jsonPath("$[0].title").value("Kind of blue"))
                .andExpect(jsonPath("$[0].artist").value( "Miles Davis"))
                .andExpect(jsonPath("$[0].discAmount").value(Integer.valueOf(1)))
                .andExpect(jsonPath("$[0].requestDate").value(now.toString()))
                .andExpect(jsonPath("$[0].dateLastChanged").value( now.toString()))
                .andExpect(jsonPath("$[0].state").value( StateType.QUALITY.toString()))
                .andExpect(jsonPath("$[0].claimed").value(isClaimed))
                .andExpect(jsonPath("$[0].recordings").value(list));
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void getRequestById() throws Exception{
        given(requestService.getRequestById(1)).willReturn(Optional.ofNullable(arDto1));

        mockMvc.perform(get("/requests/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void getClaimByRequestId() throws Exception{

        given(requestService.getClaimByRequestId(2)).willReturn(Optional.ofNullable(claimDto));
        mockMvc.perform(get("/requests/2/claim")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(""))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void postNewRequest() throws Exception {
        given(requestService.createRequest(arDto1)).willReturn(arDto1);

        mockMvc.perform(post("/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(arDto1)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void postBatchImportRequests() throws Exception{
        given(requestService.createRequest(arDto1)).willReturn(arDto1);
        List<AlbumRequestDto> list = Arrays.asList(arDto1, arDto2, arDto3);

        mockMvc.perform(post("/batch-import")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(list.toString()))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void postClaimRequest() throws Exception {
        given(requestService.createClaim(1l)).willReturn(claimDto);

        ResultActions resultActions = mockMvc.perform(post("/requests/claim/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(claimDto)))
                .andExpect(status().isCreated());

    }

    @Test
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void postQualityRequest() throws Exception{

        given(requestService.createQuality(qualityDto, 1l)).willReturn(qualityDto);
        mockMvc.perform(post("/requests/1/quality")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(qualityDto)))
                .andDo(print())
                .andExpect(status().isCreated());
    }

    @ParameterizedTest
    @MethodSource("generator")
    @WithMockUser(username="admin@wilbertbongers", roles="ADMIN")
    void postRecordingByRequestAndDiscnumber(RecordingDto recording, long id) throws Exception{
        given(recordingService.create(2L,recording)).willReturn(true);
        mockMvc.perform(MockMvcRequestBuilders.multipart("/requests/2/recording")
                .file((MockMultipartFile) recording.file)
                .param("discnumber", "1")
                .param("recordingtechnology", "16-44"));
    }

    @Test
    void alterRequest() throws Exception{

        albumDto.setTitle("Kind of blues");

        given(requestService.alterRequest(3l,albumDto)).willReturn(alteredAR);
        mockMvc.perform(put("/requests/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(asJsonString(albumDto)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteClaimRequest() throws Exception{

        mockMvc.perform(delete("/requests/2/claim"))
                .andExpect(content().string("Claim deleted!"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteClaimRequestThrowsError() throws Exception{
        given(requestService.deleteClaim(12L)).willThrow(new RuntimeException("No Claim found"));
        mockMvc.perform(delete("/requests/12/claim"))
                .andExpect(status().isBadRequest());
    }

    public static String asJsonString(final AlbumDto obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static String asJsonString(final AlbumRequestDto obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static String asJsonString(final QualityDto obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    public static String asJsonString(final ClaimDto obj) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mapper.registerModule(new JavaTimeModule());
            mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
            return mapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }
    private static Stream<Arguments> generator() {

        return Stream.of(
                Arguments.of(recordingDto, 2L));
    }
}