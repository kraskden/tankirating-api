package me.fizzika.tankirating.controller;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import me.fizzika.tankirating.config.security.CaptchaAuthentication;
import me.fizzika.tankirating.exceptions.ExternalException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@Tag(name = "Auth", description = "Authentication api")
@RequestMapping("/auth")
public class AuthController {

    @PostMapping
    public Principal auth(@Parameter(hidden = true) Principal principal) {
        if (principal instanceof CaptchaAuthentication) {
            throw new ExternalException("Not authenticated", HttpStatus.UNAUTHORIZED);
        }
        return principal;
    }
}
