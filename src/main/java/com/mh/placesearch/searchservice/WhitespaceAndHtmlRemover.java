package com.mh.placesearch.searchservice;

public class WhitespaceAndHtmlRemover {
    public static String apply(String o) {
        return o.replaceAll("<(/)?([a-zA-Z]*)(\\s[a-zA-Z]*=[^>]*)?(\\s)*(/)?>|\\s", "");
    }
}
