package com.pastebin.repository;

import com.pastebin.entity.ShortURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortURLAccessRepo extends JpaRepository<ShortURL, Long> {
    long countAllByMessageNull();

    Optional<ShortURL> getFirstShortURLByMessageIsNullOrMessageDeletedIsTrue();

    Optional<ShortURL> findByUrlValue(String value);

    void deleteShortURLByUrlValue(String value);
}
