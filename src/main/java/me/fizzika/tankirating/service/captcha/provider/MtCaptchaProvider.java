package me.fizzika.tankirating.service.captcha.provider;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.dto.MtCaptchaResponseDTO;
import me.fizzika.tankirating.service.captcha.CaptchaProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Component
@RequiredArgsConstructor
public class MtCaptchaProvider implements CaptchaProvider {

    private static final String URL_TEMPLATE =
            "https://service.mtcaptcha.com/mtcv1/api/checktoken?privatekey={privateKey}&token={captcha}";

    private final RestTemplate restTemplate;

    @Value("${app.mtcaptcha.private_key}")
    private String privateKey;

    @Override
    public boolean validate(String captcha) {
        if (isBlank(captcha)) {
            return false;
        }

        var response = restTemplate.getForObject(URL_TEMPLATE, MtCaptchaResponseDTO.class,
                privateKey, captcha);
        return response != null && response.isSuccess();
    }

}
