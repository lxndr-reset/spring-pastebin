package com.pastebin.dao;

import com.pastebin.entity.Message;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface MessageAccessRepo extends JpaRepository<Message, Long> {

}
