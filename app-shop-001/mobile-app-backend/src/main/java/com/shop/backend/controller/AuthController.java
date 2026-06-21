package com.shop.backend.controller;

import com.shop.backend.dto.LoginRequest;
import com.shop.backend.dto.LoginResponse;
import com.shop.backend.model.User;
import com.shop.backend.repository.UserRepository;
import com.shop.backend.security.JwtUtils;
import com.shop.backend.security.UserDetailsImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtUtils jwtUtils;
    
    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String jwt = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);
        
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        return ResponseEntity.ok(LoginResponse.fromUser(user, jwt, refreshToken));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String authorizationHeader) {
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            return ResponseEntity.badRequest().body("无效的授权头");
        }
        
        String refreshToken = authorizationHeader.substring(7);
        
        if (!jwtUtils.validateToken(refreshToken) || !jwtUtils.isRefreshToken(refreshToken)) {
            return ResponseEntity.badRequest().body("无效的刷新令牌");
        }
        
        String username = jwtUtils.getUserNameFromToken(refreshToken);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        String newToken = jwtUtils.generateTokenFromUser(user);
        String newRefreshToken = jwtUtils.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(
                        UserDetailsImpl.build(user),
                        null,
                        UserDetailsImpl.build(user).getAuthorities()
                )
        );
        
        return ResponseEntity.ok(LoginResponse.fromUser(user, newToken, newRefreshToken));
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            return ResponseEntity.badRequest().body("邮箱已存在");
        }
        
        // 设置默认角色为VIEWER
        if (user.getRole() == null) {
            user.setRole(User.Role.VIEWER);
        }
        
        // 加密密码
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        user.setCreatedAt(now);
        user.setUpdatedAt(now);
        
        User savedUser = userRepository.save(user);
        
        // 生成token
        String jwt = jwtUtils.generateTokenFromUser(savedUser);
        String refreshToken = jwtUtils.generateRefreshToken(
                new UsernamePasswordAuthenticationToken(
                        UserDetailsImpl.build(savedUser),
                        null,
                        UserDetailsImpl.build(savedUser).getAuthorities()
                )
        );
        
        return ResponseEntity.ok(LoginResponse.fromUser(savedUser, jwt, refreshToken));
    }
    
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.badRequest().body("未认证");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new RuntimeException("用户不存在"));
        
        // 移除密码
        user.setPassword(null);
        
        return ResponseEntity.ok(user);
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "ok");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}