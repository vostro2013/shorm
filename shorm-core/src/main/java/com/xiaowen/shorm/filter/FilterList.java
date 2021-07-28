package com.xiaowen.shorm.filter;

import com.xiaowen.shorm.persistency.impl.PersistentBase;
import com.xiaowen.shorm.util.ReflectionUtils;
import org.apache.hadoop.io.Text;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author: wenc.hao
 * @date: 2018/2/26 8:44
 * @since: v2.0.0
 */
public class FilterList<K, T extends PersistentBase> implements Filter<K, T> {
    /**
     * set operator
     */
    public static enum Operator {
        /**
         * !AND
         */
        MUST_PASS_ALL,
        /**
         * !OR
         */
        MUST_PASS_ONE
    }

    private Operator operator = Operator.MUST_PASS_ALL;
    private List<Filter<K, T>> filters = new ArrayList<>();

    public FilterList() {
    }

    public FilterList(final List<Filter<K, T>> rowFilters) {
        this.filters = rowFilters;
    }

    public FilterList(final Operator operator) {
        this.operator = operator;
    }

    public FilterList(final Operator operator, final List<Filter<K, T>> rowFilters) {
        this.filters = rowFilters;
        this.operator = operator;
    }

    public List<Filter<K, T>> getFilters() {
        return filters;
    }

    public Operator getOperator() {
        return operator;
    }

    public void addFilter(Filter<K, T> filter) {
        this.filters.add(filter);
    }

    @Override
    public void readFields(DataInput in) throws IOException {
        byte opByte = in.readByte();
        operator = Operator.values()[opByte];
        int size = in.readInt();
        if (size > 0) {
            filters = new ArrayList<>(size);
            try {
                for (int i = 0; i < size; i++) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Filter<K, T>> cls = (Class<? extends Filter<K, T>>) Class.forName(Text.readString(in)).asSubclass(Filter.class);
                    Filter<K, T> filter = ReflectionUtils.newInstance(cls);
                    filter.readFields(in);
                    filters.add(filter);
                }
            } catch (Exception e) {
                throw (IOException) new IOException("Failed filter init").initCause(e);
            }
        }
    }

    @Override
    public void write(DataOutput out) throws IOException {
        out.writeByte(operator.ordinal());
        out.writeInt(filters.size());
        for (Filter<K, T> filter : filters) {
            Text.writeString(out, filter.getClass().getName());
            filter.write(out);
        }
    }

    @Override
    public boolean filter(K key, T persistent) {
        // TODO not yet implemented
        return false;
    }
}
