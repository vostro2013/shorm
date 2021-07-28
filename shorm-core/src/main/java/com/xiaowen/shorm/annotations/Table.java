package com.xiaowen.shorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation of the table
 *
 * @author: wenc.hao
 * @date: 2018/2/8 18:40
 * @since: v2.0.0
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Table {
    /**
     * The name of the table.
     *
     * Defaults to the entity name.
     */
    String name() default "";

    /**
     * The rowkey's name of the table.
     */
    String key() default "id";

    /**
     * The rowkey's class type of the table.
     */
    Class<?> keyClass() default String.class;
}
