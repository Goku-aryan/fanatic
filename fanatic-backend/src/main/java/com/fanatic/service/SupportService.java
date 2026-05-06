package com.fanatic.service;

import com.fanatic.dto.common.PagedResponse;
import com.fanatic.dto.support.SupportTicketCreateRequest;
import com.fanatic.dto.support.SupportTicketDTO;
import com.fanatic.dto.support.TicketResponseRequest;
import com.fanatic.entity.SupportTicket;
import com.fanatic.entity.TicketStatus;
import com.fanatic.entity.User;
import com.fanatic.exception.BadRequestException;
import com.fanatic.exception.ResourceNotFoundException;
import com.fanatic.repository.SupportTicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportService {

    private final SupportTicketRepository supportTicketRepository;
    private final UserService userService;

    // ========== CREATE TICKET ==========
    @Transactional
    public SupportTicketDTO createTicket(SupportTicketCreateRequest request) {
        User user = userService.getCurrentUser();

        SupportTicket ticket = SupportTicket.builder()
                .user(user)
                .subject(request.getSubject())
                .message(request.getMessage())
                .status(TicketStatus.OPEN)
                .build();

        SupportTicket saved = supportTicketRepository.save(ticket);
        return mapToDTO(saved);
    }

    // ========== GET MY TICKETS ==========
    @Transactional(readOnly = true)
    public PagedResponse<SupportTicketDTO> getMyTickets(int page, int size) {
        UUID userId = userService.getCurrentUserId();
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<SupportTicket> tickets = supportTicketRepository.findByUserId(userId, pageable);
        return mapToPagedResponse(tickets);
    }

    // ========== GET TICKET BY ID ==========
    @Transactional(readOnly = true)
    public SupportTicketDTO getTicketById(UUID ticketId) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        // Check if user owns ticket or is admin
        UUID currentUserId = userService.getCurrentUserId();
        User currentUser = userService.getCurrentUser();

        if (!ticket.getUser().getId().equals(currentUserId) &&
            currentUser.getRole() != com.fanatic.entity.UserRole.ADMIN) {
            throw new BadRequestException("You can only view your own tickets");
        }

        return mapToDTO(ticket);
    }

    // ========== GET ALL TICKETS (Admin) ==========
    @Transactional(readOnly = true)
    public PagedResponse<SupportTicketDTO> getAllTickets(String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<SupportTicket> tickets;

        if (status != null && !status.isEmpty()) {
            try {
                TicketStatus ticketStatus = TicketStatus.valueOf(status.toUpperCase());
                tickets = supportTicketRepository.findByStatus(ticketStatus, pageable);
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + status);
            }
        } else {
            tickets = supportTicketRepository.findAllOrderedByPriority(pageable);
        }

        return mapToPagedResponse(tickets);
    }

    // ========== RESPOND TO TICKET (Admin) ==========
    @Transactional
    public SupportTicketDTO respondToTicket(UUID ticketId, TicketResponseRequest request) {
        User admin = userService.getCurrentUser();

        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        ticket.setAdminResponse(request.getResponse());
        ticket.setRespondedBy(admin);

        if (request.getStatus() != null) {
            try {
                ticket.setStatus(TicketStatus.valueOf(request.getStatus().toUpperCase()));
            } catch (IllegalArgumentException e) {
                throw new BadRequestException("Invalid status: " + request.getStatus());
            }
        } else {
            ticket.setStatus(TicketStatus.RESOLVED);
        }

        SupportTicket saved = supportTicketRepository.save(ticket);
        return mapToDTO(saved);
    }

    // ========== UPDATE TICKET STATUS (Admin) ==========
    @Transactional
    public SupportTicketDTO updateTicketStatus(UUID ticketId, String status) {
        SupportTicket ticket = supportTicketRepository.findById(ticketId)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket", "id", ticketId));

        try {
            ticket.setStatus(TicketStatus.valueOf(status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }

        SupportTicket saved = supportTicketRepository.save(ticket);
        return mapToDTO(saved);
    }

    // ========== MAP TO DTO ==========
    private SupportTicketDTO mapToDTO(SupportTicket ticket) {
        return SupportTicketDTO.builder()
                .id(ticket.getId())
                .userId(ticket.getUser().getId())
                .username(ticket.getUser().getUsername())
                .subject(ticket.getSubject())
                .message(ticket.getMessage())
                .status(ticket.getStatus().name())
                .adminResponse(ticket.getAdminResponse())
                .respondedByUsername(ticket.getRespondedBy() != null ?
                        ticket.getRespondedBy().getUsername() : null)
                .createdAt(ticket.getCreatedAt())
                .updatedAt(ticket.getUpdatedAt())
                .build();
    }

    // ========== MAP TO PAGED RESPONSE ==========
    private PagedResponse<SupportTicketDTO> mapToPagedResponse(Page<SupportTicket> page) {
        List<SupportTicketDTO> content = page.getContent().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        return PagedResponse.<SupportTicketDTO>builder()
                .content(content)
                .page(page.getNumber())
                .size(page.getSize())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }
}