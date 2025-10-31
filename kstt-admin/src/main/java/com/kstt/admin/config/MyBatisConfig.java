package com.kstt.admin.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis配置类
 */
@Configuration
@MapperScan(basePackages = {
    "com.kstt.sys.mapper",
    "com.kstt.journal.mapper", 
    "com.kstt.articles.mapper",
    "com.kstt.notices.mapper",
    "com.kstt.contact.mapper",
    "com.kstt.about.mapper",
    "com.kstt.common.mapper"
})
public class MyBatisConfig {
}

