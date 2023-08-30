package com.example.eventinformationsystembackend.repository;

import com.example.eventinformationsystembackend.dto.CartItemDtoResponse;
import com.example.eventinformationsystembackend.dto.CartItemDtoResponseProjection;
import com.example.eventinformationsystembackend.model.CartItem;
import com.example.eventinformationsystembackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Repository
public interface CartItemRepository extends JpaRepository<CartItem, Long> {
        @Query(value = "SELECT ci.cart_item_id as cartItemId, ci.ticket_quantity as ticketQuantity, ci.total_price as totalPrice, e.ticket_price as ticketPrice, e.event_picture_path as eventPicturePath, e.name as eventName\n" +
            "FROM cart_items AS ci\n" +
            "JOIN events_cart_items AS eci\n" +
            "ON ci.cart_item_id = eci.cart_item_id\n" +
            "JOIN events AS e\n\n" +
            "ON eci.event_id = e.event_id\n" +
            "JOIN users_cart_items AS uci\n" +
            "ON ci.cart_item_id = uci.cart_item_id\n" +
            "JOIN users AS u\n" +
            "ON u.user_id = uci.user_id\n" +
            "WHERE u.username = :username\n",
            nativeQuery = true)
        List<CartItemDtoResponseProjection> findAllByUser(@Param("username") String username);

        List<CartItem> findAllByUser(User user);

        @Transactional
        void removeAllByUser(User user);
}
