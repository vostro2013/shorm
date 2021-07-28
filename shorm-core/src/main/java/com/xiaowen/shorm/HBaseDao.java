package com.xiaowen.shorm;

import com.xiaowen.shorm.persistency.Persistent;
import com.xiaowen.shorm.query.HBaseQuery;

import java.util.List;

/**
 * 泛化的 HBase Dao 基础接口
 * @param <K> key
 * @param <T> Persistent
 *
 * @author: wenc.hao
 * @date: 2018/2/12 16:30
 * @since: v2.0.0
 */
public interface HBaseDao<K, T extends Persistent> {
    /**
     * Returns the object corresponding to the given key fetching all the fields.
     *
     * @param key the key of the object
     * @return the Object corresponding to the key or null if it cannot be found
     */
    T get(K key);

    /**
     * Returns the object corresponding to the given key.
     *
     * @param key    the key of the object
     * @param fields the fields required in the object. Pass null, to retrieve all fields
     * @return the Object corresponding to the key or null if it cannot be found
     */
    T get(K key, final String[] fields);

    /**
     * Inserts the persistent object with the given key. If an
     * object with the same key already exists it will silently
     * be replaced. See also the note on
     * <a href="#visibility">visibility</a>.
     */
    void put(final K key, final T obj);

    /**
     * Deletes the object with the given key
     *
     * @param key the key of the object
     * @return whether the object was successfully deleted
     */
    boolean delete(final K key);

    /**
     * Deletes all the objects matching the query.
     * See also the note on <a href="#visibility">visibility</a>.
     *
     * @param query matching records to this query will be deleted
     * @return number of deleted records
     */
    long deleteByQuery(HBaseQuery<K, T> query);

    /**
     * Executes the given query and returns the results.
     *
     * @param query the query to execute.
     * @return .
     */
    List<T> findByQuery(HBaseQuery<K, T> query);

    /**
     * returns the results.
     *
     * @return .
     */
    List<T> findAll();
}
