package com.skillswap.repository;

import com.skillswap.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByExchangeRequestIdOrderByCreatedAtAsc(Long exchangeRequestId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.isRead = true WHERE m.exchangeRequest.id = :exchangeId AND m.receiver.id = :userId AND m.isRead = false")
    void markMessagesAsRead(@Param("exchangeId") Long exchangeId, @Param("userId") Long userId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.exchangeRequest.id = :exchangeId AND m.receiver.id = :userId AND m.isRead = false")
    long countUnreadMessages(@Param("exchangeId") Long exchangeId, @Param("userId") Long userId);
}