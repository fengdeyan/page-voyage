package me.yan.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * DashScope 百炼平台配置
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "dashscope")
public class DashScopeConfig {
    
    /**
     * API Key
     */
    private String apiKey;
    
    /**
     * 模型名称，默认qwen-max
     */
    private String model = "qwen-max";
    
    /**
     * 最大token数
     */
    private Integer maxTokens = 1500;
    
    /**
     * 温度参数，控制回答的创造性
     */
    private Double temperature = 0.7;
}