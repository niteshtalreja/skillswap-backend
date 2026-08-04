package com.skillswap.dto;

import java.time.LocalDateTime;

public class MessageDTO {

    private Long id;
    private Long senderId;
    private String senderName;
    private Long receiverId;
    private String receiverName;
    private Long exchangeRequestId;
    private String content;
    private boolean isRead;
    private LocalDateTime createdAt;

    // ✅ DEFAULT CONSTRUCTOR
    public MessageDTO() {}

    // ✅ ALL-ARGS CONSTRUCTOR
    public MessageDTO(Long id, Long senderId, String senderName, Long receiverId,
                      String receiverName, Long exchangeRequestId, String content,
                      boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.senderName = senderName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.exchangeRequestId = exchangeRequestId;
        this.content = content;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // ✅ GETTERS
    public Long getId() { return id; }
    public Long getSenderId() { return senderId; }
    public String getSenderName() { return senderName; }
    public Long getReceiverId() { return receiverId; }
    public String getReceiverName() { return receiverName; }
    public Long getExchangeRequestId() { return exchangeRequestId; }
    public String getContent() { return content; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // ✅ SETTERS
    public void setId(Long id) { this.id = id; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    public void setSenderName(String senderName) { this.senderName = senderName; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public void setExchangeRequestId(Long exchangeRequestId) { this.exchangeRequestId = exchangeRequestId; }
    public void setContent(String content) { this.content = content; }
    public void setRead(boolean read) { isRead = read; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}