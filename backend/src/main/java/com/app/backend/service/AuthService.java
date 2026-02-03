package com.app.backend.service;

import com.app.backend.dto.*;
import com.app.backend.entity.User;
import com.app.backend.exception.BadRequestException;
import com.app.backend.exception.UnauthorizeException;
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
    private final PasswordEncoder passwordEncoder;
    private final TokenFacade tokenFacade;


    @Transactional
    public AuthResponse register(RegisterRequest request, HttpServletResponse response){
     User user = userService.create(request);
     return tokenFacade.issueTokens(user,response);
    }

    public AuthResponse login(LoginRequest request,HttpServletResponse response){
        User user = userService.findUser(request.getEmail());
        if (!passwordEncoder.matches(request.getPassword(),user.getPassword())){
            throw new BadRequestException("Invalid password");
        }
        return tokenFacade.issueTokens(user,response);
    }
    public AuthResponse refresh(String refreshToken,HttpServletResponse response){
        String email = tokenFacade.extractSubjectFromToken(refreshToken);
        User user = userService.findUser(email);
        String cacheToken = tokenService.get(user.getId());
        if (cacheToken == null || !cacheToken.equals(refreshToken)){
            throw new UnauthorizeException("Invalid refreshToken");
        }
        return tokenFacade.issueTokens(user,response);
    }
    @Transactional
    public void logout(String refreshToken,HttpServletResponse response){
        String email = tokenFacade.extractSubjectFromToken(refreshToken);
        User user = userService.findUser(email);
        String cacheToken = tokenService.get(user.getId());
        if (cacheToken == null || !cacheToken.equals(refreshToken)){
            throw new UnauthorizeException("Invalid or expired token");
        }
        tokenFacade.validateToken(refreshToken);
        tokenFacade.deleteTokenAndClearCookie(user,response);
    }
    public void forgotPassword(ForgotPasswordRequest request){
        User user = userService.findUser(request.getEmail());
        tokenFacade.resetToken(user);
    }
    public void resetPassword(ResetPasswordRequest request){
        tokenFacade.validateToken(request.getToken());
        String email = tokenFacade.extractSubjectFromToken(request.getToken());
        User user = userService.findUser(email);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        userService.saveUser(user);
    }
}
