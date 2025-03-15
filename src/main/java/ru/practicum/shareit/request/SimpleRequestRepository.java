package ru.practicum.shareit.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.SimpleRequest;

import java.util.List;

public interface SimpleRequestRepository extends JpaRepository<SimpleRequest, Long> {
    @Query("SELECT r FROM SimpleRequest r WHERE r.requester.id = :userId ORDER BY r.created DESC")
    List<SimpleRequest> findByRequesterIdOrderByCreatedDesc(@Param("userId") Long userId);
}
