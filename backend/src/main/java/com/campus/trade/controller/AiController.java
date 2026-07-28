package com.campus.trade.controller;

import com.campus.trade.common.Result;
import com.campus.trade.dto.*;
import com.campus.trade.service.AiService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Autowired
    private AiService aiService;

    @PostMapping("/price-suggest")
    public Result<PriceResponseDTO> suggestPrice(@Valid @RequestBody PriceRequestDTO request) {
        return Result.success(aiService.suggestPrice(request));
    }

    @PostMapping("/review-desc")
    public Result<ReviewResponseDTO> reviewDescription(@Valid @RequestBody ReviewRequestDTO request) {
        return Result.success(aiService.reviewDescription(request));
    }

    @GetMapping("/search/semantic")
    public Result<SemanticSearchDTO> semanticSearch(@RequestParam String q) {
        return Result.success(aiService.semanticSearch(q));
    }
}
