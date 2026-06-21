package com.shop.backend.repository.csv;

import com.shop.backend.model.User;
import com.shop.backend.repository.UserRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@Profile("csv")
public class UserCsvRepository extends BaseCsvRepository<User> implements UserRepository {
    
    @Override
    protected Class<User> getEntityClass() {
        return User.class;
    }
    
    @Override
    protected String getCsvFileName() {
        return "users.csv";
    }
    
    @Override
    protected String[] getCsvHeaders() {
        return new String[]{"id", "username", "password", "email", "role", "created_at", "updated_at"};
    }
    
    @Override
    protected Long getEntityId(User entity) {
        return entity.getId();
    }
    
    @Override
    protected void setEntityId(User entity, Long id) {
        entity.setId(id);
    }
    
    @Override
    public Optional<User> findById(Long id) {
        return findAll().stream()
                .filter(user -> user.getId().equals(id))
                .findFirst();
    }
    
    @Override
    public Optional<User> findByUsername(String username) {
        return findAll().stream()
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }
    
    @Override
    public Optional<User> findByEmail(String email) {
        return findAll().stream()
                .filter(user -> user.getEmail().equals(email))
                .findFirst();
    }
    
    @Override
    public List<User> findAll() {
        return super.findAll();
    }
    
    @Override
    public User save(User user) {
        return super.save(user);
    }
    
    @Override
    public void deleteById(Long id) {
        super.deleteById(id);
    }
    
    @Override
    public boolean existsByUsername(String username) {
        return findAll().stream()
                .anyMatch(user -> user.getUsername().equals(username));
    }
    
    @Override
    public boolean existsByEmail(String email) {
        return findAll().stream()
                .anyMatch(user -> user.getEmail().equals(email));
    }
    
    @Override
    public long count() {
        return findAll().size();
    }
}