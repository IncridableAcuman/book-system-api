package com.app.backend.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class CookieUtil {
    @Value("${jwt.refresh_time}")
    private int refreshTime;

    public void addCookie(String refreshToken, HttpServletResponse response){
        Cookie cookie = new Cookie("refreshToken",refreshToken);
        cookie.setHttpOnly(true);
        cookie.setValue(refreshToken);
        cookie.setMaxAge(refreshTime/1000);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
    public void clearCookie(HttpServletResponse response){
        Cookie cookie = new Cookie("refreshToken",null);
        cookie.setHttpOnly(true);
        cookie.setValue(null);
        cookie.setMaxAge(0);
        cookie.setSecure(false);
        cookie.setPath("/");
        response.addCookie(cookie);
    }
}
