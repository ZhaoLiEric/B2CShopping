package com.leyou.common.threadlocals;

public class UserHolder {

    private static ThreadLocal<Long> tl = new ThreadLocal<>();

    /**
     * 设置用户id
     * @param userId
     */
    public static void setUser(Long userId){
        tl.set(userId);
    }

    /**
     * 获取用户id
     * @return
     */
    public static Long getUser(){
        return tl.get();
    }

    /**
     * 删除用户信息
     */
    public static void removeUser(){
        tl.remove();
    }
}
