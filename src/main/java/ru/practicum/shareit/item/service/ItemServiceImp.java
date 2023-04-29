package ru.practicum.shareit.item.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
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
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.*;

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
        checkUserFromMemory(userId);

        Item item = ItemMapper.fromItemDto(itemDto);
        item.setOwner(userId);

        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Transactional(readOnly = true)
    @Override
    public ItemResponseDto getItem(Long userId, Long itemId) {
        checkUserFromMemory(userId);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ExistenceException("Вещь с id=" + itemId + " не найден в базе."));
        List<Comment> comments = commentRepository.findByItem_Id(itemId);
        ItemResponseDto itemResponseDto;
        if (userId.equals(item.getOwner())) {
            BookingForItemDto last = getLastBookingByItemId(itemId);
            BookingForItemDto next = getNextBookingByItemId(itemId);
            itemResponseDto = ItemMapper.toItemWithBookingDto(item, last, next,
                    comments.stream().map(CommentsMapper::toCommentResponseDto).collect(toList()));
        } else {
            itemResponseDto = ItemMapper.toItemWithBookingDto(item, comments);
        }

        return itemResponseDto;

    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemResponseDto> getUsersItem(Long userId) {
        checkUserFromMemory(userId);

        List<ItemResponseDto> items = new ArrayList<>();
        Map<Long, Item> itemsInMemory = itemRepository.findAllByOwner(userId)
                .stream()
                .collect(Collectors.toMap(Item::getId, Function.identity()));
        Map<Long, List<CommentResponseDto>> commentMap = commentRepository.findByItem_OwnerEquals(userId)
                .stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId(),
                        mapping(CommentsMapper::toCommentResponseDto, toList())));

        List<Booking> bookingsLast = bookingRepository
                .findByItem_OwnerAndStartIsBeforeAndStatus(userId, LocalDateTime.now(),
                BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"));
        Map<Long, List<Booking>> bookingsLastMap = bookingsLast
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(),
                        mapping(booking -> booking, toList())));

        List<Booking> bookingsNext = bookingRepository
                .findByItem_OwnerAndStartIsAfterAndStatus(userId, LocalDateTime.now(),
                        BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"));
        Map<Long, List<Booking>> bookingsNextMap = bookingsNext
                .stream()
                .collect(Collectors.groupingBy(booking -> booking.getItem().getId(),
                        mapping(booking -> booking, toList())));

        for (Item item: itemsInMemory.values()) {
            BookingForItemDto last = null;
            BookingForItemDto next = null;
            List<CommentResponseDto> comment = new ArrayList<>();

            if (bookingsLastMap.containsKey(item.getId())) {
                last = BookingMapper.toBookingForItemDto(bookingsLastMap.get(item.getId())
                        .stream()
                        .sorted(Comparator.comparing(Booking::getStart)
                                .reversed()).collect(Collectors.toList()).get(0));
            }
            if (bookingsNextMap.containsKey(item.getId())) {
                next = BookingMapper.toBookingForItemDto(bookingsNextMap.get(item.getId())
                        .stream()
                        .sorted(Comparator.comparing(Booking::getStart)).collect(Collectors.toList()).get(0));
            }
            if (commentMap.containsKey(item.getId())) {
                comment = commentMap.get(item.getId());
            }

            items.add(ItemMapper.toItemWithBookingDto(item, last, next, comment));
        }

        return items.stream().sorted(Comparator.comparing(ItemResponseDto::getId)).collect(toList());
    }

    private BookingForItemDto getLastBookingByItemId(Long itemId) {
        Booking lastBooking;
        BookingForItemDto last = null;
        List<Booking> bookingInMemory = bookingRepository.findByItem_IdAndStartIsBeforeAndStatus(itemId, LocalDateTime.now(),
                BookingStatus.APPROVED, Sort.by(Sort.Direction.DESC, "start"));
        if (!bookingInMemory.isEmpty()) {
            lastBooking = bookingInMemory.get(0);
            last = new BookingForItemDto(lastBooking.getId(), lastBooking.getBooker().getId());
        }
        return last;
    }

    private BookingForItemDto getNextBookingByItemId(Long itemId) {
        Booking nextBooking;
        BookingForItemDto next = null;
        List<Booking> bookingInMemory = bookingRepository.findByItem_IdAndStartIsAfterAndStatus(itemId, LocalDateTime.now(),
                BookingStatus.APPROVED, Sort.by(Sort.Direction.ASC, "start"));
        if (!bookingInMemory.isEmpty()) {
            nextBooking = bookingInMemory.get(0);
            next = new BookingForItemDto(nextBooking.getId(), nextBooking.getBooker().getId());
        }
        return next;
    }

    @Transactional
    @Override
    public CommentResponseDto saveComment(Long itemId, Long userId, CommentRequestDto commentRequest) {
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new ExistenceException("Пользвателя с id=" + userId + " не найден в базе."));
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
                .collect(toList());
    }

    private void checkUserFromMemory(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ExistenceException("Пользвателя с id=" + userId + " не найден в базе.");
        }
    }

}
