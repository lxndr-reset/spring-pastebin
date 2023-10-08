package com.pastebin.service;

import com.pastebin.entity.ShortURL;
import com.pastebin.exception.NoAvailableShortURLException;
import com.pastebin.repository.ShortURLAccessRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@Transactional
public class ShortURLService {
    private final ShortURLAccessRepo shortURLAccessRepo;

    @Autowired
    public ShortURLService(ShortURLAccessRepo shortURLAccessRepo) {
        this.shortURLAccessRepo = shortURLAccessRepo;
    }

    public ShortURL save(ShortURL url) {
        return shortURLAccessRepo.save(url);
    }

    public ShortURL findById(long id) {
        return shortURLAccessRepo.findById(id).orElseThrow(() ->
                new NoSuchElementException("URL with id " + id + " not found"));
    }

    public void deleteById(long id) {
        shortURLAccessRepo.deleteById(id);
    }

    public long countAllByMessageNull() {
        return shortURLAccessRepo.countAllByMessageNull();
    }

    public long count() {
        return shortURLAccessRepo.count();
    }

    public List<ShortURL> saveAll(Iterable<ShortURL> urls) {
        return shortURLAccessRepo.saveAll(urls);
    }

    public ShortURL getByMessageIsNullOrMessageDeleted() throws NoAvailableShortURLException {
        Optional<ShortURL> result = shortURLAccessRepo.getFirstByMessageIsNullOrMessageDeletedIsTrue();

        if (result.isEmpty()) {
            throw new NoAvailableShortURLException("No urls are available! Generation starts now");
        }
        return result.get();
    }

    public ShortURL findByUrlValue(String value) {
        return shortURLAccessRepo.findByUrlValue(value)
                .orElseThrow(NoSuchElementException::new);
    }
}
