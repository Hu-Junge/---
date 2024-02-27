package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
* 自定义注解，为某个方法标识公共字段填充处理
*/
@Target(ElementType.METHOD) // 该注解添加在方法上面
@Retention(RetentionPolicy.RUNTIME)
public @interface AutoFill {
    // 指定数据库操作类型:UPDATE、INSERT
    OperationType value();
}
