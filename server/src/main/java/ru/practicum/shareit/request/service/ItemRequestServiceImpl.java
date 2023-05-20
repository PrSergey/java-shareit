package ru.practicum.shareit.request.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.dto.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {

    ItemRequestRepository itemRequestRepository;
    UserRepository userRepository;
    ItemRepository itemRepository;

    @Transactional
    @Override
    public ItemRequestResponseDto save(Long userId, ItemRequestDto itemRequestDto) {
        checkUser(userId);

        LocalDateTime createTime = LocalDateTime.now();
        ItemRequest itemRequest = new ItemRequest(userId, itemRequestDto.getDescription(), createTime);
        return ItemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestResponseDto> getPersonalRequests(Long userId) {
        checkUser(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestor(userId);
        return getItemRequestResponseDtos(itemRequests);
    }

    private List<ItemRequestResponseDto> getItemRequestResponseDtos(List<ItemRequest> itemRequests) {
        Map<Long, ItemDto> itemsMap = getItemDtoByRequestsId(itemRequests.stream()
                .map(ItemRequest::getId)
                .collect(Collectors.toList()));

        return itemRequests.stream()
                .map(ir -> ItemRequestMapper.toItemRequestDto(ir, itemsMap.containsKey(ir.getId())
                        ? List.of(itemsMap.get(ir.getId())) : List.of())).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Override
    public ItemRequestResponseDto getItemRequestById(Long userId, Long requestId) {
        checkUser(userId);

        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new ExistenceException("Запрос вещь с id=" + requestId + " не найден в базе."));
        List<ItemDto> items = itemRepository.findAllByRequest_id(requestId)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
        return ItemRequestMapper.toItemRequestDto(itemRequest, items);
    }

    @Transactional(readOnly = true)
    @Override
    public List<ItemRequestResponseDto> getItemRequests(Long userId, PageRequest pageRequest) {
        checkUser(userId);

        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequestorNot(userId, pageRequest);
        return getItemRequestResponseDtos(itemRequests);
    }

    public Map<Long, ItemDto> getItemDtoByRequestsId(List<Long> requestsId) {
        return itemRepository.findAllByRequest_IdIn(requestsId)
                .stream()
                .collect(Collectors.toMap(item -> item.getRequest().getId(), ItemMapper::toItemDto));
    }

    private void checkUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ExistenceException("Пользвателя с id=" + userId + " не найден в базе.");
        }
    }


}
