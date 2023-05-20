package ru.practicum.shareit.request.storage;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequest, Long> {

    List<ItemRequest> findAllByRequestor(Long userId);

    List<ItemRequest> findAllByRequestorNot(Long userId, PageRequest pageRequest);
}
