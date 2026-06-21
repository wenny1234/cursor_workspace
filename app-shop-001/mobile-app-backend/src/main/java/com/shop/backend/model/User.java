package com.shop.backend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvCustomBindByName;
import com.shop.backend.repository.csv.LocalDateTimeConverter;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @CsvBindByName(column = "ID")
    private Long id;
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须在3到50个字符之间")
    @CsvBindByName(column = "USERNAME", required = true)
    private String username;
    
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 100, message = "密码长度必须在6到100个字符之间")
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @CsvBindByName(column = "PASSWORD", required = true)
    private String password;
    
    @NotBlank(message = "邮箱不能为空")
    @Email(message = "邮箱格式不正确")
    @CsvBindByName(column = "EMAIL", required = true)
    private String email;
    
    @CsvBindByName(column = "ROLE")
    private Role role;
    
    @CsvCustomBindByName(column = "CREATEDAT", converter = LocalDateTimeConverter.class)
    private LocalDateTime createdAt;
    
    @CsvCustomBindByName(column = "UPDATEDAT", converter = LocalDateTimeConverter.class)
    private LocalDateTime updatedAt;
    
    @JsonIgnore
    public boolean isAdmin() {
        return Role.ADMIN.equals(role);
    }
    
    @JsonIgnore
    public boolean isStaff() {
        return Role.STAFF.equals(role);
    }
    
    @JsonIgnore
    public boolean isViewer() {
        return Role.VIEWER.equals(role);
    }
    
    public enum Role {
        ADMIN,
        STAFF,
        VIEWER
    }
}