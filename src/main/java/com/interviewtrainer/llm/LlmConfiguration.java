package com.interviewtrainer.llm;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Wires the {@link LlmClient}. With {@code llm.provider=anthropic} (the default) the real Claude
 * client is used; tests set {@code llm.provider=fake} and provide their own implementation.
 */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(LlmProperties.class)
public class LlmConfiguration {

    @Bean
    @ConditionalOnProperty(name = "llm.provider", havingValue = "anthropic", matchIfMissing = true)
    AnthropicLlmClient anthropicLlmClient(LlmProperties properties) {
        return new AnthropicLlmClient(properties);
    }
}
