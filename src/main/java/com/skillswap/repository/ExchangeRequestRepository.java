package com.skillswap.repository;

import com.skillswap.entity.ExchangeRequest;
import com.skillswap.entity.ExchangeRequest.RequestStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ExchangeRequestRepository extends JpaRepository<ExchangeRequest, Long> {

    List<ExchangeRequest> findBySenderId(Long senderId);

    List<ExchangeRequest> findByReceiverId(Long receiverId);

    List<ExchangeRequest> findBySenderIdAndStatus(Long senderId, RequestStatus status);

    List<ExchangeRequest> findByReceiverIdAndStatus(Long receiverId, RequestStatus status);

    boolean existsBySenderIdAndReceiverIdAndStatus(Long senderId, Long receiverId, RequestStatus status);

    @Query("SELECT e FROM ExchangeRequest e " +
            "JOIN FETCH e.sender " +
            "JOIN FETCH e.receiver " +
            "JOIN FETCH e.senderSkill " +
            "JOIN FETCH e.receiverSkill " +
            "WHERE e.sender.id = :userId")
    List<ExchangeRequest> findBySenderIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT e FROM ExchangeRequest e " +
            "JOIN FETCH e.sender " +
            "JOIN FETCH e.receiver " +
            "JOIN FETCH e.senderSkill " +
            "JOIN FETCH e.receiverSkill " +
            "WHERE e.receiver.id = :userId")
    List<ExchangeRequest> findByReceiverIdWithDetails(@Param("userId") Long userId);

    @Query("SELECT e FROM ExchangeRequest e " +
            "JOIN FETCH e.sender " +
            "JOIN FETCH e.receiver " +
            "JOIN FETCH e.senderSkill " +
            "JOIN FETCH e.receiverSkill " +
            "WHERE e.id = :id")
    Optional<ExchangeRequest> findByIdWithDetails(@Param("id") Long id);
}