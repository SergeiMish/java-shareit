package ru.practicum.shareit.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


@Builder
@Getter
@Setter
@EqualsAndHashCode
public class UserDto {
    private Long id;
    @Email
    @NotNull
    private String email;
    private String name;
}