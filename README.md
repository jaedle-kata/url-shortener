# "Create your own url-shortener"-Kata

## Summary
Within this kata you should create a class `UrlShortener` which accepts an url to shorten `shorten(String url)` and returns an shortened link.

Example from bit.ly:

|long url               |shortened url           |
|-----------------------|------------------------|
| https://www.google.de | https://bit.ly/1gjR8PE |
| https://www.denic.de  | https://bit.ly/2jNPT1Z |  


## Hints

- Each main-point or sub-point below should create at least one new test or an addition to an existing test
- Follow them line by line
- Please do not forget to refactor!
- Do not implement any other public methods than listed above and create them as you need them! 
  - `int size()`
  - `String shorten(String)`
  - `String unshorten(String)`
  - `int countUnshortenings(String)`
  - `String statisctics(String)`

## Rules

- A newly created UrlShortener is empty: 
  - The size is empty `int size()` returns 0
- There is a method which can be called to shorten an url `String shorten(String longUrl)`:
  - The size increases to 1
  - The shortened urls should start with https://sho.rt
  - The shortened url should follow the format https://sho.rt/ID, the format of the id is free to choose. 
  Hint: think about using an ascending number.
- The full url behind a shortened url can be retrieved through `String unshorten(String shortUrl)`
  - given the shortUrl it will return the long url 
- The url shortener should handle multiple urls
- The url shortener should handle 1000 urls
- An url that has the same domain https://sho.rt can not be shortened (again)
- if given an unknown shortUrl to `unshorten(String shortUrl)` it will throw an RuntimeException
- If the same url should be shortened more than once it will return the same short url. It should fail with RuntimeException.
- The id should be of consist of 3 digits with [0-9]. Example: 001, 345, ...
- The count of _unshortenings_ `int unshorteningsFor(String shortUrl)` of an url should be counted:
  - When a short url is created the count is 0
  - When a short url is unshortend the count increases by one
  - When the short url given does not exist throw an RuntimeException
- The id should be random and independend of the given url. (number between 0 and 999)
Hint: two instances of the UrlShortener should return different ids for same url.  
- The method `int unshorteningsFor(String shortUrl)` should be changed to 
`int unshorteningsFor(String shortOrLongUrl)` so that it does not matter if the short url or long url were given. 
Hint: urls starting with https://sho.rt/ can not be shortened:
  - unknown long urls should throw a RuntimeException
- The method `String shorten(String url)` should only accept urls starting with `https://`. If other urls are given, it should throw a RuntimeException 
- Create a statistics method String `String statisctics(String shortOrLongUrl)` which returns a string like: 
`Short URL: https://sho.rt/1234, long URL: https://www.google.de, visits: 12`:
  - short/long url can be given
  - given an unknown url it throws an RuntimeException
