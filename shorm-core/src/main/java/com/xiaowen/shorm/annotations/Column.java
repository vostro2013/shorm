package com.xiaowen.shorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation of the column
 *
 * @author: wenc.hao
 * @date: 2018/2/8 18:45
 * @since: v2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * The name of the column.
     */
    String name() default "";

    /**
     * The family of the column.
     */
    String family() default "";

    /**
     * The qualifier of the column.
     */
    String qualifier() default "";
}
