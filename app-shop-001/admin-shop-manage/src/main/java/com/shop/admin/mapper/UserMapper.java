package com.shop.admin.mapper;

import com.shop.admin.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findById(@Param("id") Long id);

    Optional<User> findByUsername(@Param("username") String username);

    List<User> search(@Param("keyword") String keyword, @Param("includeInactive") boolean includeInactive);

    int insert(User user);

    int update(User user);

    int deactivate(@Param("id") Long id, @Param("updatedAt") java.time.LocalDateTime updatedAt);

    boolean existsByUsername(@Param("username") String username, @Param("excludeId") Long excludeId);

    boolean existsByEmail(@Param("email") String email, @Param("excludeId") Long excludeId);

    long countActive();
}
