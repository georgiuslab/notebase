package chemilabi.trade.notebase_backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class NotebaseBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(NotebaseBackendApplication.class, args);
	}

}
