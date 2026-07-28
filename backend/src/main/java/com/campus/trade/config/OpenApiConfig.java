package com.campus.trade.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("校园二手交易平台 API")
                        .version("2.0.0")
                        .description("校园二手交易平台 — 学生间闲置物品买卖，集成 DeepSeek 智能定价与语义搜索"));
    }
}
