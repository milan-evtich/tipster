package com.milan.tipster.model;

import lombok.Data;

import javax.persistence.Embeddable;
import java.io.Serializable;

@Data
@Embeddable
public class Rating implements Serializable {

    // Общее количество матчей которые сиграный в этом сезоне
    private Long overallMatchCount = 0L;
    // Общее количество прогнозов для этого сезона
    private Long overallTipCount = 0L;
    // Выиграные прогнозы
    private Long tipsWon = 0L;
    // Проигравшые прогнозы
    private Long tipsLost = 0L;
    // Прогнозы с возвратом (draw no bet)
    private Long tipsDNB = 0L;
    // NOBET
    private Long nobetCount = 0L;
    // UNKNOWN
    private Long unknownCount = 0L;
    // Тотальный скор
    private Double overallScore = 0D;
    // Тотальный рейтинг
    private Long overallRating = 0L;

    public Rating addTipDNB() {
        this.tipsDNB++;
        return this;
    }

    public Rating addTipLost() {
        this.tipsLost++;
        return this;
    }

    public Rating addMatch() {
        this.overallMatchCount++;
        return this;
    }

    public Rating addTip() {
        this.overallTipCount++;
        return this;
    }

    public Rating addTipWon() {
        this.tipsWon++;
        return this;
    }

    public Rating addNobet() {
        this.nobetCount++;
        return this;
    }

    public Rating addUnknown() {
        this.unknownCount++;
        return this;
    }
}
