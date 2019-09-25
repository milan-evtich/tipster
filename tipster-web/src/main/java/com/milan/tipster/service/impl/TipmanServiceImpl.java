package com.milan.tipster.service.impl;

import com.milan.tipster.dao.TipmanRepository;
import com.milan.tipster.error.exception.MultipleTipmenWithSameName;
import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.Tipman;
import com.milan.tipster.service.RatingService;
import com.milan.tipster.service.TipmanService;
import com.milan.tipster.util.Constants;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component
public class TipmanServiceImpl implements TipmanService {

    @Autowired
    private TipmanRepository tipmanRepository;

    @Autowired
    private RatingService ratingService;

    /**
     * Fetch author name from document,
     * search for tipman in DB, returns it if it exists,
     * otherwise adds a new one to the DB and returns it
     *
     * @param mathcDoc Страница матча
     * @return tipman or null
     */
    @Override
    public Tipman fetchAndSaveTipman(Document mathcDoc) {

        // Tip Author (tipman)
        String authorName = null;
        Element authorDiv = mathcDoc.selectFirst("div.row-author");
        if (authorDiv != null && authorDiv.hasText()) {
            Element authorFirstSpan = authorDiv.selectFirst("span");
            if (authorFirstSpan != null && authorFirstSpan.hasText()) {
                String authorText = authorFirstSpan.text();
                if (authorText.contains(Constants.MATCH_MONEY_GRAFEI)) {
                    authorName = authorText.substring(authorText.indexOf(Constants.MATCH_MONEY_GRAFEI) + Constants.MATCH_MONEY_GRAFEI.length());
                    if (authorName != null) {
                        List<Tipman> authors = tipmanRepository.findByFullName(authorName);
                        if (authors == null || authors.isEmpty()) {
                            return tipmanRepository.save(Tipman.builder().fullName(authorName).build());
                        } else if (authors.size() == 1) {
                            return authors.get(0);
                        } else {
                            // Ручная разборка
                            throw new MultipleTipmenWithSameName("Author name: " + authorName);
                        }
                    }
                }
            }
        }
        return null;
    }

    @Override
    public void updateTipmanRating(String tipmanFullName, Tip... tips) {
        // TODO
    }

    @Override
    public List<Tipman> findAll() {
        return (List<Tipman>) tipmanRepository.findAll();
    }

}
