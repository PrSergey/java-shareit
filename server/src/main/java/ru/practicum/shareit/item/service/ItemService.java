package ru.practicum.shareit.item.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;

import java.util.List;


public interface ItemService {

    ItemDto add(Long userId, ItemDto item);

    ItemResponseDto getItem(Long userId, Long itemId);

    List<ItemResponseDto> getUsersItem(Long userId, PageRequest pageRequest);

    ItemDto updateItem(Long userId, Long itemId, ItemDto item);

    List<ItemDto> searchItem(String text, PageRequest pageRequest);

    CommentResponseDto saveComment(Long itemId, Long userId, CommentRequestDto commentRequest);

}
