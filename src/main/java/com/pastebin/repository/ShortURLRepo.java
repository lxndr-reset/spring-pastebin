package com.pastebin.repository;

import com.pastebin.entity.ShortURL;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShortURLRepo extends JpaRepository<ShortURL, Long> {
    long countAllByMessageNull();

    Optional<ShortURL> getFirstShortURLByMessageIsNull();

    Optional<ShortURL> findByUrlValue(String value);

    void deleteShortURLByUrlValue(String value);
    @Query("SELECT val FROM ShortURL.urlValue val ORDER BY val DESC LIMIT 1")
    String getLastGeneratedSequence();
}
