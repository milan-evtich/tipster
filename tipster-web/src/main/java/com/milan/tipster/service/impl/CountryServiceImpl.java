package com.milan.tipster.service.impl;

import com.milan.tipster.dao.CountryRepository;
import com.milan.tipster.model.Country;
import com.milan.tipster.service.CountryService;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.milan.tipster.util.Utils.greekToUpper;

@Slf4j
@Service
public class CountryServiceImpl implements CountryService {

    @Autowired
    private CountryRepository countryRepository;

    @Override
    public Country findOrMakeCountry(Elements tdsElms, String gameCode) {
        Element countryEl = tdsElms.first();
//        String countryCode = parseCountryFlag(countryEl);
        String countryFlag = parseCountryFlag(countryEl);
//        Country country = countryRepository.findByCode(countryCode);
        Country country = countryRepository.findByFlag(countryFlag);
        if (country == null) {
            log.warn("----WARN----COUNTRY NOT FOUND----{}---countryEl:{}---gameCode:{}", countryFlag, tdsElms.toString(), gameCode);
            // make Country name
            String countryName = parseCountryName(tdsElms);
            country = Country.builder()
                    .flag(countryFlag)
                    .name(countryName)
                    .build();
            country = countryRepository.save(country);
        }
        return country;
    }

    @Override
    public Country findCountryFromSeasonLabel(Element seasonLabelEl) {
        String countryFlag = parseCountryFlag(seasonLabelEl);
        return countryRepository.findByFlag(countryFlag);
    }

    private String parseCountryName(Elements tdsElms) {
        Element competitionEl = tdsElms.get(2);
        return greekToUpper(competitionEl.text().trim()).split(" - ")[0];
    }

    private String parseCountryFlag(Element countryEl) {
        log.info("-----COUNTRY------countryEl:{}", countryEl.toString());
        Element countrySpanEl = countryEl.selectFirst("span");
        String[] countryParts = countrySpanEl.className().split(" ");
        return countryParts[countryParts.length - 1];
    }
}
