package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * TODO Sprint add-controllers.
 */
@Builder
@Getter
@Setter
@EqualsAndHashCode
public class Item {
    private Long id;
    private String name;
    private String description;
    private boolean available;
    private Long ownerId;
}
