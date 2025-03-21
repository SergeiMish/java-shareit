package ru.practicum.shareit.item;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;


@SpringBootTest
@Transactional
@Rollback
public class ItemServiceImplTest {

    @Autowired
    private ItemService itemService;

    @Autowired
    private UserService userService;

    @Autowired
    private BookingService bookingService;

    private User owner;
    private User anotherUser;
    private ItemDto itemDtoRequest;
    private BookingDto bookingDto;

    @BeforeEach
    public void setUp() {
        owner = User.builder()
                .email("owner@test.com")
                .name("owner")
                .build();
        owner = userService.add(owner);

        anotherUser = User.builder()
                .email("another@test.com")
                .name("another")
                .build();
        anotherUser = userService.add(anotherUser);

        itemDtoRequest = ItemDto.builder()
                .name("testItem")
                .description("testDescription")
                .available(true)
                .build();

        bookingDto = BookingDto.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .status(BookingStatus.APPROVED)
                .build();
    }

    @Test
    void addItem_shouldCreateItem() {
        ItemDto createdItem = itemService.addItem(owner.getId(), itemDtoRequest);

        Assertions.assertThat(createdItem.getName()).isEqualTo(itemDtoRequest.getName());
        Assertions.assertThat(createdItem.getDescription()).isEqualTo(itemDtoRequest.getDescription());
        Assertions.assertThat(createdItem.getAvailable()).isEqualTo(itemDtoRequest.getAvailable());
        Assertions.assertThat(createdItem.getOwnerId()).isEqualTo(owner.getId());
    }

    @Test
    void getItem_shouldReturnItemWithDetails() {
        ItemDto createdItem = itemService.addItem(owner.getId(), itemDtoRequest);
        ItemDto retrievedItem = itemService.getItem(createdItem.getId(), owner.getId());

        Assertions.assertThat(retrievedItem.getId()).isEqualTo(createdItem.getId());
        Assertions.assertThat(retrievedItem.getName()).isEqualTo(createdItem.getName());
    }

    @Test
    void getUserItems_shouldReturnItems() {
        // Создаем пользователя и элемент
        ItemDto createdItem = itemService.addItem(owner.getId(), itemDtoRequest);

        // Создаем и добавляем букинг для созданного элемента
        bookingDto.setItemId(createdItem.getId());
        BookingDto createdBooking = bookingService.addBooking(owner.getId(), bookingDto);

        // Получаем список элементов пользователя
        List<ItemDto> items = itemService.getUserItems(owner.getId());

        // Проверяем результаты
        Assertions.assertThat(items)
                .isNotEmpty()
                .hasSize(1)
                .extracting(ItemDto::getId)
                .contains(createdItem.getId());
    }

    @Test
    void searchItems_shouldFindByName() {
        itemService.addItem(owner.getId(), itemDtoRequest);
        List<ItemDto> result = itemService.searchItems(itemDtoRequest.getName().toLowerCase());

        Assertions.assertThat(result).isNotEmpty();
        Assertions.assertThat(result.get(0).getName()).isEqualTo(itemDtoRequest.getName());
    }

    @Test
    void addComment_shouldCreateComment() {
        ItemDto item = itemService.addItem(owner.getId(), itemDtoRequest);

        bookingDto.setItemId(item.getId());
        BookingDto createdBooking = bookingService.addBooking(anotherUser.getId(), bookingDto);

        bookingService.updateBookingStatus(createdBooking.getId(), true, owner.getId());

        CommentDto commentDto = CommentDto.builder()
                .text("test comment")
                .build();

        CommentDto createdComment = itemService.addComment(anotherUser.getId(), item.getId(), commentDto);

        Assertions.assertThat(createdComment.getText()).isEqualTo(commentDto.getText());
        Assertions.assertThat(createdComment.getAuthorName()).isEqualTo(anotherUser.getName());
    }

    @Test
    void addComment_shouldThrowExceptionWithoutBooking() {
        ItemDto item = itemService.addItem(owner.getId(), itemDtoRequest);

        CommentDto commentDto = CommentDto.builder()
                .text("test comment")
                .build();

        Assertions.assertThatThrownBy(() -> {
                    itemService.addComment(anotherUser.getId(), item.getId(), commentDto);
                }).isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("User has not booked this item with APPROVED status or the booking is not completed");
    }
}