package com.shop.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.shop.admin.mapper")
public class MybatisConfig {
}
