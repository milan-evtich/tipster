package com.milan.tipster.model;

import com.milan.tipster.model.enums.EPick;
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
    // Коэффициент выигранные ставки - проигранные
    private Double overallCoefficient = 0D;

    // Новый коэффициент только типы от 1.8 до 3.0 кроме SPOT_X
    private Double newCoefficient = 0D;

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

    public Rating addCoefficientWin(double coefficient) {
        this.overallCoefficient += (coefficient - 1);
        return this;
    }

    public Rating addCoefficientLost(double coefficient) {
        double odds = 1/(coefficient) - 1;
        this.overallCoefficient -= odds;
        return this;
    }

    public Rating addNewCoefficientWin(double odds, EPick pick) {
        switch (pick) {
            case SPOT_1:
            case SPOT_2:
                if (odds >= 1.8 && odds <= 3D) {
                    this.newCoefficient += (odds - 1);
                }
            case SPOT_DNB_1:
            case SPOT_DNB_2:
                if (odds >= 1.6 && odds <= 2.6) {
                    this.newCoefficient += (odds - 1);
                }
            case SPOT_1X:
            case SPOT_2X:
                if (odds >= 1.4 && odds <= 2.3) {
                    this.newCoefficient += (odds - 1);
                }
        }
        return this;
    }

    public Rating addNewCoefficientLost(double odds, EPick pick) {
        switch (pick) {
            case SPOT_1:
            case SPOT_2:
                if (odds >= 1.8 && odds <= 3D) {
                    this.newCoefficient -= 1/(odds) - 1;
                }
            case SPOT_DNB_1:
            case SPOT_DNB_2:
                if (odds >= 1.6 && odds <= 2.6) {
                    this.newCoefficient -= 1/(odds) - 1;
                }
            case SPOT_1X:
            case SPOT_2X:
                if (odds >= 1.4 && odds <= 2.3) {
                    this.newCoefficient -= 1/(odds) - 1;
                }
        }
        return this;
    }

}
