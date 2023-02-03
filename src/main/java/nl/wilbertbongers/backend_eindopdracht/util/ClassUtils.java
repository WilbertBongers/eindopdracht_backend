package nl.wilbertbongers.backend_eindopdracht.util;

import nl.wilbertbongers.backend_eindopdracht.dto.AlbumRequestDto;
import nl.wilbertbongers.backend_eindopdracht.dto.ClaimDto;
import nl.wilbertbongers.backend_eindopdracht.dto.QualityDto;
import nl.wilbertbongers.backend_eindopdracht.dto.UserDto;
import nl.wilbertbongers.backend_eindopdracht.model.*;

import java.util.ArrayList;
import java.util.List;

public class ClassUtils {

    public static List<AlbumRequestDto> createAlbumRequestDtoList(List<Request> requestList) {
        List<AlbumRequestDto> list = new ArrayList<>();

        for(Request request : requestList) {

            list.add(createSingleAlbumRequestDto(request));
        }

        return list;
    }
    public static AlbumRequestDto createSingleAlbumRequestDto(Request request) {
        List<String> recordings = new ArrayList<>();
        AlbumRequestDto dto = new AlbumRequestDto();
        dto.setId(request.getId());
        dto.setArtist(request.getAlbum().getArtist());
        dto.setTitle(request.getAlbum().getTitle());
        dto.setDiscAmount(request.getAlbum().getDiscAmount());
        dto.setRequestDate(request.getDateCreated());
        dto.setDateLastChanged(request.getDateLastChanged());
        dto.setState(request.getState());
        if(request.getAlbum().getRecordingList().size() > 0) {
            for(Recording rec : request.getAlbum().getRecordingList()) {
                recordings.add(rec.getFilelocation());
            }
            dto.setRecordings(recordings);
        }
        dto.setClaimed(!request.getClaim().isEmpty());

        return dto;
    }
    public static User createSingleUser(UserDto userDto) {
        User user = new User();
        user.setUsername(userDto.getEmailadress());
        user.setPassword(userDto.getPassword());
        user.setCity(userDto.getCity());
        user.setDateOfBirth(userDto.getDateOfBirth());
        user.setCountry(userDto.getCountry());
        user.setStreet(userDto.getStreet());
        user.setNumber(userDto.getNumber());
        user.setNiceName(userDto.getNiceName());
        user.setPostalCode(userDto.getPostalCode());
        user.setActive(true);
        return user;
    }
    public static UserDto createSingleUserDto (User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmailadress(user.getUsername());
        userDto.setPassword("**************");
        userDto.setCity(user.getCity());
        userDto.setDateOfBirth(user.getDateOfBirth());
        userDto.setCountry(user.getCountry());
        userDto.setStreet(user.getStreet());
        userDto.setNumber(user.getNumber());
        userDto.setNiceName(user.getNiceName());
        userDto.setPostalCode(user.getPostalCode());
        userDto.setTelephoneNumber(user.getTelephoneNumber());
        userDto.setRole(user.getRole().getName());

        return userDto;
    }
    public static List<UserDto> createUserDtoList(List<User> userList) {
        List<UserDto> list = new ArrayList<>();

        for(User user : userList) {

            list.add(createSingleUserDto(user));
        }

        return list;
    }
    public static ClaimDto createClaimDto(Claim claim) {
        return new ClaimDto(claim.getId(),claim.getRequest().getId(),claim.getUser().getId());
    }
    public static QualityDto createQualityDto(Quality quality) {
        return new QualityDto(quality.getId(), quality.getPoints(), quality.getMessage(), quality.getDiscnumber());
    }
}
