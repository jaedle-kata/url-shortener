package com.example.kata;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.IntStream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

public class UrlShortenerTest {

    private static final String SHORT_PREFIX = "https://sho.rt/";

    private static final String URL_1 = "https://www.google.de";
    private static final String URL_2 = "https://www.denic.de";
    private static final String URL_3 = "https://www.amazon.de";

    private static final String BASE_URL = "https://www.denic.de";
    private static final int MAXIMUM_CAPACITY = 1000;
    private static final String ALLOWED_ID_DIGITS = "0123456789";
    private static final String UNKNOWN_SHORT_URL = "https://sho.rt/asdf";
    private static final String UNKONWN_LONG_URL = "https://UNKOWN";

    private static String id(String shortUrl) {
        return shortUrl.replaceAll(SHORT_PREFIX, "");
    }

    private static String[] digits(String id) {
        return id.split("");
    }

    private static String[] generateUniqueUrls(int number) {
        return IntStream.range(0, number)
                .mapToObj(i -> BASE_URL + i)
                .toArray(String[]::new);
    }

    @Test
    public void isEmptyOnCreation() {
        assertEquals(0, new UrlShortener().size());
    }

    @Test
    public void shortensUrls() {
        UrlShortener urlShortener = new UrlShortener();

        String result = urlShortener.shorten(URL_1);

        assertEquals(1, urlShortener.size());
        assertTrue(result.startsWith(SHORT_PREFIX));
    }

    @Test
    public void unshortenUrls() {
        UrlShortener urlShortener = new UrlShortener();

        String shortUrl = urlShortener.shorten(URL_1);
        String result = urlShortener.unshorten(shortUrl);

        assertEquals(URL_1, result);
    }

    @Test
    public void unshortenManyUrls() {
        UrlShortener urlShortener = new UrlShortener();

        String shortenUrl1 = urlShortener.shorten(URL_1);
        String shortenUrl2 = urlShortener.shorten(URL_2);

        assertEquals(2, urlShortener.size());
        assertEquals(URL_1, urlShortener.unshorten(shortenUrl1));
        assertEquals(URL_2, urlShortener.unshorten(shortenUrl2));
    }

    @Test
    public void canHandle1000Urls() {
        UrlShortener urlShortener = new UrlShortener();
        String[] longUrls = generateUniqueUrls(MAXIMUM_CAPACITY);

        Map<String, String> longToShortUrl = new HashMap<>();
        for (int i = 0; i < MAXIMUM_CAPACITY; i++) {
            longToShortUrl.put(longUrls[i], urlShortener.shorten(longUrls[i]));
        }

        assertEquals(MAXIMUM_CAPACITY, urlShortener.size());
        assertCanUnshortenUrls(urlShortener, longToShortUrl);
        assertIdHas3Digits(longToShortUrl);
    }

    private void assertCanUnshortenUrls(UrlShortener urlShortener, Map<String, String> mapping) {
        for (String longUrl : mapping.keySet()) {
            String shortUrl = mapping.get(longUrl);
            assertEquals(longUrl, urlShortener.unshorten(shortUrl));
        }
    }

    private void assertIdHas3Digits(Map<String, String> longToShortUrl) {
        for (String shortUrl : longToShortUrl.values()) {
            String id = id(shortUrl);
            assertEquals(3, id.length());
            for (String digit : digits(id)) {
                assertTrue(ALLOWED_ID_DIGITS.contains(digit));
            }
        }
    }

    @Test(expected = RuntimeException.class)
    public void givenUnknownShortUrlFails() {
        UrlShortener urlShortener = new UrlShortener();
        urlShortener.shorten(URL_1);

        urlShortener.unshorten("https://sho.rt/asdf");
    }

    @Test
    public void doesNotCreateDuplicateEntries() {
        UrlShortener urlShortener = new UrlShortener();

        String shortUrl1 = urlShortener.shorten(URL_1);
        String shortUrl2 = urlShortener.shorten(URL_1);

        assertEquals(shortUrl1, shortUrl2);
        assertEquals(1, urlShortener.size());
    }

    @Test
    public void initialCountOfUnshorteningsIsZero() {
        UrlShortener urlShortener = new UrlShortener();

        String shortUrl = urlShortener.shorten(URL_1);

        assertEquals(0, urlShortener.unshorteningsFor(shortUrl));
    }

    @Test
    public void countsUnshortenings() {
        UrlShortener urlShortener = new UrlShortener();
        String neverUnshortened = urlShortener.shorten(URL_1);
        String onceUnshorted = urlShortener.shorten(URL_2);
        String twiceUnshorted = urlShortener.shorten(URL_3);

        urlShortener.unshorten(onceUnshorted);
        urlShortener.unshorten(twiceUnshorted);
        urlShortener.unshorten(twiceUnshorted);

        assertEquals(0, urlShortener.unshorteningsFor(neverUnshortened));
        assertEquals(1, urlShortener.unshorteningsFor(onceUnshorted));
        assertEquals(2, urlShortener.unshorteningsFor(twiceUnshorted));
    }

    @Test(expected = RuntimeException.class)
    public void unknownShortUrlFailsOnCountingUnshortening() {
        UrlShortener urlShortener = new UrlShortener();
        urlShortener.shorten(URL_1);

        urlShortener.unshorteningsFor(UNKNOWN_SHORT_URL);
    }

    @Test
    public void idShouldBeRandom() {
        UrlShortener shortener1 = new UrlShortener();
        UrlShortener shortener2 = new UrlShortener();

        assertNotEquals(shortener1.shorten(URL_1), shortener2.shorten(URL_1));
    }

    @Test
    public void countsUnshorteningsForLongUrl() {
        UrlShortener urlShortener = new UrlShortener();
        String shortUrl = urlShortener.shorten(URL_1);

        urlShortener.unshorten(shortUrl);

        assertEquals(1, urlShortener.unshorteningsFor(URL_1));
    }

    @Test(expected = RuntimeException.class)
    public void countUnshorteningFailsIfLongUrlIsUnknown() {
        UrlShortener urlShortener = new UrlShortener();
        urlShortener.shorten(URL_1);

        urlShortener.unshorteningsFor(UNKONWN_LONG_URL);
    }

    @Test(expected = RuntimeException.class)
    public void creatingShortUrlForNonHttpsFails() {
        new UrlShortener().shorten("http://www.google.de");
    }

    @Test
    public void createsStatistics() {
        UrlShortener urlShortener = new UrlShortener();
        String shortUrl = urlShortener.shorten(URL_1);

        urlShortener.unshorten(shortUrl);
        urlShortener.unshorten(shortUrl);

        String expected = "Short URL: " + shortUrl + ", long URL: " + URL_1 + ", visits: 2";
        assertEquals(expected, urlShortener.statistics(shortUrl));
        assertEquals(expected, urlShortener.statistics(URL_1));
    }

    @Test(expected = RuntimeException.class)
    public void statisticsForUnknownShortUrlFails() {
        new UrlShortener().statistics(UNKNOWN_SHORT_URL);
    }

    @Test(expected = RuntimeException.class)
    public void statisticsForUnknownLongUrlFails() {
        new UrlShortener().statistics(UNKONWN_LONG_URL);
    }

    @Test(expected = RuntimeException.class)
    public void canNotShortenUrlWithSamePrefix() {
        new UrlShortener().shorten(SHORT_PREFIX);
    }
}
