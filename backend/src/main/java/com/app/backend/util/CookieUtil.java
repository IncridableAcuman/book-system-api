package com.app.backend.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.refresh_time}")
    private int refreshTime;

    @Value("${cookie.secure:false}")
    private boolean cookieSecure;

    @Value("${cookie.same-site:Lax}")
    private String sameSite;

    public void addCookie(String refreshToken, HttpServletResponse response){
        Cookie cookie = new Cookie("refreshToken", refreshToken);
        cookie.setHttpOnly(true);
        cookie.setValue(refreshToken);
        cookie.setMaxAge(refreshTime / 1000);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        try {
            cookie.setAttribute("SameSite", sameSite);
        } catch (NoSuchMethodError ignored) {
            // In case of older servlet runtime
        }
        response.addCookie(cookie);
    }

    public void clearCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("refreshToken", null);
        cookie.setHttpOnly(true);
        cookie.setValue(null);
        cookie.setMaxAge(0);
        cookie.setSecure(cookieSecure);
        cookie.setPath("/");
        try {
            cookie.setAttribute("SameSite", sameSite);
        } catch (NoSuchMethodError ignored) {
            // In case of older servlet runtime
        }
        response.addCookie(cookie);
    }
}
