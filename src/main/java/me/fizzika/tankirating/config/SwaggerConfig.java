package me.fizzika.tankirating.config;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "auth", scheme = "basic")
public class SwaggerConfig {

    private static final String DESCRIPTION =
            "REST API for https://tankirating.org\n\n" +
                    "Usage notes: \n" +
                    "When you are encounter urls with form: /{period}/{offset}, threat " +
                    "offset as how many periods (days, weeks, years, etc) are passed from today.\n" +
                    "So if you wanna get something for previous week, made request: /week/1 \n";

    @Value("${app.swagger.server-url}")
    private String serverUrl;

    @Bean
    public OpenAPI api() {
        List<Server> servers = List.of(buildServer(serverUrl));
        return new OpenAPI()
                .info(new Info().title("TankiRating API")
                        .description(DESCRIPTION)
                        .version("v2.0"))
                .servers(servers);
    }

    private Server buildServer(String url) {
        var server = new Server();
        server.setUrl(url);
        return server;
    }
}
