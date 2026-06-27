package com.shop.wms.service;

import com.shop.wms.mapper.UserMapper;
import com.shop.wms.model.User;
import com.shop.wms.util.PasswordValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public List<User> search(String keyword, boolean includeInactive) {
        return userMapper.search(keyword, includeInactive);
    }

    public User getById(Long id) {
        return userMapper.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("ユーザーが見つかりません: " + id));
    }

    @Transactional
    public User save(User user, String rawPassword, boolean isNew) {
        if (userMapper.existsByUsername(user.getUsername(), user.getId())) {
            throw new IllegalArgumentException("ユーザー名は既に使用されています");
        }
        if (userMapper.existsByEmail(user.getEmail(), user.getId())) {
            throw new IllegalArgumentException("メールアドレスは既に使用されています");
        }

        LocalDateTime now = LocalDateTime.now();
        if (isNew) {
            if (!StringUtils.hasText(rawPassword)) {
                throw new IllegalArgumentException("パスワードは必須です");
            }
            if (!PasswordValidator.isValid(rawPassword)) {
                throw new IllegalArgumentException(PasswordValidator.message());
            }
            user.setPassword(passwordEncoder.encode(rawPassword));
            user.setActive(user.getActive() == null || user.getActive());
            user.setCreatedAt(now);
            user.setUpdatedAt(now);
            userMapper.insert(user);
        } else {
            User existing = getById(user.getId());
            user.setCreatedAt(existing.getCreatedAt());
            user.setActive(user.getActive() == null ? existing.getActive() : user.getActive());
            if (StringUtils.hasText(rawPassword)) {
                if (!PasswordValidator.isValid(rawPassword)) {
                    throw new IllegalArgumentException(PasswordValidator.message());
                }
                user.setPassword(passwordEncoder.encode(rawPassword));
            } else {
                user.setPassword(existing.getPassword());
            }
            user.setUpdatedAt(now);
            userMapper.update(user);
        }
        user.setPassword(null);
        return user;
    }

    @Transactional
    public void deactivate(Long id) {
        getById(id);
        userMapper.deactivate(id, LocalDateTime.now());
    }

    public long countActive() {
        return userMapper.countActive();
    }
}
