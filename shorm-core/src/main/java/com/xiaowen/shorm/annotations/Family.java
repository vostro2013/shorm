package com.xiaowen.shorm.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The annotation of the family
 *
 * @author: wenc.hao
 * @date: 2018/3/5 18:18
 * @since: v2.0.0
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Family {
    /**
     * The name of the family.
     */
    String name() default "f";

    /**
     *
     */
    String compression() default "";

    /**
     *
     */
    String blockCache() default "";

    /**
     *
     */
    String blockSize() default "";

    /**
     *
     */
    String bloomFilter() default "";

    /**
     *
     */
    String maxVersions() default "";

    /**
     *
     */
    String timeToLive() default "";

    /**
     *
     */
    String inMemory() default "";
}
