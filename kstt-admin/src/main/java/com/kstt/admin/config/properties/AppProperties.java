package com.kstt.admin.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 应用自定义配置属性
 */
@Data
@Component
@ConfigurationProperties(prefix = "app")
public class AppProperties {

    private CacheProperties cache = new CacheProperties();
    private RabbitMqProperties rabbitmq = new RabbitMqProperties();
    private DataSourceProperties datasource = new DataSourceProperties();
    private PerformanceProperties performance = new PerformanceProperties();
    private MailProperties mail = new MailProperties();

    @Data
    public static class CacheProperties {
        private Map<String, String> names = new HashMap<>();
        private Map<String, Duration> ttl = new HashMap<>();
    }

    @Data
    public static class RabbitMqProperties {
        private ArticleStatusProperties articleStatus = new ArticleStatusProperties();

        @Data
        public static class ArticleStatusProperties {
            private String exchange;
            private String queue;
            private String routingKey;
            private boolean durable = true;
        }
    }

    @Data
    public static class DataSourceProperties {
        private List<String> readMethods;
    }

    @Data
    public static class PerformanceProperties {
        private long slowThresholdMs = 1000L;
        private long verySlowThresholdMs = 3000L;
        private boolean enableMethodMonitor = true;
        private boolean enableApiLog = true;
    }

    @Data
    public static class MailProperties {
        private boolean enabled = true;
        private ArticleSubmissionProperties articleSubmission = new ArticleSubmissionProperties();

        @Data
        public static class ArticleSubmissionProperties {
            private boolean enabled = true;
            private String to;
            private String cc;
            private String subject = "新投稿通知";
        }
    }
}

