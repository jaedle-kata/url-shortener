package com.example.kata;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class UrlShortener {

    private static final String SHORT_URL_PREFIX = "https://sho.rt/";

    private Map<String, String> urls;
    private Map<String, Integer> unshortenings;

    public UrlShortener() {
        this.urls = new HashMap<>();
        this.unshortenings = new HashMap<>();
    }

    public int size() {
        return urls.size();
    }

    public String shorten(String longUrl) {
        assertIsHttpsLink(longUrl);
        assertIsDifferentDomain(longUrl);

        if (alreadyContains(longUrl)) {
            return getShortUrl(longUrl);
        } else {
            return add(longUrl);
        }
    }

    private static void assertIsDifferentDomain(String longUrl) {
        if (longUrl.startsWith(SHORT_URL_PREFIX)) {
            throw new RuntimeException();
        }
    }

    private static void assertIsHttpsLink(String longUrl) {
        if (!longUrl.startsWith("https://")) {
            throw new RuntimeException();
        }
    }

    private String add(String longUrl) {
        String shortUrl = generateId();
        this.urls.put(shortUrl, longUrl);
        return shortUrl;
    }

    private boolean alreadyContains(String longUrl) {
        return this.urls.containsValue(longUrl);
    }

    private String getShortUrl(String longUrl) {
        return this.urls.entrySet().stream().filter(e -> e.getValue().equals(longUrl)).findFirst().get().getKey();
    }

    private String generateId() {
        do {
            String idCandidate = idCandidate();
            if (!this.urls.containsKey(idCandidate)) {
                return idCandidate;
            }
        } while (true);
    }

    private String idCandidate() {
        return String.format(SHORT_URL_PREFIX + "%03d", new Random().nextInt(1000));
    }

    public String unshorten(String shortUrl) {
        assertShortUrlExists(shortUrl);

        countUnshortening(shortUrl);
        return this.urls.get(shortUrl);
    }

    private void countUnshortening(String shortUrl) {
        this.unshortenings.put(shortUrl, this.unshortenings.getOrDefault(shortUrl, 0) + 1);
    }

    private void assertShortUrlExists(String shortUrl) {
        if (!this.urls.containsKey(shortUrl)) {
            throw new RuntimeException();
        }
    }

    public int unshorteningsFor(String shortOrLongUrl) {
        String shortUrl = shortUrlOf(shortOrLongUrl);
        assertShortUrlExists(shortUrl);
        return this.unshortenings.getOrDefault(shortUrl, 0);
    }

    private String shortUrlOf(String shortOrLongUrl) {
        String result = shortOrLongUrl;
        if (isLongUrl(shortOrLongUrl)) {
            result = getShortUrl(shortOrLongUrl);
        }
        return result;
    }

    private boolean isLongUrl(String shortOrLongUrl) {
        return !shortOrLongUrl.startsWith(SHORT_URL_PREFIX);
    }

    public String statistics(String shortOrLongUrl) {
        String shortUrl = shortUrlOf(shortOrLongUrl);
        return String.format("Short URL: %s, long URL: %s, visits: %d",
                shortUrl,
                this.urls.get(shortUrl),
                this.unshorteningsFor(shortUrl));
    }
}
