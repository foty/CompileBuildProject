package com.example.aop.login;

/**
 * Create by lxx
 * Date : 2021/11/8 14:55
 * Use by 登录辅助类
 */
public class LoginHelper {
    private LoginHelper() {
    }

    private static LoginHelper instance;

    public static LoginHelper getInstance() {
        if (instance == null) {
            synchronized (LoginHelper.class) {
                if (null == instance) {
                    instance = new LoginHelper();
                }
            }
        }
        return instance;
    }

    private ILoginFilter iLoginFilter;

    public ILoginFilter getILoginFilter() {
        return iLoginFilter;
    }

    public void setILoginFilter(ILoginFilter iLoginFilter) {
        this.iLoginFilter = iLoginFilter;
    }

}