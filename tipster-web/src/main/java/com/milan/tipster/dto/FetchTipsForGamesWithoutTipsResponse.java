package com.milan.tipster.dto;

import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class FetchTipsForGamesWithoutTipsResponse {
    @Builder.Default
    List<String> errorMessages = new ArrayList<>();

    @Builder.Default
    int tipCount = 0;

    public void addErrorMessage(String errorMessage) {
        if (!StringUtils.isEmpty(errorMessage)) {
            this.errorMessages.add(errorMessage);
        }
    }
}
