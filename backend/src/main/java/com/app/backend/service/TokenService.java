package com.app.backend.service;

import com.app.backend.entity.User;
import com.app.backend.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class TokenService {
    private final RedisTemplate<String,Object> template;

    @Value("${jwt.refresh_time}")
    private int refreshTime;


// saving token
    public void saveToken(User user,String refreshToken){
        if (user == null || user.getId() == null){
            throw new BadRequestException("User or User ID is null");
        }
        if (refreshToken == null || refreshToken.isEmpty()){
            throw new BadRequestException("Token is empty or null");
        }
        String key = getKey(user.getId());
        template.opsForValue().set(
                key,
                refreshToken,
                refreshTime,
                TimeUnit.MILLISECONDS
        );
    }
//    get token from cache
    public String get(Long userId){
        if (userId == null) return null;
        Object token = template.opsForValue().get(getKey(userId));
        return token != null ? token.toString() : null;
    }
//    delete token from cache
    public void delete(Long userId){
        if (userId == null) return;
        template.delete(getKey(userId));
    }
//    get token via key
    public String getKey(Long userId){
        return "refreshToken:" + userId;
    }
}
