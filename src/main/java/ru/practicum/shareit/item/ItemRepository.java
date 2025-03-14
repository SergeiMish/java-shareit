package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByOwnerId(Long ownerId);

    @Query("SELECT i FROM Item i " +
            "LEFT JOIN FETCH i.bookings b " +
            "LEFT JOIN FETCH i.comments c " +
            "WHERE i.owner.id = :userId")
    List<Item> findItemsWithBookingsAndCommentsByOwnerId(@Param("userId") Long userId);

    Optional<Item> findById(Long id);
    List<Item> findByRequestId(Long requestId);
}