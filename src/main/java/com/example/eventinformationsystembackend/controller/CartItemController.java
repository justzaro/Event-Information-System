package com.example.eventinformationsystembackend.controller;

import com.example.eventinformationsystembackend.dto.CartItemDto;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.model.CartItem;
import com.example.eventinformationsystembackend.service.CartItemService;
import com.example.eventinformationsystembackend.service.OrderService;
import jakarta.validation.Valid;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping(path = "/cart")
public class CartItemController {

    private final CartItemService cartItemService;

    @Autowired
    public CartItemController(CartItemService cartItemService) {
        this.cartItemService = cartItemService;
    }

/*    @GetMapping(path = "/coupon/{username}")
    public Double applyCoupon(@PathVariable("username") String username,
                              @RequestParam("couponCode") String couponCode) {
        return orderService.getCartItemsTotalPriceWithCoupon(username, couponCode);
    }*/

    @GetMapping("/{username}")
    public List<CartItemDtoResponse> getAllCartItemsForUser(
            @PathVariable("username") String username) {
        return cartItemService.getAllCartItemsForUser(username);
    }

    @PostMapping
    public CartItemDtoResponse addCartItem(@RequestBody @Valid CartItemDto cartItemDto) {
        return cartItemService.addCartItem(cartItemDto);
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> removeCartItem(@PathVariable("id") Long id) {
        cartItemService.removeCartItem(id);
        return ResponseEntity.noContent().build();
    }
}