package com.leyou.cart.controller;

import com.leyou.cart.dto.CartDTO;
import com.leyou.cart.service.CartService;
import org.junit.runners.Parameterized;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
public class CartController {

    @Autowired
    private CartService cartService;
    /**
     * 添加购物车
     * @param cartDTO
     * @return
     */
    @PostMapping
    public ResponseEntity<Void> saveCart(@RequestBody CartDTO cartDTO){
        cartService.saveCart(cartDTO);
        return ResponseEntity.noContent().build();
    }

    /**
    * 查询购物车数据
     **/
    @GetMapping("/list")
    public ResponseEntity<List<CartDTO>> findCartList(){
        return ResponseEntity.ok(cartService.findCartList());
    }

    /**
     * 修改购物车数量
     * @param skuId
     * @param num
     * @return
     */
    @PutMapping
    public ResponseEntity<Void> updateNum(@RequestParam(name = "id") Long skuId
            ,@RequestParam(name = "num") Integer num){
        cartService.updateNum(skuId,num);
        return ResponseEntity.noContent().build();
    }

    /**
     * 删除购物车
     * @param skuId
     * @return
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCart(@PathVariable(name = "id")Long skuId){
        cartService.deleteCart(skuId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 批量保存
     * @param cartDTOS
     * @return
     */
    @PostMapping("/list")
    public ResponseEntity<Void> batchAdd(@RequestBody List<CartDTO> cartDTOS){
        cartService.batchAdd(cartDTOS);
        return ResponseEntity.noContent().build();
    }
}
