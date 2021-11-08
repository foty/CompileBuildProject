package com.example.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Create by lxx
 * Date : 2021/11/8 14:47
 * Use by 创建注解
 */

@Retention(RetentionPolicy.RUNTIME) // 保留策略，这里保存到class字节码级别
@Target(ElementType.METHOD)  //作用目标，这里是针对方法。
public @interface LoginFilter {
    int loginStatue() default 0;
}
