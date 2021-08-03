package com.visitor.urlshortener.util;

import java.math.BigInteger;
import org.springframework.stereotype.Component;

@Component
public class Base62 {
    private final int BASE = 62;
    public  final String BASE_FORMAT = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

    public String encoding(BigInteger uniqueKey) {

        if (uniqueKey.compareTo(BigInteger.ZERO) == 0) {
            return String.valueOf(BASE_FORMAT.charAt(0));
        }

        StringBuffer sb = new StringBuffer();

        while (uniqueKey.compareTo(BigInteger.ZERO) > 0) {
            sb.append(BASE_FORMAT.charAt((uniqueKey.mod(BigInteger.valueOf(BASE)).intValue())));
            uniqueKey = uniqueKey.divide(BigInteger.valueOf(BASE));
        }
        return sb.reverse().toString();
    }

    public String decoding(String encodedStr) {
        BigInteger retValue = BigInteger.ZERO;
        int indexOfEncodedStr = 0;
        while (indexOfEncodedStr < encodedStr.length()) {
            retValue = (retValue.multiply(BigInteger.valueOf(BASE))
                .add(BigInteger.valueOf(BASE_FORMAT.indexOf(encodedStr.charAt(indexOfEncodedStr)))));
            ++indexOfEncodedStr;
        }

        return retValue.toString(16);
    }

}
