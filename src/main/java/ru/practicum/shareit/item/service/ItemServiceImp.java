package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.constant.BookingStatus;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.storage.CommentRepository;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ItemServiceImp implements ItemService {

    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;


    @Transactional
    @Override
    public ItemDto add(Long userId, ItemDto itemDto) {

        if (itemDto.getAvailable() == null) {
            throw new ValidationException("При добавление вещи, не указан статус доступности");
        }
        if (itemDto.getName() == null || itemDto.getName().isBlank()) {
            throw new ValidationException("При добавление вещи, не указано имя");
        }
        if (itemDto.getDescription() == null) {
            throw new ValidationException("При добавление вещи, нет описания");
        }
        getAndCheckUserFromMemory(userId);

        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemResponseDto getItem(Long userId, Long itemId) {
        getAndCheckUserFromMemory(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExistenceException("Вещь с id=" + itemId + " не найден в базе."));
        List<Comment> comments = commentRepository.findByItem_Id(itemId);

        ItemResponseDto itemResponseDto;
        if (userId.equals(item.getOwner())) {
            BookingForItemDto last = getLastBookingByItemId(itemId);
            BookingForItemDto next = getNextBookingByItemId(itemId);
            itemResponseDto = ItemMapper.toItemWithBookingDto(item, last, next, comments.stream()
                    .map(CommentsMapper::toCommentResponseDto).collect(Collectors.toList()));
        } else {
            itemResponseDto = ItemMapper.toItemWithBookingDto(item, comments.stream()
                    .map(CommentsMapper::toCommentResponseDto).collect(Collectors.toList()));
        }

        return itemResponseDto;

    }

    private BookingForItemDto getLastBookingByItemId(Long itemId) {
        Booking lastBooking;
        BookingForItemDto last = null;
        if (!bookingRepository.findByItem_IdAndStartIsBeforeAndStatus(itemId, LocalDateTime.now(),
                BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start")).isEmpty()) {
            lastBooking = bookingRepository.findByItem_IdAndStartIsBeforeAndStatus(itemId, LocalDateTime.now(),
                    BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start")).get(0);
            last = new BookingForItemDto(lastBooking.getId(), lastBooking.getBooker().getId());
        }
        return last;
    }

    private BookingForItemDto getNextBookingByItemId(Long itemId) {
        Booking nextBooking;
        BookingForItemDto next = null;
        if (!bookingRepository.findByItem_IdAndStartIsAfterAndStatus(itemId, LocalDateTime.now(),
                BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start")).isEmpty()) {
            nextBooking = bookingRepository.findByItem_IdAndStartIsAfterAndStatus(itemId, LocalDateTime.now(),
                    BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start")).get(0);
            next = new BookingForItemDto(nextBooking.getId(), nextBooking.getBooker().getId());
        }
        return next;
    }

    @Transactional
    @Override
    public CommentResponseDto saveComment(Long itemId, Long userId, CommentRequestDto commentRequest) {
        User author = getAndCheckUserFromMemory(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExistenceException("Предмет с id=" + itemId + " не найден в базе."));
        if (bookingRepository.findByItem_IdAndBooker_IdAndEndIsBefore(itemId, userId, LocalDateTime.now()).isEmpty()) {
            throw new ValidationException("Пользователь с id=" + userId
                    + " не брал в аренду вещь с id= " + itemId
                    + " или не завершил аренду");
        }

        Comment comment = new Comment(commentRequest.getText(), item, author, LocalDateTime.now());
        return CommentsMapper.toCommentResponseDto(commentRepository.save(comment));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemResponseDto> getUsersItem(Long userId) {
        getAndCheckUserFromMemory(userId);

        List<ItemResponseDto> items = new ArrayList<>();
        List<Item> itemsInMemory = itemRepository.findAllByOwner(userId);
        for (Item item: itemsInMemory) {
            BookingForItemDto last = new BookingForItemDto();
            BookingForItemDto next = new BookingForItemDto();
            if (item.getOwner().equals(userId)) {
                    last = getLastBookingByItemId(item.getId());
                    next = getNextBookingByItemId(item.getId());

            }
            List<Comment> comments = commentRepository.findByItem_Id(item.getId());
            items.add(ItemMapper.toItemWithBookingDto(item, last, next, comments.stream()
                    .map(CommentsMapper::toCommentResponseDto).collect(Collectors.toList())));
        }

        return items.stream().sorted(Comparator.comparing(ItemResponseDto::getId)).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto item) {
        Item itemInMemory = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExistenceException("Предмет с id=" + itemId + " не найден в базе."));

        if (!Objects.equals(itemInMemory.getOwner(), userId)) {
            throw new ExistenceException("Пользователь с id=" + userId +
                    " не является собствеником вещи с id=" + itemId);
        }
        if (item.getName() != null)
            itemInMemory.setName(item.getName());
        if (item.getDescription() != null) {
            itemInMemory.setDescription(item.getDescription());
        }
        if (item.getAvailable() != null) {
            itemInMemory.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(itemInMemory));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemDto> searchItem(String text) {
        if (text.isBlank()) {
            return new ArrayList<>();
        }
        return itemRepository.itemWithText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private User getAndCheckUserFromMemory(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException("Пользвателя с id=" + userId + " не найден в базе."));
    }

}
