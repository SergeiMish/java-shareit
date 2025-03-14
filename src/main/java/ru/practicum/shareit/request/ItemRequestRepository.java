package ru.practicum.shareit.request;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items " +
            "WHERE ir.requester.id = :userId ORDER BY ir.created DESC")
    List<ItemRequest> findByRequesterIdOrderByCreatedDesc(@Param("userId") Long userId);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items " +
            "WHERE ir.requester.id <> :userId ORDER BY ir.created DESC")
    List<ItemRequest> findByRequesterIdNotOrderByCreatedDesc(@Param("userId") Long userId);

    @Query("SELECT ir FROM ItemRequest ir LEFT JOIN FETCH ir.items " +
            "WHERE ir.id = :requestId")
    Optional<ItemRequest> findByIdWithItems(@Param("requestId") Long requestId);
}