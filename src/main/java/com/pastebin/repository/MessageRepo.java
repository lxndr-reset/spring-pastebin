package com.pastebin.repository;

import com.pastebin.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    @Modifying
    List<Message> deleteAllByDeletedIsTrue();

    long countByDeletedIsFalse();

    Optional<Message> findMessageByShortURLUrlValue(String value);

    @Modifying
    @Transactional
    @Query("delete from Message m where m.deleted = true or m.deletionDate <= :timestamp")
    List<Message> deleteAllByDeletedIsTrueOrDeletionDateIsGreaterThanEqual(Timestamp timestamp);

}