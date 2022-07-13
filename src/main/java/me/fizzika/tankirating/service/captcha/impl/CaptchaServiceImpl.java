package me.fizzika.tankirating.service.captcha.impl;

import lombok.RequiredArgsConstructor;
import me.fizzika.tankirating.service.captcha.CaptchaProvider;
import me.fizzika.tankirating.service.captcha.CaptchaService;
import me.fizzika.tankirating.service.captcha.provider.MtCaptchaProvider;
import me.fizzika.tankirating.service.captcha.provider.NoCaptchaProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CaptchaServiceImpl implements CaptchaService {

    private final MtCaptchaProvider mtCaptchaProvider;
    private final NoCaptchaProvider noCaptchaProvider;

    @Value("${app.captcha.enabled}")
    private Boolean useCaptcha;

    @Override
    public CaptchaProvider getProvider() {
        return useCaptcha ? mtCaptchaProvider : noCaptchaProvider;
    }

}
