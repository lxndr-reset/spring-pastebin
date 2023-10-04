package com.pastebin.dao;

import com.pastebin.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MessageAccessRepo extends JpaRepository<Message, String> {

}
