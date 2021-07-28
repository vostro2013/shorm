package com.xiaowen.shorm.util;

import com.xiaowen.shorm.filter.Filter;
import com.xiaowen.shorm.mapper.HBaseMapping;
import com.xiaowen.shorm.persistency.impl.PersistentBase;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Scan;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An utility class for Hbase filter.
 *
 * @author: wenc.hao
 * @date: 2018/2/26 15:50
 * @since: v2.0.0
 */
public class HBaseFilterUtil<K, T extends PersistentBase> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseFilterUtil.class);

    private Map<String, FilterFactory<K, T>> factories = new LinkedHashMap<>();

    public HBaseFilterUtil() {
        FilterFactory<K, T> filterFactory = new DefaultFilterFactory<>();
        filterFactory.setHBaseFilterUtil(this);
    }

    public HBaseFilterUtil(Configuration conf) throws RuntimeException {
        String[] factoryClassNames = conf.getStrings("hbase.filter.factories",
                "com.sunyard.dip.core.hbase.util.DefaultFilterFactory");
        for (String factoryClass : factoryClassNames) {
            try {
                @SuppressWarnings("unchecked")
                FilterFactory<K, T> factory = (FilterFactory<K, T>) ReflectionUtils.newInstance(factoryClass);
                for (String filterClass : factory.getSupportedFilters()) {
                    factories.put(filterClass, factory);
                }
                factory.setHBaseFilterUtil(this);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    public FilterFactory<K, T> getFactory(Filter<K, T> filter) {
        return factories.get(filter.getClass().getCanonicalName());
    }

    /**
     * Set a filter on the Scan. It translates a filter to a HBase filter.
     *
     * @param scan
     * @param filter   The filter.
     * @param mapping The HBaseMapping.
     * @return if remote filter is successfully applied.
     */
    public boolean setFilter(Scan scan, Filter<K, T> filter, HBaseMapping mapping) {
        FilterFactory<K, T> factory = getFactory(filter);
        if (factory != null) {
            org.apache.hadoop.hbase.filter.Filter hbaseFilter = factory.createFilter(filter, mapping);
            if (hbaseFilter != null) {
                scan.setFilter(hbaseFilter);
                return true;
            } else {
                LOGGER.warn("HBase remote filter not yet implemented for " + filter.getClass().getCanonicalName());
                return false;
            }
        } else {
            LOGGER.warn("HBase remote filter factory not yet implemented for " + filter.getClass().getCanonicalName());
            return false;
        }
    }
}
