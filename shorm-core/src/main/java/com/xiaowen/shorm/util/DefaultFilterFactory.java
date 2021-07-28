/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaowen.shorm.util;

import com.xiaowen.shorm.filter.Filter;
import com.xiaowen.shorm.filter.FilterList;
import com.xiaowen.shorm.filter.FilterOp;
import com.xiaowen.shorm.filter.MapFieldValueFilter;
import com.xiaowen.shorm.filter.SingleFieldValueFilter;
import com.xiaowen.shorm.mapper.HBaseColumn;
import com.xiaowen.shorm.mapper.HBaseMapping;
import com.xiaowen.shorm.persistency.impl.PersistentBase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.FilterList.Operator;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;

import java.util.ArrayList;
import java.util.List;

/**
 * The default filter factory.
 *
 * @author: wenc.hao
 * @date: 2018/3/7 11:27
 * @since: v2.0.0
 */
public class DefaultFilterFactory<K, T extends PersistentBase> extends BaseFilterFactory<K, T> {
    private static final Log LOG = LogFactory.getLog(DefaultFilterFactory.class);

    public List<String> getSupportedFilters() {
        List<String> filters = new ArrayList<>();
        filters.add(SingleFieldValueFilter.class.getCanonicalName());
        filters.add(MapFieldValueFilter.class.getCanonicalName());
        filters.add(FilterList.class.getCanonicalName());
        return filters;
    }

    public org.apache.hadoop.hbase.filter.Filter createFilter(Filter<K, T> filter, HBaseMapping mapping) {
        if (filter instanceof FilterList) {
            FilterList<K, T> filterList = (FilterList<K, T>) filter;
            org.apache.hadoop.hbase.filter.FilterList hbaseFilter = new org.apache.hadoop.hbase.filter.FilterList(
                    Operator.valueOf(filterList.getOperator().name()));
            for (Filter<K, T> rowFilter : filterList.getFilters()) {
                FilterFactory<K, T> factory = getHBaseFilterUtil().getFactory(rowFilter);
                if (factory == null) {
                    LOG.warn("HBase remote filter factory not yet implemented for " + rowFilter.getClass().getCanonicalName());
                    return null;
                }
                org.apache.hadoop.hbase.filter.Filter hbaseRowFilter = factory.createFilter(rowFilter, mapping);
                if (hbaseRowFilter != null) {
                    hbaseFilter.addFilter(hbaseRowFilter);
                }
            }
            return hbaseFilter;
        } else if (filter instanceof SingleFieldValueFilter) {
            SingleFieldValueFilter<K, T> fieldFilter = (SingleFieldValueFilter<K, T>) filter;
            HBaseColumn column = mapping.getColumn(fieldFilter.getFieldName());
            CompareOp compareOp = getCompareOp(fieldFilter.getFilterOp());
            byte[] family = column.getFamily();
            byte[] qualifier = column.getQualifier();
            byte[] value = HBaseByteInterface.toBytes(fieldFilter.getOperands().get(0));
            SingleColumnValueFilter hbaseFilter = new SingleColumnValueFilter(family, qualifier, compareOp, value);
            hbaseFilter.setFilterIfMissing(fieldFilter.isFilterIfMissing());
            return hbaseFilter;
        } else if (filter instanceof MapFieldValueFilter) {
            MapFieldValueFilter<K, T> mapFilter = (MapFieldValueFilter<K, T>) filter;
            HBaseColumn column = mapping.getColumn(mapFilter.getFieldName());
            CompareOp compareOp = getCompareOp(mapFilter.getFilterOp());
            byte[] family = column.getFamily();
            byte[] qualifier = HBaseByteInterface.toBytes(mapFilter.getMapKey());
            byte[] value = HBaseByteInterface.toBytes(mapFilter.getOperands().get(0));
            SingleColumnValueFilter hbaseFilter = new SingleColumnValueFilter(family, qualifier, compareOp, value);
            hbaseFilter.setFilterIfMissing(mapFilter.isFilterIfMissing());
            return hbaseFilter;
        } else {
            LOG.warn("HBase remote filter not yet implemented for " + filter.getClass().getCanonicalName());
            return null;
        }
    }

    private CompareOp getCompareOp(FilterOp filterOp) {
        switch (filterOp) {
            case EQUALS:
                return CompareOp.EQUAL;
            case NOT_EQUALS:
                return CompareOp.NOT_EQUAL;
            case LESS:
                return CompareOp.LESS;
            case LESS_OR_EQUAL:
                return CompareOp.LESS_OR_EQUAL;
            case GREATER:
                return CompareOp.GREATER;
            case GREATER_OR_EQUAL:
                return CompareOp.GREATER_OR_EQUAL;
            default:
                throw new IllegalArgumentException(filterOp + " no HBase equivalent yet");
        }
    }
}