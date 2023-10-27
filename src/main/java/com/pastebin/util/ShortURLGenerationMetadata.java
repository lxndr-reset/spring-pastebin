package com.pastebin.util;

import com.pastebin.service.ShortURLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ShortURLGenerationMetadata {
    private static ShortURLService shortURLService;

    @Autowired
    public ShortURLGenerationMetadata(ShortURLService shortURLService) {
        ShortURLGenerationMetadata.shortURLService = shortURLService;
    }

    //When table is empty, we make our last sequence "a"
    public static String getLastGeneratedSequence() {
        return shortURLService.getLastGeneratedSequence().orElse("a");
    }
}
