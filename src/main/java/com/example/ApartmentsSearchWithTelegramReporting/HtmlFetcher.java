package com.example.ApartmentsSearchWithTelegramReporting;

import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Log4j2
public class HtmlFetcher {

    private final RestTemplate restTemplate = new RestTemplate();

    public Elements fetchAndParseElements(String url, String cssQuery) {

        String html = "";
        try {
            html = restTemplate.getForObject(url, String.class);

        } catch (HttpClientErrorException | HttpServerErrorException e) {

            log.error("HTTP Status Code: {}", e.getStatusCode());
        } catch (RestClientException e) {

            log.error("Error during REST call: {}", e.getMessage());
        }

        if (html != null && !html.isEmpty()) {

            Document document = Jsoup.parse(html);
            return document.select(cssQuery);
        } else {

            log.error("Content is empty");
            return null;
        }
    }

}
