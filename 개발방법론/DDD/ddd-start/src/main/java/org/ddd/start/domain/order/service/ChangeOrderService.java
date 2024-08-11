package org.ddd.start.domain.order.service;

import org.ddd.start.domain.order.ShippingInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class ChangeOrderService {

}
