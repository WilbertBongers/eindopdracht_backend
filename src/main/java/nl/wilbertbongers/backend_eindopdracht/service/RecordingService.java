package nl.wilbertbongers.backend_eindopdracht.service;

import nl.wilbertbongers.backend_eindopdracht.dto.RecordingDto;
import nl.wilbertbongers.backend_eindopdracht.model.*;
import nl.wilbertbongers.backend_eindopdracht.repository.*;
import nl.wilbertbongers.backend_eindopdracht.security.CustomUserDetails;
import nl.wilbertbongers.backend_eindopdracht.util.StateType;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.FileNameMap;
import java.net.MalformedURLException;
import java.net.URLConnection;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

@Service
public class RecordingService {

    private final Path root = Paths.get("uploads");

    private final RecordingRepository recordingRepository;
    private final RecordingTechnologyRepository recordingTechnologyRepository;
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final ClaimRepository claimRepository;

    public RecordingService(
            RecordingRepository recordingRepository,
            RecordingTechnologyRepository recordingTechnologyRepository,
            RequestRepository requestRepository,
            UserRepository userRepository,
            ClaimRepository claimRepository) {

        this.recordingRepository = recordingRepository;
        this.recordingTechnologyRepository = recordingTechnologyRepository;
        this.requestRepository = requestRepository;
        this.claimRepository = claimRepository;
        this.userRepository = userRepository;
        init();
    }

    public void init() {
        try {
            Files.createDirectories(root);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize folder for upload!");
        }
    }

    public boolean create(
            Long id,
            RecordingDto recordingDto
            ) throws Exception{
        Optional<Request> requestOpt = requestRepository.findById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        UserDetails ud = null;

        if (auth.getPrincipal() instanceof CustomUserDetails) {
             ud = (UserDetails) auth.getPrincipal();
        }

        Optional<User> userOpt = userRepository.findByUsername(ud.getUsername());
        Optional<RecordingTechnology> recTechOpt = recordingTechnologyRepository.findBySlug(recordingDto.recordingtechnology);

        if (requestOpt.isPresent() && requestOpt.get().getState() == StateType.RECORD && recTechOpt.isPresent()) {

            Request request = requestOpt.get();
            Optional<Claim> claimOpt = claimRepository.findClaimByRequestId(request.getId());

            Album album = request.getAlbum();
            Optional<Recording> recordingOpt = recordingRepository.findRecordingsByAlbumAndDiscnumber(album.getId(), recordingDto.discnumber);

            if (recordingOpt.isEmpty()) {
                InputStream stream = null;
                try {
                    stream = recordingDto.file.getInputStream();
                    String extension = FilenameUtils.getExtension(recordingDto.file.getOriginalFilename());
                    String filename = "request"+id+"disc"+recordingDto.discnumber+"."+extension;
                    Path path = this.root.resolve(filename);
                    Files.copy(stream, path);

                    FileNameMap fileNameMap = URLConnection.getFileNameMap();
                    String mimeType = fileNameMap.getContentTypeFor(String.valueOf(path));

                    Recording record = new Recording();
                    record.setDiscnumber(recordingDto.discnumber);
                    record.setFilelocation(String.valueOf(path));
                    record.setAlbum(album);
                    record.setUser(userOpt.get());
                    record.setRecordingTechnology(recTechOpt.get());
                    recordingRepository.save(record);

                    request.setState(StateType.QUALITY);
                    request.setDateLastChanged(Date.valueOf(LocalDate.now()));
                    requestRepository.save(request);

                    claimRepository.deleteById(claimOpt.get().getId());

                } catch (Exception e) {
                    if (e instanceof FileAlreadyExistsException) {
                        throw new RuntimeException("A file of that name already exists.");
                    }
                    throw new RuntimeException(e.getMessage());
                } finally {
                    try { stream.close(); } catch (Exception ignore) {}
                }
            } else {
                throw new RuntimeException("File exist");
            }
        } else {
            String message = "";
            if (requestOpt.isEmpty()) {
                message = "Request does not exist";
            } else if (requestOpt.get().getState() != StateType.RECORD) {
                message = "Request is not in the right state (RECORD), current state is "+requestOpt.get().getState();
            } else {
                message = "Recording Technology is not valid";
            }
            throw new RuntimeException(message);
        }
        return true;
    }

    public Resource load(String filename) {
        try {
            Path file = root.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() || resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Could not read the file!");
            }
        } catch (MalformedURLException e) {
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    public void deleteAll() {
        FileSystemUtils.deleteRecursively(root.toFile());
    }
}
