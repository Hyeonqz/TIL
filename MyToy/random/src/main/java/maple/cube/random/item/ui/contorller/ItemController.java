package maple.cube.random.item.ui.contorller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import maple.cube.random.item.application.dto.request.ItemRequestDTO;
import maple.cube.random.item.application.service.ItemService;
import maple.cube.random.item.domain.Item;
import maple.cube.random.user.domain.User;

@RequestMapping("api/item")
@RequiredArgsConstructor
@RestController
public class ItemController {
	private final ItemService itemService;

	@PostMapping("/create")
	public Item addItem(@RequestBody ItemRequestDTO itemRequestDTO) {
		return itemService.save(itemRequestDTO);
	}

	@GetMapping()
	public List<Item> getAllItems() {
		return itemService.findAll();
	}

}
