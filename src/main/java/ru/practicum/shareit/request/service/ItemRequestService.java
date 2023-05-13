package ru.practicum.shareit.request.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.util.List;

public interface ItemRequestService {

    ItemRequestResponseDto save(Long userId, ItemRequestDto itemRequestDto);

    List<ItemRequestResponseDto> getPersonalRequests(Long userId);

    ItemRequestResponseDto getItemRequestById(Long userId, Long requestId);

    List<ItemRequestResponseDto> getItemRequests(Long userId, PageRequest pageRequest);

}
