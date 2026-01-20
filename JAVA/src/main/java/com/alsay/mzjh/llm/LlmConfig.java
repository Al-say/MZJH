package com.alsay.mzjh.llm;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * LLM 配置类，根据模式创建对应的 ChatClient Bean
 */
@Configuration
public class LlmConfig {

    private static final Logger log = LoggerFactory.getLogger(LlmConfig.class);

    @Value("${spring.ai.openai.api-key:}")
    private String apiKey;

    @Value("${spring.ai.openai.base-url:https://api.deepseek.com}")
    private String baseUrl;

    @Value("${spring.ai.openai.chat.options.model:deepseek-chat}")
    private String model;

    @Bean
    @ConditionalOnProperty(name = "roundtable.llm.mode", havingValue = "deepseek")
    public OpenAiApi openAiApi() {
        log.info("初始化 OpenAiApi: baseUrl={}, model={}", baseUrl, model);
        return new OpenAiApi(baseUrl, apiKey);
    }

    @Bean
    @ConditionalOnProperty(name = "roundtable.llm.mode", havingValue = "deepseek")
    public OpenAiChatClient openAiChatClient(OpenAiApi openAiApi) {
        OpenAiChatOptions options = OpenAiChatOptions.builder()
                .withModel(model)
                .withTemperature(0.7f)
                .withMaxTokens(2000)
                .build();
        
        log.info("初始化 OpenAiChatClient: model={}, temperature=0.7", model);
        return new OpenAiChatClient(openAiApi, options);
    }
}
