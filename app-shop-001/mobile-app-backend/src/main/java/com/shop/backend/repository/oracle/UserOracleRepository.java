package com.shop.backend.repository.oracle;

import com.shop.backend.mapper.UserMapper;
import com.shop.backend.model.User;
import com.shop.backend.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@Profile("oracle")
public class UserOracleRepository implements UserRepository {

    private final UserMapper userMapper;

    public UserOracleRepository(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    @Override
    public Optional<User> findById(Long id) {
        return userMapper.findById(id);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userMapper.findByUsername(username);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMapper.findByEmail(email);
    }

    @Override
    public List<User> findAll() {
        return userMapper.findAll();
    }

    @Override
    public User save(User user) {
        if (user.getId() != null && userMapper.findById(user.getId()).isPresent()) {
            userMapper.update(user);
        } else {
            userMapper.insert(user);
        }
        return user;
    }

    @Override
    public void deleteById(Long id) {
        userMapper.deleteById(id);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userMapper.existsByUsername(username) > 0;
    }

    @Override
    public boolean existsByEmail(String email) {
        return userMapper.existsByEmail(email) > 0;
    }

    @Override
    public long count() {
        return userMapper.count();
    }
}
