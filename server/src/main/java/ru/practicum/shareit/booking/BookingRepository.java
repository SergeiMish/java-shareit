package ru.practicum.shareit.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;

import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    @Query("SELECT b FROM Booking b WHERE b.booker.id = :bookerId ORDER BY b.start DESC")
    List<Booking> findBookingsByBookerId(@Param("bookerId") Long bookerId);

    @Query("SELECT b FROM Booking b WHERE b.item.owner.id = :ownerId ORDER BY b.start DESC")
    List<Booking> findBookingsByOwnerId(@Param("ownerId") Long ownerId);

    @Query("SELECT COUNT(b) > 0 FROM Booking b WHERE b.item.id = :itemId AND b.booker.id = :userId AND b.status = :status")
    boolean existsByItemIdAndBookerIdAndStatus(@Param("itemId") Long itemId,
                                               @Param("userId") Long userId,
                                               @Param("status") BookingStatus status);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start < CURRENT_TIMESTAMP ORDER BY b.start DESC")
    Optional<Booking> findLastBookingByItemId(@Param("itemId") Long itemId);

    @Query("SELECT b FROM Booking b WHERE b.item.id = :itemId AND b.start > CURRENT_TIMESTAMP ORDER BY b.start ASC")
    Optional<Booking> findNextBookingByItemId(@Param("itemId") Long itemId);
}