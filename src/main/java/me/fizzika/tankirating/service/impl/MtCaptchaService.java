package me.fizzika.tankirating.service.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.MtCaptchaResponseDTO;
import me.fizzika.tankirating.exceptions.ExternalException;
import me.fizzika.tankirating.service.CaptchaService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class MtCaptchaService implements CaptchaService {

    private static final String URL_TEMPLATE =
            "https://service.mtcaptcha.com/mtcv1/api/checktoken?privatekey={privateKey}&token={captcha}";

    private final RestTemplate restTemplate;

    @Value("${app.mtcaptcha.private_key}")
    private String privateKey;

    @Override
    public boolean validate(String captcha) {
        var response = restTemplate.getForObject(URL_TEMPLATE, MtCaptchaResponseDTO.class,
                privateKey, captcha);
        return response != null && response.isSuccess();
    }

}
