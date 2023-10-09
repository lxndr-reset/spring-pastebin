package com.pastebin.repository;

import com.pastebin.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageAccessRepo extends JpaRepository<Message, Long> {
    @Modifying
    List<Message> deleteAllByDeletedIsTrue();

    long countByDeletedIsFalse();
}