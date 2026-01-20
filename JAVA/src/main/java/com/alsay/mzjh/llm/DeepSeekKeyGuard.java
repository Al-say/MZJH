package com.alsay.mzjh.llm;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 启动时校验 DeepSeek API Key 配置。
 * 避免启用 deepseek 模式却忘记设置环境变量 MZJH_DEEPSEEK。
 */
@Component
public class DeepSeekKeyGuard {

    private static final Logger log = LoggerFactory.getLogger(DeepSeekKeyGuard.class);

    @Value("${roundtable.llm.mode:mock}")
    private String mode;

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @PostConstruct
    public void validate() {
        if ("deepseek".equalsIgnoreCase(mode)) {
            if (apiKey == null || apiKey.isBlank()) {
                throw new IllegalStateException(
                    "LLM 模式为 deepseek，但环境变量 MZJH_DEEPSEEK 未设置。" +
                    "请设置环境变量后重启：export MZJH_DEEPSEEK=\"your-api-key\""
                );
            }
            // 安全日志：仅显示密钥长度，不输出实际内容
            log.info("DeepSeek API Key 已配置 (长度: {})", apiKey.length());
        } else {
            log.info("LLM 模式: {} (DeepSeek 未启用)", mode);
        }
    }
}
