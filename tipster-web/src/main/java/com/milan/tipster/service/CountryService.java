package com.milan.tipster.service;

import com.milan.tipster.model.Country;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.lang.Nullable;

public interface CountryService {

    /**
     * Парсит код страны из countryEl, ищет в БД по коду, если не находит создает такую страну
     * @param tdsElms
     * @param gameCode
     * @return страну
     */
    Country findOrMakeCountry(Elements tdsElms, @Nullable String gameCode);

    /**
     * Парсить блок season-label и находит страну
     * @param seasonLabelEl
     * @return
     */
    Country findCountryFromSeasonLabel(Element seasonLabelEl);
}
