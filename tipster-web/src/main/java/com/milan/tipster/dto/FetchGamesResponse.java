package com.milan.tipster.dto;

import com.milan.tipster.model.Game;
import lombok.Builder;
import lombok.Data;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
public class FetchGamesResponse {
    @Builder.Default
    List<String> errorMessages = new ArrayList<>();

    @Builder.Default
    List<Game> games = new ArrayList<>();

    public void addGames(List<Game> games) {
        if (!CollectionUtils.isEmpty(games)) {
            this.games.addAll(games);
        }
    }

    public void addErrorMessages(List<String> errorMessages) {
        if (!CollectionUtils.isEmpty(errorMessages)) {
            this.errorMessages.addAll(errorMessages);
        }
    }
}
