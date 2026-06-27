package com.shop.wms.controller.api;

import com.shop.wms.model.User;
import com.shop.wms.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Validated
public class UserApiController {

    private final UserService userService;

    @GetMapping
    public List<User> list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "false") boolean includeInactive) {
        List<User> users = userService.search(keyword, includeInactive);
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    @GetMapping("/{id}")
    public User get(@PathVariable Long id) {
        User user = userService.getById(id);
        user.setPassword(null);
        return user;
    }

    @PostMapping
    public User create(@Valid @RequestBody UserForm form) {
        User user = form.toUser();
        return userService.save(user, form.getPassword(), true);
    }

    @PutMapping("/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody UserForm form) {
        User user = form.toUser();
        user.setId(id);
        return userService.save(user, form.getPassword(), false);
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Map<String, Object>> deactivate(@PathVariable Long id) {
        userService.deactivate(id);
        Map<String, Object> body = new HashMap<>();
        body.put("message", "ユーザーを無効化しました");
        body.put("id", id);
        return ResponseEntity.ok(body);
    }

    @Data
    public static class UserForm {
        private Long id;

        @NotNull
        private String username;
        private String password;

        @NotNull
        private String email;

        @NotNull
        private User.Role role;

        private String avatarUrl;
        private Boolean active;

        User toUser() {
            return User.builder()
                    .id(id)
                    .username(username)
                    .email(email)
                    .role(role)
                    .avatarUrl(avatarUrl)
                    .active(active)
                    .build();
        }
    }
}
