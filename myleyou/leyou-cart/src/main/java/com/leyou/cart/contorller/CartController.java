package com.leyou.cart.contorller;

import com.leyou.cart.pojo.Cart;
import com.leyou.cart.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;

    /**
     * 添加购物车
     * @param cart
     * @return
     */

   /* @PostMapping
    public ResponseEntity<Void> addCart(@RequestBody Cart cart){
        this.cartService.addCart(cart);
        return ResponseEntity.ok().build();
    }*/

    @PostMapping
    public ResponseEntity<Void> addCartList(@RequestBody List<Cart> carts){

        if(!CollectionUtils.isEmpty(carts)){
            for (Cart cart:carts){
                this.cartService.addCart(cart);
            }

        }
        return ResponseEntity.ok().build();
    }


    /**
     * 查询购物车列表
     * @return
     */
    @GetMapping
    public ResponseEntity<List<Cart>> queryCartList(){
        List<Cart> carts = this.cartService.queryCatrList();
        if(CollectionUtils.isEmpty(carts)){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        return ResponseEntity.ok(carts);
    }

    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestBody Cart cart){
        this.cartService.updateCarts(cart);
        //返回204
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("{skuId}")
    public ResponseEntity<Void> deleteCart(@PathVariable("skuId") String skuId){
        this.cartService.deleteCart(skuId);
        return ResponseEntity.ok().build();
    }

}
