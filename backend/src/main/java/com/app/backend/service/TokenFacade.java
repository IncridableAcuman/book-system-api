package com.app.backend.service;

import com.app.backend.dto.AuthResponse;
import com.app.backend.entity.User;
import com.app.backend.exception.UnauthorizeException;
import com.app.backend.util.CookieUtil;
import com.app.backend.util.JwtUtil;
import com.app.backend.util.MailUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TokenFacade {
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final TokenService tokenService;
    private final MailUtil mailUtil;

    public AuthResponse issueTokens(User user, HttpServletResponse response){
        String accessToken = jwtUtil.getAccessToken(user);
        String refreshToken = jwtUtil.getRefreshToken(user);
        tokenService.saveToken(user,refreshToken);
        cookieUtil.addCookie(refreshToken,response);
        return AuthResponse.from(user,accessToken);
    }
    public String extractSubjectFromToken(String token){
        return jwtUtil.extractSubject(token);
    }
    public void validateToken(String token){
        if (!jwtUtil.validateToken(token)){
            throw new UnauthorizeException("Token is invalid or expired");
        }
    }
    public void deleteTokenAndClearCookie(User user,HttpServletResponse response){
        tokenService.delete(user.getId());
        cookieUtil.clearCookie(response);
    }
    public void resetToken(User user){
        String token = jwtUtil.getAccessToken(user);
        String uri = "http://localhost:5173/reset-password?token="+token;
        mailUtil.sendMail(user.getEmail(),"Reset Password",uri);
    }
}
