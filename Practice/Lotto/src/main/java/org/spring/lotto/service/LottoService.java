package org.spring.lotto.service;

import org.spring.lotto.entity.User;
import org.spring.lotto.reopsitory.UserRepository;
import org.spring.lotto.ui.model.LottoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;

@Transactional(readOnly = true)
@Service
public class LottoService {
	private final UserRepository userRepository;

	public LottoService (UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Transactional
	public LottoDTO buyLotto(LottoDTO lottoDTO) {

		String first = lottoDTO.getFirstLotto();
		String second = lottoDTO.getSecondLotto();

		// 발표날은 매주 목요일 8시30분임
		// 내가 구매한 날짜랑 비교를해서 어떻게 불가능한가?


		userRepository.save(User.builder()
				.firstLotto(first)
				.secondLotto(second)
				.round(lottoDTO.getRound())
				.amount(lottoDTO.getAmount())
				.createAt(lottoDTO.getCreateAt())
				.announcementDay(lottoDTO.getAnnouncementDay()) // 발표날은 요청 값이 아닌, 응답값 설정을 해둬야함.
			.build()
		);
		// 로또를 구매한다

		// 번호는 자동으로 추천 받을거다.

		// 추천 받은 로또를 구매한다.

		// DB에 넣는다.
		return LottoDTO.builder()

			.build();
	}

	// 로또 번호 추천
	// List 로 담아서 반환한다.
	// List 에 꺼내서 2개만 사용한다?

}
