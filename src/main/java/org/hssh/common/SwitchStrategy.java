package org.hssh.common;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * Created by hssh on 2017/2/17.
 */
@Aspect
public class SwitchStrategy {

    @Pointcut("@annotation(org.hssh.common.DataSourceName)")
    public void point(){}


    @Before("point()")
    public void before(JoinPoint jp) {
        Signature signature = jp.getSignature();
        Method method = ((MethodSignature)signature).getMethod();
        DataSourceName annotation = method.getAnnotation(DataSourceName.class);
        ContextHolder.setHolder(annotation.value());
    }

}
