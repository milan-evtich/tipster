package com.milan.tipster.service;

import com.milan.tipster.model.Score;
import org.jsoup.nodes.Element;

public interface ScoreService {

    Score makeScore(Element scoreEl);
}
