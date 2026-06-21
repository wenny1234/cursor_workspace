package com.shop.backend.mapper;

import com.shop.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Optional;

@Mapper
public interface UserMapper {
    Optional<User> findById(@Param("id") Long id);

    Optional<User> findByUsername(@Param("username") String username);

    Optional<User> findByEmail(@Param("email") String email);

    List<User> findAll();

    int insert(User user);

    int update(User user);

    int deleteById(@Param("id") Long id);

    int deleteAll();

    int insertWithId(User user);

    Long selectMaxId();

    long count();

    int existsByUsername(@Param("username") String username);

    int existsByEmail(@Param("email") String email);
}
