package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constant.BookingStatus;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImpTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Captor
    private ArgumentCaptor<Item> itemArgumentCaptor;

    @Mock
    private ItemRequestRepository itemRequestRepository;

    @InjectMocks
    ItemServiceImp itemService;

    private User createUser() {
        return User.builder()
                .id(1L)
                .name("name")
                .email("email@email.com")
                .build();
    }

    private Item createItem() {
        return Item.builder()
                .available(true)
                .description("description")
                .id(1L)
                .name("name")
                .owner(2L)
                .build();
    }

    private Comment createComment() {
       // return new Comment(1L, "text comments", new Item(), (new User(), LocalDateTime.now());
            return Comment.builder()
                .id(1L)
                .created(LocalDateTime.now())
                .authorName(new User())
                .item(new Item())
                .text("text comments")
                .build();
    }

    private Booking createBooking() {
        return Booking.builder()
                .id(1L)
                .booker(new User(3L, "name", "email@email.com"))
                .item(new Item(4L, 5L, "name", "description", true, new ItemRequest()))
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .status(BookingStatus.WAITING)
                .build();
    }

    @Test
    void add_whenUserNotFoundInMemory_thenExistenceExceptionThrow() {
        ItemDto itemDto = ItemMapper.toItemDto(createItem());
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(false);

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> itemService.add(userId, itemDto));
        assertEquals(ex.getMessage(), "Пользвателя с id=" + userId + " не найден в базе.");
    }

    @Test
    void add_whenItemDtoIsValid_thenReturnItemDto() {
        Item item = createItem();
        ItemDto itemDto = ItemMapper.toItemDto(item);
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRepository.save(any()))
                .thenReturn(item);

        itemService.add(userId, itemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item itemForSave = itemArgumentCaptor.getValue();
        assertEquals(itemForSave.getName(), itemDto.getName());
        assertEquals(itemForSave.getAvailable(), itemDto.getAvailable());
        assertEquals(itemForSave.getDescription(), itemDto.getDescription());
    }

    @Test
    void getItem_whenItemNotFound_thenExistenceExceptionThrow() {
        long userId = 1L;
        long itemId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.empty());

        ExistenceException ex = assertThrows(ExistenceException.class, () -> itemService.getItem(userId, itemId));

        assertEquals(ex.getMessage(), "Вещь с id=" + itemId + " не найден в базе.");
    }

    @Test
    void getItem_whenUserNotEqualsOwner_thenReturnItemResponseDtoWithoutLastAndNextBooking() {
        long userId = 1L;
        long itemId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        Item item = createItem();
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));

        ItemResponseDto getItemFromMemory = itemService.getItem(userId, itemId);

        assertNull(getItemFromMemory.getLastBooking());
        assertNull(getItemFromMemory.getNextBooking());
        assertEquals(getItemFromMemory.getId(), item.getId());
    }

    @Test
    void getItem_whenUserEqualsOwner_thenReturnItemResponseDtoWithoutLastAndNextBooking() {
        Item item = createItem();
        long userId = item.getOwner();
        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRepository.findById(any()))
                .thenReturn(Optional.of(item));
        Booking booking = createBooking();
        when(bookingRepository.findByItem_IdAndStartIsBeforeAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of(booking));
        when(bookingRepository.findByItem_IdAndStartIsAfterAndStatus(any(), any(), any(), any()))
                .thenReturn(List.of(booking));

        ItemResponseDto getItemFromMemory = itemService.getItem(userId, item.getId());

        assertNotNull(getItemFromMemory.getLastBooking());
        assertNotNull(getItemFromMemory.getNextBooking());
        assertEquals(getItemFromMemory.getId(), item.getId());
    }

    @Test
    void getUsersItem() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        Item item = createItem();
        when(itemRepository.findAllByOwner(any(), any()))
                .thenReturn((List.of(item)));
        Comment comment = createComment();
        comment.setItem(item);
        when(commentRepository.findByItem_OwnerEquals(any()))
                .thenReturn(List.of(comment));
        Booking bookingLast = createBooking();
        bookingLast.setItem(item);
        when(bookingRepository.findByItem_OwnerAndStartIsBeforeAndStatus(any(),any(),any(),any(), any()))
                .thenReturn(List.of(bookingLast));
        Booking bookingNext = createBooking();
        bookingNext.setItem(item);
        when(bookingRepository.findByItem_OwnerAndStartIsAfterAndStatus(any(),any(),any(),any(), any()))
                .thenReturn(List.of(bookingNext));

        List<ItemResponseDto> getItems = itemService.getUsersItem(userId, PageRequest.of(2, 2));

        assertEquals(getItems.size(), 1);
        assertEquals(getItems.get(0).getId(), item.getId());
    }

    @Test
    void saveComment_whenUserNotFoundInMemory_thenExistenceExceptionThrow() {
        long userId = 1L;
        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> itemService.saveComment(1L, userId, new CommentRequestDto()));

        assertEquals(ex.getMessage(), "Пользвателя с id=" + userId + " не найден в базе.");
    }

    @Test
    void saveComment_whenItemNotFoundInMemory_thenExistenceExceptionThrow() {
        User user = createUser();
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        long itemId = 1L;
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());


        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> itemService.saveComment(1L, user.getId(), new CommentRequestDto()));

        assertEquals(ex.getMessage(), "Предмет с id=" + itemId + " не найден в базе.");
    }

    @Test
    void saveComment_whenUserNotRentItem_thenValidationExceptionThrow() {
        User user = createUser();
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Item item = createItem();
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        List<Booking> bookings = new ArrayList<>();
        when(bookingRepository.findByItem_IdAndBooker_IdAndEndIsBefore(any(), any(), any()))
                .thenReturn(bookings);

        ValidationException ex = assertThrows(ValidationException.class,
                () -> itemService.saveComment(1L, user.getId(), new CommentRequestDto()));

        assertEquals(ex.getMessage(), "Пользователь с id=" + user.getId()
                + " не брал в аренду вещь с id= " + item.getId()
                + " или не завершил аренду");
    }

    @Test
    void saveComment_whenSaveComment_thenReturnCommentResponseDto() {
        User user = createUser();
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
        Item item = createItem();
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        Booking booking = createBooking();

        when(bookingRepository.findByItem_IdAndBooker_IdAndEndIsBefore(any(), any(), any()))
                .thenReturn(List.of(booking));
        Comment comment = createComment();
        when(commentRepository.save(any()))
                .thenReturn(comment);

        CommentRequestDto commentRequest = new CommentRequestDto(comment.getText());
        CommentResponseDto commentResult = itemService.saveComment(item.getId(), user.getId(),
                commentRequest);

        assertEquals(commentResult.getText(), comment.getText());
        assertEquals(commentResult.getAuthorName(), comment.getAuthorName().getName());
    }


    @Test
    void updateItem_whenItemNotFound_thenExistenceExceptionThrow() {
        long itemId = 1L;
        when(itemRepository.findById(itemId))
                .thenReturn(Optional.empty());

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> itemService.updateItem(1L, itemId, new ItemDto()));

        assertEquals(ex.getMessage(), "Предмет с id=" + itemId + " не найден в базе.");
    }

    @Test
    void updateItem_whenUserEqualOwner_thenExistenceExceptionThrow() {
        Item item = createItem();
        Long userId = 99L;
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> itemService.updateItem(userId, item.getId(), new ItemDto()));

        assertEquals(ex.getMessage(), "Пользователь с id=" + userId +
                " не является собствеником вещи с id=" + item.getId());
    }

    @Test
    void updateItem_whenItemChangeNameDescriptionAvailable_thenReturnItemDto() {
        Item item = createItem();
        Long userId = item.getOwner();
        when(itemRepository.findById(item.getId()))
                .thenReturn(Optional.of(item));
        ItemDto itemDto = ItemMapper.toItemDto(item);
        String newName = "new name";
        String newDescription = "new description";
        Boolean newAvailable = !item.getAvailable();
        itemDto.setAvailable(newAvailable);
        itemDto.setDescription(newDescription);
        itemDto.setName(newName);
        when(itemRepository.save(any()))
                .thenReturn(item);

        itemService.updateItem(userId, item.getId(), itemDto);

        verify(itemRepository).save(itemArgumentCaptor.capture());
        Item itemAfetUpdate = itemArgumentCaptor.getValue();
        assertEquals(itemAfetUpdate.getName(), newName);
        assertEquals(itemAfetUpdate.getDescription(), newDescription);
        assertEquals(itemAfetUpdate.getAvailable(), newAvailable);
    }



    @Test
    void searchItem_whenTextIsBlank_thenReturnEmptyList() {
        List<ItemDto> itemDtos = itemService.searchItem("", PageRequest.of(2, 2));

        assertTrue(itemDtos.isEmpty());
    }
}