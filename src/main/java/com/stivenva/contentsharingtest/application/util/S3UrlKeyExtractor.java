package com.stivenva.contentsharingtest.application.util;

public final class S3UrlKeyExtractor {

    public static String extractKeyFromUrl(String url) {
        if (url == null || url.isBlank()) {
            return null;
        }
        try {
            int firstSlash = url.indexOf('/', 8);
            if (firstSlash == -1) {
                return null;
            }
            return url.substring(firstSlash + 1);
        } catch (Exception e) {
            return null;
        }
    }
}
