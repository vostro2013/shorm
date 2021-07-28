package com.xiaowen.shorm.query;

import com.xiaowen.shorm.filter.Filter;
import com.xiaowen.shorm.persistency.Persistent;

import java.util.Arrays;

/**
 * HBase Query.
 *
 * @author: wenc.hao
 * @date: 2018/2/26 9:48
 * @since: v2.0.0
 */
public class HBaseQuery<K, T extends Persistent> {
    /**
     * The fields need to query.
     */
    protected String[] fields;

    /**
     * The start key.
     */
    protected K startKey;

    /**
     * The end key.
     */
    protected K endKey;

    /**
     * The start timestamp.
     */
    protected long startTime = -1;

    /**
     * The end timestamp.
     */
    protected long endTime = -1;

    protected Filter<K, T> filter;

    protected boolean localFilterEnabled = true;

    protected long limit = -1;

    public void setFields(String... fields) {
        this.fields = fields;
    }

    public String[] getFields() {
        return fields;
    }

    public void setKey(K key) {
        setKeyRange(key, key);
    }

    public void setStartKey(K startKey) {
        this.startKey = startKey;
    }

    public void setEndKey(K endKey) {
        this.endKey = endKey;
    }

    public void setKeyRange(K startKey, K endKey) {
        this.startKey = startKey;
        this.endKey = endKey;
    }

    public K getKey() {
        if (startKey == endKey) {
            return startKey; //address comparison
        }
        return null;
    }

    public K getStartKey() {
        return startKey;
    }

    public K getEndKey() {
        return endKey;
    }

    public void setTimestamp(long timestamp) {
        setTimeRange(timestamp, timestamp);
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public void setTimeRange(long startTime, long endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public long getTimestamp() {
        return startTime == endTime ? startTime : -1;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setLimit(long limit) {
        this.limit = limit;
    }

    public long getLimit() {
        return limit;
    }

    public Filter<K, T> getFilter() {
        return filter;
    }

    public void setFilter(Filter<K, T> filter) {
        this.filter = filter;
    }

    public boolean isLocalFilterEnabled() {
        return localFilterEnabled;
    }

    public void setLocalFilterEnabled(boolean enable) {
        this.localFilterEnabled = enable;
    }

    @Override
    public String toString() {
        return "HBaseQuery{" +
                "fields=" + Arrays.toString(fields) +
                ", startKey=" + startKey +
                ", endKey=" + endKey +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", filter=" + filter +
                ", localFilterEnabled=" + localFilterEnabled +
                ", limit=" + limit +
                '}';
    }
}
