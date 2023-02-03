package nl.wilbertbongers.backend_eindopdracht;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class BackendEindopdrachtApplication {

    public static void main(String[] args) {
        SpringApplication.run(BackendEindopdrachtApplication.class, args);
    }

}
