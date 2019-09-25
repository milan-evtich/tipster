package com.milan.tipster.service;

import com.milan.tipster.model.Game;
import com.milan.tipster.model.Link;
import com.milan.tipster.model.Team;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.List;

public interface FetchingService {

    List<Team> fetchTeams(String path);

    List<Link> fetchLinksFilterByHref(String path, String filterHrefTerm);

    void logLinks(String path);

    Document fetchDocByUrlOrPath(String urlOrPath, boolean isFile) throws IOException;

    Document fetchDoc(String path);

}
