package com.campus.trade.service.impl;

import com.campus.trade.common.BusinessException;
import com.campus.trade.dto.*;
import com.campus.trade.entity.Product;
import com.campus.trade.mapper.ProductMapper;
import com.campus.trade.service.AiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class AiServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AiServiceImpl.class);

    @Value("${deepseek.api-key}")
    private String apiKey;

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.model}")
    private String model;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ProductMapper productMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // ---- 智能定价 ----

    @Override
    public PriceResponseDTO suggestPrice(PriceRequestDTO request) {
        String prompt = String.format("""
                你是一个专业的二手商品估价助手。请根据以下信息给出合理建议价格：

                商品：%s
                成色：%s
                分类：%s

                请用以下JSON格式回答（只返回JSON，不要其他文字）：
                {"suggestedPrice": 数字, "priceRange": "价格区间，如2800-3500", "reasoning": "定价理由，50字以内"}
                """, request.getTitle(), request.getCondition(), request.getCategory());

        String response = callDeepSeek(prompt);
        return parseJson(response, PriceResponseDTO.class);
    }

    // ---- 语义搜索 ----

    @Override
    public SemanticSearchDTO semanticSearch(String keyword) {
        // 用 AI 扩展搜索关键词
        String prompt = String.format("""
                你是一个搜索关键词扩展助手。用户想搜索二手商品，请把他的口语化查询扩展成2-4个相关关键词，
                用逗号分隔，只返回关键词不要其他文字。

                用户搜索："%s"
                扩展关键词：""", keyword);

        String expanded = callDeepSeek(prompt);
        String[] keywords = expanded.split("[,，]");
        log.info("语义搜索: 原始='{}' → 扩展={}", keyword, Arrays.toString(keywords));

        // 用扩展关键词 LIKE 查询
        Set<Product> resultSet = new LinkedHashSet<>();
        for (String kw : keywords) {
            String trimmed = kw.trim();
            if (!trimmed.isEmpty()) {
                List<Product> products = productMapper.selectList(
                        new com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper<Product>()
                                .like(Product::getTitle, trimmed)
                                .or()
                                .like(Product::getDescription, trimmed)
                );
                resultSet.addAll(products);
            }
        }

        SemanticSearchDTO dto = new SemanticSearchDTO();
        dto.setProducts(new ArrayList<>(resultSet));
        dto.setExpandedKeywords(expanded);
        return dto;
    }

    // ---- 商品合规检查 ----

    @Override
    public ReviewResponseDTO reviewDescription(ReviewRequestDTO request) {
        String prompt = String.format("""
                你是一个二手交易平台的内容审核员。请检查以下商品描述是否违规：
                违规类型包括但不限于：违禁品、仿品/假货、虚假描述、联系方式外露、含有外部链接。

                商品描述：%s

                请用以下JSON格式回答（只返回JSON）：
                {"pass": true或false, "reason": "审核意见，50字以内", "riskLevel": "LOW/MEDIUM/HIGH"}
                """, request.getDescription());

        String response = callDeepSeek(prompt);
        return parseJson(response, ReviewResponseDTO.class);
    }

    // ---- 通用 API 调用 ----

    private String callDeepSeek(String userPrompt) {
        try {
            Map<String, Object> body = new HashMap<>();
            body.put("model", model);
            body.put("messages", List.of(Map.of("role", "user", "content", userPrompt)));
            body.put("temperature", 0.7);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(
                    baseUrl + "/chat/completions", entity, Map.class);

            if (response.getBody() == null) {
                throw new BusinessException(502, "DeepSeek 返回空响应");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            return (String) message.get("content");

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 DeepSeek 失败", e);
            throw new BusinessException(502, "AI 服务暂时不可用，请稍后重试");
        }
    }

    private <T> T parseJson(String content, Class<T> clazz) {
        try {
            // API 返回可能包裹在 ```json ... ``` 中
            String json = content.trim();
            if (json.startsWith("```")) {
                json = json.replaceAll("```json\\s*", "").replaceAll("```\\s*", "");
            }
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            log.error("解析 DeepSeek 返回 JSON 失败: content={}", content, e);
            throw new BusinessException(502, "AI 返回格式异常，请重试");
        }
    }
}
