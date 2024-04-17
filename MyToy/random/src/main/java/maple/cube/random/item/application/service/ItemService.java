package maple.cube.random.item.application.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import maple.cube.random.item.application.dto.request.ItemRequestDTO;
import maple.cube.random.item.domain.Item;
import maple.cube.random.item.domain.ItemRepository;

@RequiredArgsConstructor
@Service
public class ItemService {
	private final ItemRepository itemRepository;

	@Transactional
	public Item save(ItemRequestDTO itemRequestDTO) {
		return itemRepository.save(itemRequestDTO.toEntity());
	}

	public List<Item> findAll() {
		return itemRepository.findAll();
	}
}
