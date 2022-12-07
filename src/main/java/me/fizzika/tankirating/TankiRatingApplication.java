package me.fizzika.tankirating;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableJpaRepositories(basePackages = "me.fizzika.tankirating.repository")
@EnableMongoRepositories(basePackages = "me.fizzika.tankirating.v1_migration.repository")
@EnableAsync
@EnableScheduling
public class TankiRatingApplication {

	public static void main(String[] args) {
		SpringApplication.run(TankiRatingApplication.class, args);
	}

}
