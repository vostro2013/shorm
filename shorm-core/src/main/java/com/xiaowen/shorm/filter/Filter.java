package com.xiaowen.shorm.filter;

import com.xiaowen.shorm.persistency.Persistent;
import org.apache.hadoop.io.Writable;

/**
 * Defines filtering (possibly including modification) of rows. By default
 * all filtering is done client side.
 *
 * @author: wenc.hao
 * @date: 2018/2/26 8:43
 * @since: v2.0.0
 */
public interface Filter<K, T extends Persistent> extends Writable {

    /**
     * Filter the key and persistent. Modification is possible.
     *
     * @param key
     * @param persistent
     * @return <code>true</code> if the row is filtered out (excluded),
     * <code>false</code> otherwise.
     */
    boolean filter(K key, T persistent);
}

