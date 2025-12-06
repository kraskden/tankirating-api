package me.fizzika.tankirating.config.security;

import me.fizzika.tankirating.service.captcha.CaptchaService;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.annotation.Resource;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class CaptchaFilter implements Filter {

    @Resource
    private CaptchaService captchaService;

    private static final String CAPTCHA_HEADER = "X-Captcha";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws ServletException, IOException {
        if (!(request instanceof HttpServletRequest)) {
            chain.doFilter(request, response);
            return;
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        if (securityContext.getAuthentication() == null) {
            var httpRequest = (HttpServletRequest) request;
            String captcha = httpRequest.getHeader(CAPTCHA_HEADER);
            if(captchaService.getProvider().validate(captcha)) {
                securityContext.setAuthentication(new CaptchaAuthentication());
            }
        }
        chain.doFilter(request, response);
    }

}