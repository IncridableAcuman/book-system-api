package com.app.backend.service;

import com.app.backend.dto.*;
import com.app.backend.entity.User;
import com.app.backend.exception.BadRequestException;
import com.app.backend.exception.UnauthorizeException;
import com.app.backend.util.CookieUtil;
import com.app.backend.util.JwtUtil;
import com.app.backend.util.MailUtil;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final TokenService tokenService;
    private final JwtUtil jwtUtil;
    private final CookieUtil cookieUtil;
    private final PasswordEncoder passwordEncoder;
    private final MailUtil mailUtil;

    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response){
     User user = userService.create(request);
     String accessToken = jwtUtil.getAccessToken(user);
     String refreshToken = jwtUtil.getRefreshToken(user);
     tokenService.saveToken(user,refreshToken);
     cookieUtil.addCookie(refreshToken,response);
     return AuthResponse.from(user,accessToken);
    }

    public AuthResponse login(LoginRequest request,HttpServletResponse response){
        User user = userService.findUser(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new BadRequestException("Invalid password");
        }
        String accessToken = jwtUtil.getAccessToken(user);
        String refreshToken = jwtUtil.getRefreshToken(user);
        tokenService.saveToken(user,refreshToken);
        cookieUtil.addCookie(refreshToken,response);
        return AuthResponse.from(user,accessToken);
    }
    public AuthResponse refresh(String refreshToken,HttpServletResponse response){
        String email = jwtUtil.extractSubject(refreshToken);
        User user = userService.findUser(email);
        String cacheToken = tokenService.get(user.getId());
        if (cacheToken == null || !cacheToken.equals(refreshToken)){
            throw new UnauthorizeException("Invalid refreshToken");
        }
        String newAccessToken = jwtUtil.getAccessToken(user);
        String newRefreshToken = jwtUtil.getRefreshToken(user);
        tokenService.saveToken(user,newRefreshToken);
        cookieUtil.addCookie(newRefreshToken,response);
        return AuthResponse.from(user,newAccessToken);
    }
    @Transactional
    public void logout(String refreshToken,HttpServletResponse response){
        String email = jwtUtil.extractSubject(refreshToken);
        User user = userService.findUser(email);
        String cacheToken = tokenService.get(user.getId());
        if (cacheToken == null || !cacheToken.equals(refreshToken) || !jwtUtil.validateToken(refreshToken)){
            throw new UnauthorizeException("Invalid or expired token");
        }
        tokenService.delete(user.getId());
        cookieUtil.clearCookie(response);
    }
    public void forgotPassword(ForgotPasswordRequest request){
        User user = userService.findUser(request.getEmail());
        String token = jwtUtil.getAccessToken(user);
        String uri = "http://localhost:5173/reset-password?token="+token;
        mailUtil.sendMail(request.getEmail(),"Reset Password",uri);
    }
    public void resetPassword(ResetPasswordRequest request){
        if (!jwtUtil.validateToken(request.getToken())){
            throw new UnauthorizeException("Invalid or expired token");
        }
        String email = jwtUtil.extractSubject(request.getToken());
        User user = userService.findUser(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);
    }
}
