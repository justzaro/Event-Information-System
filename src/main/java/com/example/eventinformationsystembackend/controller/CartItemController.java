package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CartItemDto;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.service.CartItemService;
import com.example.eventinformationsystembackend.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartItemController {

    private final CartItemService cartItemService;
    private final OrderService orderService;

    @GetMapping("/coupon/{username}")
    public Double applyCoupon(@PathVariable String username,
                              @RequestParam(value = "couponCode") String couponCode) {
        return orderService.getCouponDiscount(username, couponCode);
    }

    @GetMapping("/number/{username}")
    public Integer getAllCartItemsNumberForUser(
            @PathVariable String username) {
        return cartItemService.getAllCartItemsNumberForUser(username);
    }

    @GetMapping("/{username}")
    public List<CartItemDtoResponse> getAllCartItemsForUser(
            @PathVariable String username) {
        return cartItemService.getAllCartItemsForUser(username);
    }

    @PostMapping
    public CartItemDtoResponse addCartItem(@RequestBody @Valid CartItemDto cartItemDto) {
        return cartItemService.addCartItem(cartItemDto);
    }

    @PostMapping("/decrease")
    public ResponseEntity<Void> decreaseCartItemTicketQuantity(@RequestBody @Valid CartItemDto cartItemDto) {
        cartItemService.decreaseCartItemTicketQuantity(cartItemDto);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable("id") Long id) {
        cartItemService.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }
}