package com.pastebin.util;

import com.pastebin.entity.ShortURL;
import com.pastebin.service.entity_service.ShortURLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Represents metadata related to the generation of short URLs.
 *
 * This class is responsible for providing information and calculations related to the generation
 * of short URLs. It depends on an instance of the {@link ShortURLService} class to perform the
 * necessary operations.
 *
 * The generation metadata includes the last generated sequence and the generation value.
 * The last generated sequence is the sequence part of the last generated short URL. If the sequence
 * is not available, the default value is "a".
 * The generation value is calculated based on the number of existing short URLs and can be used to
 * determine the next generation sequence.
 *
 * This class is a Spring Framework component and can be managed by the Spring container.
 */
@Component
public class ShortURLGenerationMetadata {
    private static ShortURLService shortURLService;
    private final static double GENERATION_MULTIPLIER = ShortURL.getMultiplier();

    @Autowired
    public ShortURLGenerationMetadata(ShortURLService shortURLService) {
        ShortURLGenerationMetadata.shortURLService = shortURLService;
    }

    public static String getLastGeneratedSequence() {
        return shortURLService.getLastGeneratedSequence()
                .orElse("a"); //When the table is empty
    }

    public static long getGenerationValue() {
        return (long) Math.max(shortURLService.count() * GENERATION_MULTIPLIER, 10);
    }
}
