package pProject.pPro;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class PProApplication {

	public static void main(String[] args) {
		SpringApplication.run(PProApplication.class, args);
	}

}
