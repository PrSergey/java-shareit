package ru.practicum.shareit.request.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exception.ExistenceException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestResponseDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {

    @Mock
    ItemRequestRepository itemRequestRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ItemRepository itemRepository;

    @InjectMocks
    ItemRequestServiceImpl service;

    @Captor
    ArgumentCaptor<ItemRequest> argumentCaptor;

    private ItemRequest createItemRequest() {
        return new ItemRequest(1L, 1L,
                "description", LocalDateTime.now(), List.of(new Item()));
    }

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
                .request(new ItemRequest())
                .build();
    }

    @Test
    void save_whenUserNotFound_thenExistenceExceptionThrow() {
        long userId = 1L;

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> service.save(1L, new ItemRequestDto()));

        assertEquals(ex.getMessage(), "Пользвателя с id=" + userId + " не найден в базе.");
    }

    @Test
    void save_whenDataIsValid_thenReturnItemRequestResponseDto() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        ItemRequest itemRequest = createItemRequest();
        when(itemRequestRepository.save(any()))
                .thenReturn(itemRequest);
        ItemRequestDto itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription(itemRequest.getDescription());

        service.save(userId, itemRequestDto);

        verify(itemRequestRepository).save(argumentCaptor.capture());
        ItemRequest itemRequestForSave = argumentCaptor.getValue();
        itemRequestForSave.setId(itemRequest.getId());
        assertEquals(itemRequestForSave, itemRequest);
    }

    @Test
    void getPersonalRequests_whenNotFoundItemForRequest_thenReturnItemRequestResponseDtoWithoutItem() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        ItemRequest itemRequest = createItemRequest();
        itemRequest.setRequestor(userId);
        when(itemRequestRepository.findAllByRequestor(userId))
                .thenReturn(List.of(itemRequest));
        Item item = createItem();
        when(itemRepository.findAllByRequest_IdIn(any()))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> personalRequests = service.getPersonalRequests(userId);

        assertEquals(personalRequests.size(), 1);
        assertEquals(personalRequests.get(0).getId(), itemRequest.getId());
        assertTrue(personalRequests.get(0).getItems().isEmpty());
    }

    @Test
    void getPersonalRequests_whenNotFoundItemForRequest_thenReturn_ItemRequestResponseDtoWithItem() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        ItemRequest itemRequest = createItemRequest();
        itemRequest.setRequestor(userId);
        when(itemRequestRepository.findAllByRequestor(userId))
                .thenReturn(List.of(itemRequest));
        Item item = createItem();
        item.setRequest(itemRequest);
        when(itemRepository.findAllByRequest_IdIn(any()))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> personalRequests = service.getPersonalRequests(userId);

        assertEquals(personalRequests.size(), 1);
        assertEquals(personalRequests.get(0).getId(), itemRequest.getId());
        assertEquals(personalRequests.get(0).getItems().get(0).getId(), item.getId());
    }

    @Test
    void getItemRequestById_whenNotFoundItemRequest_thenExistenceExceptionThrow() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.empty());
        long requestId = 1L;

        ExistenceException ex = assertThrows(ExistenceException.class,
                () -> service.getItemRequestById(userId, requestId));
        assertEquals(ex.getMessage(), "Запрос вещь с id=" + requestId + " не найден в базе.");
    }

    @Test
    void getItemRequestById_whenFoundItemRequestWithoutItems_thenReturnItemRequestResponseDtoWithoutItem() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        ItemRequest itemRequest = createItemRequest();
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.of(itemRequest));

        ItemRequestResponseDto itemRequestById = service.getItemRequestById(userId, itemRequest.getId());

        assertTrue(itemRequestById.getItems().isEmpty());
        assertEquals(itemRequestById.getId(), itemRequest.getId());
    }

    @Test
    void getItemRequestById_whenFoundItemRequestWithItems_thenRetirnItemRequestResponseDtoWithItem() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        ItemRequest itemRequest = createItemRequest();
        when(itemRequestRepository.findById(any()))
                .thenReturn(Optional.of(itemRequest));
        Item item = createItem();
        item.setRequest(itemRequest);
        when(itemRepository.findAllByRequest_id(itemRequest.getId()))
                .thenReturn(List.of(item));

        ItemRequestResponseDto itemRequestById = service.getItemRequestById(userId, itemRequest.getId());

        assertEquals(itemRequestById.getItems().size(), 1);
        assertEquals(itemRequestById.getId(), itemRequest.getId());
        assertEquals(itemRequestById.getItems().get(0).getId(), item.getId());
    }

    @Test
    void getItemRequests() {
        long userId = 1L;
        when(userRepository.existsById(userId))
                .thenReturn(true);
        ItemRequest itemRequest = createItemRequest();
        when(itemRequestRepository.findAllByRequestorNot(userId, PageRequest.of(2, 2)))
                .thenReturn(List.of(itemRequest));
        Item item = createItem();
        when(itemRepository.findAllByRequest_IdIn(any()))
                .thenReturn(List.of(item));

        List<ItemRequestResponseDto> itemRequests = service.getItemRequests(userId, PageRequest.of(2, 2));

        assertEquals(itemRequests.size(), 1);
    }
}