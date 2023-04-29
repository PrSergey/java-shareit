package ru.practicum.shareit.item.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.item.model.ItemWithComments;

import java.util.List;

public interface ItemWithCommentsRepository extends JpaRepository<ItemWithComments, Long> {

    List<ItemWithComments> findAllByOwner(Long id);

}
