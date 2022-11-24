package com.milan.tipster.dto;

import com.milan.tipster.model.enums.EPick;
import com.milan.tipster.model.enums.EStrategyPart;
import com.milan.tipster.model.enums.ESystemType;
import lombok.Data;

import java.util.EnumSet;
import java.util.Map;

@Data
public class StrategyConfig {

    /**
     * Лейбел стратегии
     */
    private EStrategyPart strategyPart;

    /**
     * Список пиков которые принимаем
     */
    private EnumSet<EPick> acceptableTipTypes;
    /**
     * Минимальный принимаемый коэфф в разрезе пика
     */
    private Map<EPick, Double> oddsMinMap;
    /**
     * Максимальный принимаемый коэфф в разрезе пика
     */
    private Map<EPick, Double> oddsMaxMap;
    /**
     * Минимальное время между двумя типами в минутах
     */
    private Integer timeBound;
    /**
     * Размер тикета либо количество параллельных планов синглов
     */
    private Integer ticketSize;
    /**
     * Сумма для бета
     */
    private Integer betAmount;
    /**
     * Таблица с суммой для бета по систему в разрезе типа система
     */
    private Map<ESystemType, Integer> systemTipAmountMap;
    /**
     * Флаг перевода dnb и x2 в спотовые типы
     */
    private Boolean convertDnbToSpot;
    /**
     * Флаг рандомной вставки в план по синглам
     */
    private Boolean randomSinglePlanInsertFlag;
    /**
     * Максимальное количество пиков без выиграша для увеличения ставок в два раза - 1000 by default
     */
    private Integer betWithoutWinMax = 1000;
    /**
     * Обнулять счетчик betWithoutWinMax каждый день
     */
    private Boolean resetBetWithoutWinDailyFlag;
}
