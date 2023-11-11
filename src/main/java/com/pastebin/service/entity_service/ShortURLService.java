package com.pastebin.service.entity_service;

import com.pastebin.entity.ShortURL;
import com.pastebin.exception.UrlNotExistsException;
import com.pastebin.repository.ShortURLRepo;
import com.pastebin.service.ScheduledOperations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Transactional
public class ShortURLService {
    private final ShortURLRepo shortURLRepo;
    private final ApplicationContext applicationContext;

    @Autowired
    public ShortURLService(ShortURLRepo shortURLRepo, ApplicationContext applicationContext) {
        this.shortURLRepo = shortURLRepo;
        this.applicationContext = applicationContext;
    }

    public ShortURL save(ShortURL url) {
        return shortURLRepo.save(url);
    }

    @Deprecated
    public ShortURL findById(long id) {
        return shortURLRepo.findById(id).orElseThrow(() ->
                new NoSuchElementException("URL with id " + id + " not found"));
    }

    public void deleteById(long id) {
        shortURLRepo.deleteById(id);
    }


    public void deleteByValue(String value) {
        shortURLRepo.deleteShortURLByUrlValue(value);
    }

    public long countAllByMessageNull() {
        return shortURLRepo.countAllByMessageNull();
    }

    public long count() {
        return shortURLRepo.count();
    }

    public List<ShortURL> saveAll(Iterable<ShortURL> urls) {

        return shortURLRepo.saveAll(urls);
    }

    public ShortURL getAvailableShortURL() {
        Optional<ShortURL> result = shortURLRepo.getFirstShortURLByMessageIsNull();

        if (result.isEmpty()) {
            CompletableFuture<Void> generateShortURLTask =
                    CompletableFuture.runAsync(() ->
                            applicationContext.getBean(ScheduledOperations.class).generateLinkValueWithoutCheck()
                    );
            generateShortURLTask.join();
            result = shortURLRepo.getFirstShortURLByMessageIsNull();
        }

        return result.get();
    }

    public ShortURL findByUrlValue(String value) throws UrlNotExistsException {
        Optional<ShortURL> byUrlValue = shortURLRepo.findByUrlValue(value);

        if (byUrlValue.isEmpty()) {
            throw new UrlNotExistsException("Url with link 'https://localhost:8080/message/get/" + value +
                    "' not exists");
        }
        return byUrlValue.get();
    }

    public Optional<String> getLastGeneratedSequence() {
        return shortURLRepo.getLastGeneratedSequence();
    }

}
