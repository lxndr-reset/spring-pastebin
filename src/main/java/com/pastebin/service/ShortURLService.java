package com.pastebin.service;

import com.pastebin.entity.ShortURL;
import com.pastebin.exception.NoAvailableShortURLException;
import com.pastebin.exception.UrlNotExistsException;
import com.pastebin.repository.ShortURLAccessRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ShortURLService {
    private final ShortURLAccessRepo shortURLAccessRepo;
    private final ApplicationContext applicationContext;

    @Autowired
    public ShortURLService(ShortURLAccessRepo shortURLAccessRepo, ApplicationContext applicationContext) {
        this.shortURLAccessRepo = shortURLAccessRepo;
        this.applicationContext = applicationContext;
    }

    @CachePut(value = "url", key = "#url.urlValue")
    public ShortURL save(ShortURL url) {
        return shortURLAccessRepo.save(url);
    }

    @Deprecated
    @Cacheable(value = "url", key = "#id")
    public ShortURL findById(long id) {
        return shortURLAccessRepo.findById(id).orElseThrow(() ->
                new NoSuchElementException("URL with id " + id + " not found"));
    }

    @CacheEvict(value = "url", key = "#id")
    public void deleteById(long id) {
        shortURLAccessRepo.deleteById(id);
    }


    @CacheEvict(value = "url", key = "#value")
    public void deleteByValue(String value) {
        shortURLAccessRepo.deleteShortURLByUrlValue(value);
    }

    public long countAllByMessageNull() {
        return shortURLAccessRepo.countAllByMessageNull();
    }

    public long count() {
        return shortURLAccessRepo.count();
    }

    @Transactional
    public List<ShortURL> saveAll(Iterable<ShortURL> urls) {
        List<ShortURL> shortURLS = shortURLAccessRepo.saveAll(urls);
        shortURLS.forEach(this::updateShortURLCache);

        return shortURLS;
    }

    @Cacheable(value = "url", key = "#url.urlValue")
    public void updateShortURLCache(ShortURL url) {
    }

    public ShortURL getAvailableShortURL() throws NoAvailableShortURLException {
        Optional<ShortURL> result = shortURLAccessRepo.getFirstShortURLByMessageIsNullOrMessageDeletedIsTrue();

        if (result.isEmpty()) {
            applicationContext.getBean(ScheduledOperations.class).generateLinkValueWithoutCheck();
            throw new NoAvailableShortURLException("No urls are available! Generation starts now. Please try again in few minutes");
        }

        return result.get();
    }

    @Cacheable(value = "url", key = "#value")
    public ShortURL findByUrlValue(String value) throws UrlNotExistsException {
        Optional<ShortURL> byUrlValue = shortURLAccessRepo.findByUrlValue(value);

        if (byUrlValue.isEmpty()) {
            throw new UrlNotExistsException("Url with link 'https://localhost:8080/message/get/" + value +
                    "' not exists");
        }
        return byUrlValue.get();
    }

}
