package com.skillswap.controller;

import com.skillswap.dto.MessageDTO;
import com.skillswap.entity.Message;
import com.skillswap.entity.User;
import com.skillswap.entity.ExchangeRequest;
import com.skillswap.repository.MessageRepository;
import com.skillswap.repository.UserRepository;
import com.skillswap.repository.ExchangeRequestRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ExchangeRequestRepository exchangeRequestRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/chat.send")
    public void sendMessage(@Payload MessageDTO messageDTO) {
        User sender = userRepository.findById(messageDTO.getSenderId()).orElseThrow();
        User receiver = userRepository.findById(messageDTO.getReceiverId()).orElseThrow();
        ExchangeRequest exchangeRequest = exchangeRequestRepository.findById(messageDTO.getExchangeRequestId()).orElseThrow();

        Message message = new Message();
        message.setSender(sender);
        message.setReceiver(receiver);
        message.setExchangeRequest(exchangeRequest);
        message.setContent(messageDTO.getContent());
        message.setCreatedAt(LocalDateTime.now());

        Message saved = messageRepository.save(message);

        MessageDTO response = convertToDTO(saved);

        // Send to receiver
        messagingTemplate.convertAndSendToUser(
                receiver.getId().toString(),
                "/queue/messages",
                response
        );

        // Send confirmation to sender
        messagingTemplate.convertAndSendToUser(
                sender.getId().toString(),
                "/queue/messages",
                response
        );
    }

    @GetMapping("/exchange/{exchangeId}")
    public List<MessageDTO> getMessages(@PathVariable Long exchangeId) {
        return messageRepository.findByExchangeRequestIdOrderByCreatedAtAsc(exchangeId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @PutMapping("/exchange/{exchangeId}/read")
    public void markAsRead(@PathVariable Long exchangeId, @AuthenticationPrincipal User user) {
        messageRepository.markMessagesAsRead(exchangeId, user.getId());
    }

    @GetMapping("/exchange/{exchangeId}/unread")
    public long getUnreadCount(@PathVariable Long exchangeId, @AuthenticationPrincipal User user) {
        return messageRepository.countUnreadMessages(exchangeId, user.getId());
    }

    private MessageDTO convertToDTO(Message message) {
        return new MessageDTO(
                message.getId(),
                message.getSender().getId(),
                message.getSender().getName(),
                message.getReceiver().getId(),
                message.getReceiver().getName(),
                message.getExchangeRequest().getId(),
                message.getContent(),
                message.isRead(),
                message.getCreatedAt()
        );
    }
}