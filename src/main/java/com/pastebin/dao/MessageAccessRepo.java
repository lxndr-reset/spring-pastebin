package com.pastebin.dao;

import com.pastebin.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageAccessRepo extends JpaRepository<Message, Long> {
    @Transactional
    @Modifying
    @Query("delete from Message where isDeleted = true")
    void deleteAllMessagesByDeleted();
}