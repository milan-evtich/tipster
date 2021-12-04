package com.milan.tipster.service.impl;

import com.milan.tipster.dao.CompetitionRepository;
import com.milan.tipster.model.Competition;
import com.milan.tipster.model.Game;
import com.milan.tipster.model.Link;
import com.milan.tipster.model.Team;
import com.milan.tipster.service.FetchingService;
import com.milan.tipster.service.TeamService;
import com.milan.tipster.util.Constants;
import com.milan.tipster.util.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.milan.tipster.util.Constants.SLEEP_MINIMUM_3_SECONDS;

@Service
@Slf4j
public class FetchingServiceImpl implements FetchingService {

    @Autowired
    private TeamService teamService;

    @Autowired
    private CompetitionRepository competitionRepository;

    @Override
    public List<Team> fetchTeams(String path) {
        Document doc = fetchDoc(path);

        if (doc != null) {
            List<Team> teams = new ArrayList<>();
            Element table_results = doc.select("table.table-results").first();
            Elements teamElems = table_results.select("td.team-name");
            for (Element teamElem : teamElems) {
                teams.add(new Team(teamElem.text()));
            }
            return teams;
        }
        return null;
    }

    @Override
    public List<Link> fetchLinksFilterByHref(String path, String filterHrefTerm) {
        Document doc = fetchDoc(path);

        if (doc != null) {
            List<Link> linkList = new ArrayList<>();
            Elements links = doc.select("a[href]");
            log.info("\nTotal links count: {}", links.size());
            for (Element linkElem : links) {
                if (StringUtils.isNotBlank(linkElem.attr("abs:href"))
                        && StringUtils.isNotBlank(filterHrefTerm)) {
                    if (linkElem.attr("abs:href").toUpperCase().contains(filterHrefTerm.toUpperCase())) {
                        Link link = Link.builder()
                                .href(linkElem.attr("abs:href"))
                                .text(linkElem.text())
                                .build();
                        linkList.add(link);
                    }
                }
            }
            return linkList;
        } else {
            log.error("Couldn't get links for path {} filtered by term {}", path, filterHrefTerm);
        }
        return null;
    }

    @Override
    public void logLinks(String path) {
        Document doc = fetchDoc(path);

        if (doc != null) {
            Elements links = doc.select("a[href]");
            print("\nLinks: (%d)", links.size());
            for (Element link : links) {
                print(" * a: <%s>  (%s)", link.attr("abs:href"), trim(link.text(), 35));
            }
        }
    }

    @Override
    public Document fetchDocByUrlOrPath(String urlOrPath, boolean isFile) throws IOException {
        log.info("Fetching {} {}...", (isFile ? "file" : "url"), urlOrPath);
        if (isFile) {
            File input = new File(urlOrPath);
            return Jsoup.parse(input, "UTF-8", "");
        } else {
            log.info("Sleeping 1-5 seconds");
            Utils.sleep(SLEEP_MINIMUM_3_SECONDS);
            return Jsoup.connect(urlOrPath).get();
        }
    }

    @Override
    public Document fetchDoc(String path) {
        Document doc;

        try {
            doc = Jsoup.connect(Constants.URL_MATCH_MONEY + path)
                    .userAgent(Constants.USER_AGENT)
                    .timeout(Constants.TIMEOUT)
                    .validateTLSCertificates(false)
                    .get();
            return doc;
        } catch (IOException e) {
            log.error("Cant fetch data from " + Constants.URL_MATCH_MONEY + path, e);
        }

        return null;
    }

    private static void print(String msg, Object... args) {
        System.out.println(String.format(msg, args));
    }

    private static String trim(String s, int width) {
        if (s.length() > width)
            return s.substring(0, width - 1) + ".";
        else
            return s;
    }

}
