package com.leyou.cart.service;

import com.leyou.cart.dto.CartDTO;
import com.leyou.common.auth.entity.Payload;
import com.leyou.common.auth.entity.UserInfo;
import com.leyou.common.auth.utils.JwtUtils;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exceptions.LyException;
import com.leyou.common.threadlocals.UserHolder;
import com.leyou.common.utils.CookieUtils;
import com.leyou.common.utils.JsonUtils;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private StringRedisTemplate redisTemplate;
    String PRE_FIX = "ly:cart:uid:";
    /**
     * 添加购物车
     * @param cartDTO
     * @return
     */
    public void saveCart(CartDTO cartDTO) {

        try{
//            String token = CookieUtils.getCookieValue(request, "LY_TOKEN");
////        获取用户ID，使用token
//            Payload<UserInfo> payload = JwtUtils.getInfoFromToken(token, UserInfo.class);
////        获取用户id
//            Long userId = payload.getUserInfo().getId();

//            从当前线程的threadlocal中获取用户id
            Long userId = UserHolder.getUser();
            String redisKey = PRE_FIX + userId;
            String hashKey = cartDTO.getSkuId().toString();
//        判断当前sku是否在redis中
            BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);

            String cartJson = boundHashOps.get(hashKey);
//        如果有数量需要叠加
            if(!StringUtils.isBlank(cartJson)){
//            用户传递的商品数量
                CartDTO cacheCart = JsonUtils.toBean(cartJson, CartDTO.class);
                Integer num = cacheCart.getNum();
                cartDTO.setNum(cartDTO.getNum()+num);
            }
//        如果没有直接保存
            boundHashOps.put(hashKey,JsonUtils.toString(cartDTO));
        }catch(Exception e){
            e.printStackTrace();
            throw new LyException(ExceptionEnum.UNAUTHORIZED);
        }
    }

    /**
     * 查询购物车数据
     **/
    public List<CartDTO> findCartList() {
//        获取用户id
        Long userId = UserHolder.getUser();
//        查询redis
        String redisKey = PRE_FIX + userId;
//        判断当前用户是否有购物车数据
        Boolean b = redisTemplate.hasKey(redisKey);
        if(b == null || !b){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
//        获取全部的购物车数据
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);
        List<String> cartJsons = boundHashOps.values();
        if(CollectionUtils.isEmpty(cartJsons)){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        List<CartDTO> cartDTOList = cartJsons.stream().map(str -> {
            return JsonUtils.toBean(str, CartDTO.class);
        }).collect(Collectors.toList());
        return cartDTOList;
    }

    /**
     * 修改购物车数量
     * @param skuId
     * @param num
     * @return
     */
    public void updateNum(Long skuId, Integer num) {
//        获取用户id
        Long userId = UserHolder.getUser();
        String redisKey = PRE_FIX + userId;
        String hashKey = skuId.toString();
//        获取skuid的购物车数据
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);
        String cartJson = boundHashOps.get(hashKey);
        if(StringUtils.isBlank(cartJson)){
            throw new LyException(ExceptionEnum.CARTS_NOT_FOUND);
        }
        CartDTO cartDTO = JsonUtils.toBean(cartJson, CartDTO.class);
//        覆盖数量
        cartDTO.setNum(num);
//        回写到 redis中
        boundHashOps.put(hashKey,JsonUtils.toString(cartDTO));
    }

    /**
     * 删除购物车
     * @param skuId
     * @return
     */
    public void deleteCart(Long skuId) {
        Long userId = UserHolder.getUser();
        String redisKey = PRE_FIX + userId;
        String hashKey = skuId.toString();
        redisTemplate.opsForHash().delete(redisKey,hashKey);
    }

    /**
     * 批量保存
     * @param cartDTOS
     * @return
     */
    public void batchAdd(List<CartDTO> cartDTOS) {
//        获取用户id
        Long userId = UserHolder.getUser();
//        redisKey
        String redisKey = PRE_FIX +userId;
        BoundHashOperations<String, String, String> boundHashOps = redisTemplate.boundHashOps(redisKey);
        List<String> cartJsons = boundHashOps.values();
//       获取redis中所有的购物车数据
        List<CartDTO> cartDTOList = cartJsons.stream().map(str -> {
            return JsonUtils.toBean(str, CartDTO.class);
        }).collect(Collectors.toList());
//        Map<Long, List<CartDTO>> skuIdCartMap = cartDTOList.stream().collect(Collectors.groupingBy(CartDTO::getSkuId));
        for (CartDTO cartDTO : cartDTOS) {
            for (CartDTO cacheCart : cartDTOList) {
                if(cartDTO.getSkuId().longValue() == cacheCart.getSkuId().longValue()){
//                    合并数量
                    cartDTO.setNum(cartDTO.getNum() + cacheCart.getNum());
                }
            }

            boundHashOps.put(cartDTO.getSkuId().toString(),JsonUtils.toString(cartDTO));
        }
    }
}
