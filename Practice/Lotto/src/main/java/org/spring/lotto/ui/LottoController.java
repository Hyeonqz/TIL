package org.spring.lotto.ui;

import org.spring.lotto.service.LottoService;
import org.spring.lotto.ui.model.LottoDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/lotto")
@RestController
public class LottoController {
	private final LottoService lottoService;

	public LottoController (LottoService lottoService) {
		this.lottoService = lottoService;
	}

	@PostMapping("/buy")
	public ResponseEntity<LottoDTO> butLotto(@RequestBody LottoDTO lottoDTO) {



		return ResponseEntity.ok(lottoDTO);
	}

}
