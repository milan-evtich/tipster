package com.milan.tipster.service;


import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Tip;
import com.milan.tipster.model.Tipman;
import org.jsoup.nodes.Document;

import java.util.List;

public interface TipmanService {

    Tipman fetchAndSaveTipman(Document authorDoc);


    /**
     * Update ONLY tipman rating
     * @param tipmanFullName
     * @param tips
     */
    // TODO
    void updateTipmanRating(String tipmanFullName, Tip... tips);

    List<Tipman> findAll();
}
