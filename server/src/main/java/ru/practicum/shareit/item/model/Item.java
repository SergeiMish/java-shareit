package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.*;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.HashSet;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "items")
@Builder
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "available")
    private boolean available;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private Set<Booking> bookings = new HashSet<>();
    ;

    @OneToMany(mappedBy = "item", fetch = FetchType.LAZY)
    private Set<Comment> comments = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "request_id")
    private ItemRequest request;
}