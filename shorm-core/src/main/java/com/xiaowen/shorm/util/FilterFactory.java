package com.xiaowen.shorm.util;

import com.xiaowen.shorm.filter.Filter;
import com.xiaowen.shorm.mapper.HBaseMapping;
import com.xiaowen.shorm.persistency.impl.PersistentBase;

import java.util.List;

/**
 * Filter factory.
 *
 * @author: wenc.hao
 * @date: 2018/2/26 15:51
 * @since: v2.0.0
 */
public interface FilterFactory<K, T extends PersistentBase> {
    /**
     *  Set the HBase filter util.
     * @param util
     */
    void setHBaseFilterUtil(HBaseFilterUtil<K, T> util);

    /**
     * Get the HBase filter util.
     * @return
     */
    HBaseFilterUtil<K, T> getHBaseFilterUtil();

    /**
     * Get the supported filters.
     * @return
     */
    List<String> getSupportedFilters();

    /**
     * Create the HBase filter.
     *
     * @param filter
     * @param mapping
     * @return
     */
    org.apache.hadoop.hbase.filter.Filter createFilter(Filter<K, T> filter, HBaseMapping mapping);
}
