package com.pastebin.repository;

import com.pastebin.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface MessageRepo extends JpaRepository<Message, Long> {
    long countByDeletedIsFalse();

    Optional<Message> findMessageByShortURLUrlValue(String value);

    @Query("select m from Message m join fetch m.user u where m.deleted = false and u.email = :userEmail")
    Set<Message> getMessagesByUser_Email(@Param("userEmail") String userEmail);

    @Query("SELECT m from Message m where m.deleted = true or m.deletionDate <= :timestamp")
    List<Message> findAllByDeletedIsTrueOrDeletionDateIsLessThanEqual(@Param("timestamp") Timestamp timestamp);
}