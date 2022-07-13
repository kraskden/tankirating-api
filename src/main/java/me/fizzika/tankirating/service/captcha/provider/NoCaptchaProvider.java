package me.fizzika.tankirating.service.captcha.provider;

import me.fizzika.tankirating.service.captcha.CaptchaProvider;
import org.springframework.stereotype.Component;

@Component
public class NoCaptchaProvider implements CaptchaProvider {

    @Override
    public boolean validate(String captcha) {
        return true;
    }

}
