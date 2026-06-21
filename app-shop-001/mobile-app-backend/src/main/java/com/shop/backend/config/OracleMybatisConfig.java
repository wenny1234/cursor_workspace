package com.shop.backend.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("oracle")
@MapperScan("com.shop.backend.mapper")
public class OracleMybatisConfig {
}
