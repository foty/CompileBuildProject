package com.example.aop;

import android.util.Log;
import android.view.View;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

@Aspect
public class OneClickAspect {

    @Pointcut("execution(@com.example.aop.OneClick * *(..))")
    public void methodAnnotated() {
    }

    @Around("methodAnnotated()")
    public void aroundJoinPoint(ProceedingJoinPoint joinPoint) throws Throwable {
        // 每次点击都会执行这个
        // 判断是哪一个View的点击，故切入方法需要传入一个view参数
        View view = null;
        for (Object arg : joinPoint.getArgs()) {
            Log.d("lxx", "arg= " + arg);
            if (arg instanceof View) {
                view = (View) arg;
                break;
            }
        }
        if (view == null) {
            Log.e("lxx", "view == null");
            return;
        }

        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        // A.isAnnotationPresent(B)：B的注解是否在A上，或者A是否有B注解
        if (!method.isAnnotationPresent(OneClick.class)) {
            Log.e("lxx", "没有找到相关注解");
            return;
        }

        OneClick singleClick = method.getAnnotation(OneClick.class);
        if (singleClick != null && !ClickHelper.isFastClick(view, singleClick.value())) {
            joinPoint.proceed();
        }
    }
}