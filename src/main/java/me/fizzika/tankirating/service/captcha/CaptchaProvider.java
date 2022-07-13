package me.fizzika.tankirating.service.captcha;

public interface CaptchaProvider {

    boolean validate(String captcha);

}
