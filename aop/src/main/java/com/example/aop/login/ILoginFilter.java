package com.example.aop.login;

/**
 * Create by lxx
 * Date : 2021/11/8 14:51
 * Use by 登录相关回调接口，
 */
public interface ILoginFilter {

    /**
     * 登录逻辑
     * @param statue
     */
    void login(int statue);

    /**
     * 判断是否登录
     * @return
     */
    boolean isLogin();

}
