package com.avi.eCommerce.controller;

import com.avi.eCommerce.dto.OrderDto;
import com.avi.eCommerce.model.Order;
import com.avi.eCommerce.response.ApiResponse;
import com.avi.eCommerce.service.order.IOrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestController
@RequestMapping("${api.prefix}/orders")
public class OrderController {

    private final IOrderService orderService;

    public OrderController(IOrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/order/{userId}")
    public ResponseEntity<ApiResponse> createOrder(@PathVariable Long userId) {
        try {
            Order order = orderService.placeOrder(userId);
            OrderDto orderDto = orderService.convertToDto(order);
            System.out.println("Here OrderDto is "+orderDto);
            return ResponseEntity.ok(new ApiResponse("Order created successfully",orderDto));
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to create order",null));
        }
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<ApiResponse> getOrderById(@PathVariable Long orderId) {
        try {
            OrderDto orderDto = orderService.getOrderById(orderId);
            return ResponseEntity.ok(new ApiResponse("Order fetched successfully",orderDto));
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to fetch order",null));
        }
    }

    @GetMapping("/orders/{userId}")
    public ResponseEntity<ApiResponse> getUserOrders(@PathVariable Long userId) {
        try {
            List<OrderDto> orderDtos = orderService.getOrdersByUserId(userId);
            return ResponseEntity.ok(new ApiResponse("Order fetched successfully",orderDtos));
        } catch (Exception e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(new ApiResponse("Failed to fetch order",null));
        }
    }

}
