package com.example.ApartmentsSearchWithTelegramReporting;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
@Log4j2
public class UrlMonitorService {

    @Autowired
    private FlatRepositoryImpl flatRepositoryImpl;

    @Autowired
    private MyTelegramBot myTelegramBot;

    private final HtmlFetcher htmlFetcher = new HtmlFetcher();

    private static final String DOMAIN = "https://inberlinwohnen.de";

    private static final long FIXED_RATE_MS = 210000;

    private long lastExecutionTime = 0;

    @Scheduled(fixedRate = FIXED_RATE_MS)
    public void monitorUrl() {

        long currentTime = System.currentTimeMillis();

        if (currentTime - lastExecutionTime < FIXED_RATE_MS) {
            return;
        }
        lastExecutionTime = currentTime;

        String url = DOMAIN + "/wohnungsfinder/";
        String idPrefix = "flat_";

        Elements flats = htmlFetcher.fetchAndParseElements(url, "li.tb-merkflat");

        int degewoApartmentAmount = Integer.parseInt(htmlFetcher.fetchAndParseElements("https://immosuche.degewo.de/de/search",
                "button.btn.btn--prim.btn--lg.btn--immosearch > span").get(0).text());
        int pages = degewoApartmentAmount % 10 == 0 ? degewoApartmentAmount / 10 : degewoApartmentAmount / 10 + 1;

        Elements degewoFlats = htmlFetcher.fetchAndParseElements("https://immosuche.degewo.de/de/search?size=10&page=1", "div.search__results.article-list > article");

        for (int i = 2; i <= pages; i++) {
            degewoFlats.addAll(htmlFetcher.fetchAndParseElements("https://immosuche.degewo.de/de/search?size=10&page=" + i, "div.search__results.article-list > article"));
        }

        if (flats != null || degewoFlats != null) {
            boolean anythingNewFound = false;
            Map<Long, String> newFlatsWithDetails = new HashMap<>();

            for (Element flat : flats) {
                Long currentId = Long.valueOf(flat.id().replace(idPrefix, ""));
                String priceAndAddress = "Price and Address: " + flat.select("h3 > span > span._tb_left").text();
                String detailsUrl = "Details URL: " + DOMAIN + flat.select("a.org-but").attr("href");
                newFlatsWithDetails.put(currentId, priceAndAddress + "\n" + detailsUrl);

                if (!flatRepositoryImpl.getAllFlats().containsKey(currentId)) {
                    anythingNewFound = true;
                    myTelegramBot.sendMessageToAll(priceAndAddress + "\n" + detailsUrl);
                    log.info("id: {}; {};\n{}", currentId, priceAndAddress, detailsUrl);
                }
            }

            for (Element flat : degewoFlats) {
                Long currentId = Long.valueOf(flat.select("div.merken").attr("data-objectid").replace("W", "").replace("-", ""));
                String priceAndAddress = "Price and Address: " + flat.select("li.article__properties-item > span").get(0).text();
                priceAndAddress += ", " + flat.select("li.article__properties-item > span").get(1).text();
                priceAndAddress += ", " + flat.select("span.price").text();
                priceAndAddress += " | " + flat.select("span.article__meta").text().replace(" | ", ", ");
                String detailsUrl = "Details URL: " + "https://immosuche.degewo.de/" + flat.select("a").attr("href");
                newFlatsWithDetails.put(currentId, priceAndAddress + "\n" + detailsUrl);

                if (!flatRepositoryImpl.getAllFlats().containsKey(currentId)) {
                    anythingNewFound = true;
                    myTelegramBot.sendMessageToAll(priceAndAddress + "\n" + detailsUrl);
                    log.info("id: {}; {};\n{}", currentId, priceAndAddress, detailsUrl);
                }
            }

            if (anythingNewFound) {
                flatRepositoryImpl.replaceFlats(newFlatsWithDetails);
            } else {
                log.info("Nothing changed");
            }
        }
    }
}