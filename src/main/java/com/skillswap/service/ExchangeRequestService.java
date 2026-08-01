package com.skillswap.service;

import com.skillswap.dto.ExchangeRequestDTO;
import com.skillswap.dto.ExchangeRequestResponseDTO;
import com.skillswap.dto.SkillDTO;
import com.skillswap.dto.UserResponseDTO;
import com.skillswap.entity.ExchangeRequest;
import com.skillswap.entity.ExchangeRequest.RequestStatus;
import com.skillswap.entity.Skill;
import com.skillswap.entity.User;
import com.skillswap.repository.ExchangeRequestRepository;
import com.skillswap.repository.SkillRepository;
import com.skillswap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExchangeRequestService {

    private final ExchangeRequestRepository exchangeRequestRepository;
    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Transactional
    public ExchangeRequestResponseDTO sendRequest(ExchangeRequestDTO requestDTO, Long senderId) {
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new IllegalArgumentException("Sender not found"));

        User receiver = userRepository.findById(requestDTO.getReceiverId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver not found"));

        Skill senderSkill = skillRepository.findById(requestDTO.getSenderSkillId())
                .orElseThrow(() -> new IllegalArgumentException("Sender skill not found"));

        Skill receiverSkill = skillRepository.findById(requestDTO.getReceiverSkillId())
                .orElseThrow(() -> new IllegalArgumentException("Receiver skill not found"));

        if (exchangeRequestRepository.existsBySenderIdAndReceiverIdAndStatus(
                senderId, requestDTO.getReceiverId(), RequestStatus.PENDING)) {
            throw new IllegalStateException("You already have a pending request with this user");
        }

        ExchangeRequest request = new ExchangeRequest();
        request.setSender(sender);
        request.setReceiver(receiver);
        request.setSenderSkill(senderSkill);
        request.setReceiverSkill(receiverSkill);
        request.setStatus(RequestStatus.PENDING);
        request.setMessage(requestDTO.getMessage());

        ExchangeRequest saved = exchangeRequestRepository.save(request);
        return convertToDTO(saved);
    }

    @Transactional
    public ExchangeRequestResponseDTO acceptRequest(Long requestId, Long userId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalStateException("You are not authorized to accept this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("This request is already " + request.getStatus());
        }

        request.setStatus(RequestStatus.ACCEPTED);
        ExchangeRequest updated = exchangeRequestRepository.save(request);
        return convertToDTO(updated);
    }

    @Transactional
    public ExchangeRequestResponseDTO rejectRequest(Long requestId, Long userId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getReceiver().getId().equals(userId)) {
            throw new IllegalStateException("You are not authorized to reject this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("This request is already " + request.getStatus());
        }

        request.setStatus(RequestStatus.REJECTED);
        ExchangeRequest updated = exchangeRequestRepository.save(request);
        return convertToDTO(updated);
    }

    @Transactional
    public ExchangeRequestResponseDTO cancelRequest(Long requestId, Long userId) {
        ExchangeRequest request = exchangeRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("Request not found"));

        if (!request.getSender().getId().equals(userId)) {
            throw new IllegalStateException("You are not authorized to cancel this request");
        }

        if (request.getStatus() != RequestStatus.PENDING) {
            throw new IllegalStateException("This request is already " + request.getStatus());
        }

        request.setStatus(RequestStatus.CANCELLED);
        ExchangeRequest updated = exchangeRequestRepository.save(request);
        return convertToDTO(updated);
    }

    public List<ExchangeRequestResponseDTO> getMySentRequests(Long userId) {
        return exchangeRequestRepository.findBySenderIdWithDetails(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    public List<ExchangeRequestResponseDTO> getMyReceivedRequests(Long userId) {
        return exchangeRequestRepository.findByReceiverIdWithDetails(userId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private ExchangeRequestResponseDTO convertToDTO(ExchangeRequest request) {
        return new ExchangeRequestResponseDTO(
                request.getId(),
                convertToUserDTO(request.getSender()),
                convertToUserDTO(request.getReceiver()),
                convertToSkillDTO(request.getSenderSkill()),
                convertToSkillDTO(request.getReceiverSkill()),
                request.getStatus(),
                request.getMessage(),
                request.getCreatedAt(),
                request.getUpdatedAt()
        );
    }

    private UserResponseDTO convertToUserDTO(User user) {
        return new UserResponseDTO(user.getId(), user.getName(), user.getEmail(), user.getCity(), user.getBio());
    }

    private SkillDTO convertToSkillDTO(Skill skill) {
        return new SkillDTO(skill.getId(), skill.getName());
    }
}