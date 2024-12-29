package com.avi.eCommerce.service.order;

import com.avi.eCommerce.dto.OrderDto;
import com.avi.eCommerce.model.Order;

import java.util.List;

public interface IOrderService {
    Order placeOrder(Long userId);
    OrderDto getOrderById(Long orderId);

    List<OrderDto> getOrdersByUserId(Long userId);

    OrderDto convertToDto(Order order);
}
