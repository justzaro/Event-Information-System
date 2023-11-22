package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CartItemDto;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.service.implementation.CartItemServiceImpl;
import com.example.eventinformationsystembackend.service.implementation.OrderServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartItemController {

    private final CartItemServiceImpl cartItemServiceImpl;
    private final OrderServiceImpl orderServiceImpl;

    @Autowired
    public CartItemController(CartItemServiceImpl cartItemServiceImpl, OrderServiceImpl orderServiceImpl) {
        this.cartItemServiceImpl = cartItemServiceImpl;
        this.orderServiceImpl = orderServiceImpl;
    }

    @GetMapping("/coupon/{username}")
    public Double applyCoupon(@PathVariable String username,
                              @RequestParam(value = "couponCode") String couponCode) {
        return orderServiceImpl.getCouponDiscount(username, couponCode);
    }

    @GetMapping("/number/{username}")
    public Integer getAllCartItemsNumberForUser(
            @PathVariable String username) {
        return cartItemServiceImpl.getAllCartItemsNumberForUser(username);
    }

    @GetMapping("/{username}")
    public List<CartItemDtoResponse> getAllCartItemsForUser(
            @PathVariable String username) {
        return cartItemServiceImpl.getAllCartItemsForUser(username);
    }

    @PostMapping
    public CartItemDtoResponse addCartItem(@RequestBody @Valid CartItemDto cartItemDto) {
        return cartItemServiceImpl.addCartItem(cartItemDto);
    }

    @PostMapping("/decrease")
    public ResponseEntity<Void> decreaseCartItemTicketQuantity(@RequestBody @Valid CartItemDto cartItemDto) {
        cartItemServiceImpl.decreaseCartItemTicketQuantity(cartItemDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable("id") Long id) {
        cartItemServiceImpl.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }
}