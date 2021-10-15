package com.example.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.CLASS) //保留时间
@Target(ElementType.FIELD)  //作用目标
public @interface FindView {
    int value();
}
