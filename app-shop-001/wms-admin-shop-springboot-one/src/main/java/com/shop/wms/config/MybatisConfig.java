package com.shop.wms.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.shop.wms.mapper")
public class MybatisConfig {
}
