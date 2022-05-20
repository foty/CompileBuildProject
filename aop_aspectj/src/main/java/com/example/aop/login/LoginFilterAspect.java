package com.example.aop.login;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MemberSignature;
import org.aspectj.lang.reflect.MethodSignature;

/**
 * Create by lxx
 * Date : 2021/11/8 14:57
 * Use by 登录拦截切面
 */

@Aspect
public class LoginFilterAspect {

    @Pointcut("execution(@com.example.aop.login.LoginFilter * * (..))")
    public void LoginFilter() {
    }

    @Around("LoginFilter()")
    public void handleLoginPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取用户实现的ILogin类，如果没有调用init()设置初始化就抛出异常。
        ILoginFilter iLoginFilter = LoginHelper.getInstance().getILoginFilter();
        if (iLoginFilter == null) throw new RuntimeException("ILoginFilter没有初始化");

        //先得到方法的签名methodSignature，然后得到@LoginFilter注解，如果注解为空，就不再往下走。
        Signature signature = joinPoint.getSignature();
        if (!(signature instanceof MemberSignature)) throw new RuntimeException("该注解只能用于方法上");

        MethodSignature methodSignature = (MethodSignature) signature;
        LoginFilter loginFilter = methodSignature.getMethod().getAnnotation(LoginFilter.class);
        if (loginFilter == null) return;

        //调用iLogin的isLogin()方法判断是否登录
        if (iLoginFilter.isLogin()) { //执行方法
            joinPoint.proceed();
        } else {
            iLoginFilter.login(loginFilter.loginStatue()); // 触发回调
        }
    }
}
