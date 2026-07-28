package com.campus.trade.service;

import com.campus.trade.dto.PriceRequestDTO;
import com.campus.trade.dto.PriceResponseDTO;
import com.campus.trade.dto.ReviewRequestDTO;
import com.campus.trade.dto.ReviewResponseDTO;
import com.campus.trade.dto.SemanticSearchDTO;

public interface AiService {

    PriceResponseDTO suggestPrice(PriceRequestDTO request);

    SemanticSearchDTO semanticSearch(String keyword);

    ReviewResponseDTO reviewDescription(ReviewRequestDTO request);
}
