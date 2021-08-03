package com.visitor.urlshortener.service;

import com.visitor.urlshortener.dto.ShortenerDto;
import com.visitor.urlshortener.dto.UrlResponseDto;
import com.visitor.urlshortener.entity.Url;
import com.visitor.urlshortener.repository.UrlRepository;
import com.visitor.urlshortener.util.Base62;
import com.visitor.urlshortener.util.ShortenerUtil;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class ShortenerService {
    public final UrlRepository urlRepository;
    public final ShortenerUtil shortenerUtil;
    public final Base62 base62;

    private final String errorRedirectUrl = "https://visitor.dev.42seoul.io/error";

    public ShortenerService(UrlRepository urlRepository,
        ShortenerUtil shortenerUtil, Base62 base62) {
        this.urlRepository = urlRepository;
        this.shortenerUtil = shortenerUtil;
        this.base62 = base62;
    }

    public String findOriginalUrl(String encodedValue) {
        String originalHash = base62.decoding(encodedValue);
        List<Url> urlList = urlRepository.searchByHashValueStartsWith(originalHash);
        if (urlList == null) {
            return errorRedirectUrl;
        }
        return urlList.stream()
            .map(url -> url.getOriginalUrl().toString())
            .filter(url -> 1 == 1)
            .findFirst().orElse(errorRedirectUrl);
    }

    public String createShortUrl(String originalUrl) throws NoSuchAlgorithmException {
        String hashValue = shortenerUtil.encrypt(originalUrl);
        Url url = new Url(hashValue, originalUrl);
        urlRepository.save(url);
        String value = hashValue.substring(0, 8);
        log.info("Truncated Hash Value is {}", value);
        BigInteger bigInteger = new BigInteger(value, 16);
        String encoding = base62.encoding(bigInteger);
        log.info("Encoded with base62 value is {}", encoding);
        return encoding;
    }

    public List<UrlResponseDto> createShortUrl(List<ShortenerDto> urlList) {
        return urlList
            .stream()
            .map(url -> {
                String value = null;
                try {
                    value = createShortUrl(url.getOriginalUrl());
                } catch (NoSuchAlgorithmException e) {
                  log.error(e.getMessage());
                }
                return new UrlResponseDto(url.getId(), value);
            })
            .collect(Collectors.toList());
    }
}
