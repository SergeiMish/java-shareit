package ru.practicum.shareit.item.model;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Entity
@Table(name = "item")
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String description;

    @Column(name = "available")
    private boolean available;

    @Column(name = "owner_id")
    private Long ownerId;

    public Item() {
    }
}