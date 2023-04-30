package me.fizzika.tankirating.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String DESCRIPTION =
            "REST API for https://tankirating.org\n\n" +
                    "Usage notes: \n" +
                    "When you are encounter urls with form: /{period}/{offset}, threat " +
                    "offset as how many periods (days, weeks, years, etc) are passed from today.\n" +
                    "So if you wanna get something for previous week, made request: /week/1 \n";

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info().title("TankiRating API")
                        .description(DESCRIPTION)
                        .version("v2.0")
                );
    }

}
