package com.sliit.smartlibrary.service.impl;

import com.sliit.smartlibrary.designpattern.observer.LibraryEvent;
import com.sliit.smartlibrary.designpattern.observer.LibraryEventSubject;
import com.sliit.smartlibrary.entity.Book;
import com.sliit.smartlibrary.entity.Reservation;
import com.sliit.smartlibrary.entity.User;
import com.sliit.smartlibrary.enums.AccountStatus;
import com.sliit.smartlibrary.enums.NotificationType;
import com.sliit.smartlibrary.enums.ReservationStatus;
import com.sliit.smartlibrary.enums.Role;
import com.sliit.smartlibrary.exception.BadRequestException;
import com.sliit.smartlibrary.exception.ResourceNotFoundException;
import com.sliit.smartlibrary.repository.BookRepository;
import com.sliit.smartlibrary.repository.ReservationRepository;
import com.sliit.smartlibrary.repository.UserRepository;
import com.sliit.smartlibrary.service.AssetService;
import com.sliit.smartlibrary.service.ReservationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReservationServiceImpl implements ReservationService {

    private static final String FUNCTION_NAME = "Book Search and Reservation Management";

    private final ReservationRepository reservations;
    private final UserRepository users;
    private final BookRepository books;
    private final AssetService assets;
    private final LibraryEventSubject eventSubject;

    public ReservationServiceImpl(
            ReservationRepository reservations,
            UserRepository users,
            BookRepository books,
            AssetService assets,
            LibraryEventSubject eventSubject
    ) {
        this.reservations = reservations;
        this.users = users;
        this.books = books;
        this.assets = assets;
        this.eventSubject = eventSubject;
    }

    @Override
    public Reservation reserve(Long memberId, Long bookId) {
        User member = users.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found."));

        if (member.getRole() != Role.MEMBER) {
            throw new BadRequestException("Only library members can create reservations.");
        }
        if (member.getStatus() != AccountStatus.ACTIVE) {
            throw new BadRequestException("This account is inactive.");
        }

        Book book = books.findById(bookId)
                .orElseThrow(() -> new ResourceNotFoundException("Book not found."));

        if (assets.availableCount(bookId) <= 0) {
            throw new BadRequestException("This book currently has no available copies.");
        }
        if (reservations.existsByMemberIdAndBookIdAndStatus(memberId, bookId, ReservationStatus.ACTIVE)) {
            throw new BadRequestException("You already have an active reservation for this book.");
        }

        Reservation reservation = new Reservation();
        reservation.setMember(member);
        reservation.setBook(book);
        reservation.setReservedAt(LocalDateTime.now());
        reservation.setExpiresAt(LocalDateTime.now().plusDays(2));
        reservation.setStatus(ReservationStatus.ACTIVE);
        reservation = reservations.save(reservation);

        publish(
                memberId,
                "Reservation created",
                "Your reservation for '" + book.getTitle() + "' is active.",
                NotificationType.RESERVATION,
                "RESERVATION_CREATED"
        );

        return reservation;
    }

    @Override
    public List<Reservation> forMember(Long id) {
        List<Reservation> list = reservations.findByMemberIdOrderByReservedAtDesc(id);
        expire(list);
        return reservations.saveAll(list);
    }

    @Override
    public List<Reservation> getAll() {
        List<Reservation> list = reservations.findAll();
        expire(list);
        return reservations.saveAll(list);
    }

    private void expire(List<Reservation> list) {
        LocalDateTime now = LocalDateTime.now();
        for (Reservation reservation : list) {
            if (reservation.getStatus() == ReservationStatus.ACTIVE
                    && reservation.getExpiresAt() != null
                    && now.isAfter(reservation.getExpiresAt())) {
                reservation.setStatus(ReservationStatus.EXPIRED);
            }
        }
    }

    @Override
    public Reservation updateStatus(Long id, String status) {
        Reservation reservation = reservations.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found."));

        try {
            reservation.setStatus(ReservationStatus.valueOf(status.toUpperCase()));
        } catch (Exception e) {
            throw new BadRequestException("Invalid reservation status.");
        }

        Reservation saved = reservations.save(reservation);
        publish(
                saved.getMember().getId(),
                "Reservation status updated",
                "Reservation for '" + saved.getBook().getTitle() + "' is now " + saved.getStatus() + ".",
                NotificationType.RESERVATION,
                "RESERVATION_STATUS_CHANGED"
        );
        return saved;
    }

    @Override
    public Reservation cancel(Long id, Long memberId) {
        Reservation reservation = reservations.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found."));

        if (!reservation.getMember().getId().equals(memberId)) {
            throw new BadRequestException("You can only cancel your own reservation.");
        }
        if (reservation.getStatus() != ReservationStatus.ACTIVE) {
            throw new BadRequestException("Only active reservations can be cancelled.");
        }

        reservation.setStatus(ReservationStatus.CANCELLED);
        Reservation saved = reservations.save(reservation);

        publish(
                memberId,
                "Reservation cancelled",
                "Your reservation for '" + saved.getBook().getTitle() + "' was cancelled.",
                NotificationType.RESERVATION,
                "RESERVATION_CANCELLED"
        );

        return saved;
    }

    private void publish(
            Long userId,
            String title,
            String message,
            NotificationType type,
            String eventName
    ) {
        eventSubject.notifyObservers(new LibraryEvent(
                userId,
                title,
                message,
                type,
                FUNCTION_NAME,
                eventName
        ));
    }
}
