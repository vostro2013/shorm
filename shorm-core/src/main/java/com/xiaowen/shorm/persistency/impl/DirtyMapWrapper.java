/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.xiaowen.shorm.persistency.impl;

import com.google.common.base.Function;
import com.google.common.collect.Collections2;
import com.xiaowen.shorm.persistency.Dirtyable;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * A {@link Map} implementation that wraps another map, intercepting
 * modifications to the map structure and reporting on weather or not the map
 * has been modified, and also checking map elements for modification.
 *
 * @param <K> The key of the map that this wrapper wraps.
 * @param <V> The value of the map that this wrapper wraps.
 * @author: wenc.hao
 * @date: 2018/2/12 16:30
 * @since: v2.0.0
 */
public class DirtyMapWrapper<K, V> implements Dirtyable, Map<K, V> {

    public static class DirtyEntryWrapper<K, V> implements Entry<K, V>, Dirtyable {
        private final Entry<K, V> entryDelegate;
        private DirtyFlag dirtyFlag;

        public DirtyEntryWrapper(Entry<K, V> delegate, DirtyFlag dirtyFlag) {
            this.entryDelegate = delegate;
            this.dirtyFlag = dirtyFlag;
        }

        public K getKey() {
            return entryDelegate.getKey();
        }

        public V getValue() {
            return entryDelegate.getValue();
        }

        public V setValue(V value) {
            dirtyFlag.makeDirty(valueChanged(value, entryDelegate.getValue()));
            return entryDelegate.setValue(value);
        }

        @Override
        public boolean equals(Object o) {
            return entryDelegate.equals(o);
        }

        @Override
        public int hashCode() {
            return entryDelegate.hashCode();
        }

        public boolean isDirty() {
            return dirtyFlag.isDirty() || (entryDelegate instanceof Dirtyable) ?
                    ((Dirtyable) entryDelegate.getValue()).isDirty() : false;
        }

        public void clearDirty() {
            dirtyFlag.clearDirty();
        }
    }

    private final Map<K, V> delegate;

    private final DirtyFlag dirtyFlag;

    public DirtyMapWrapper(Map<K, V> delegate) {
        this(delegate, new DirtyFlag());
    }

    DirtyMapWrapper(Map<K, V> delegate, DirtyFlag dirtyFlag) {
        this.dirtyFlag = dirtyFlag;
        this.delegate = delegate;
    }

    public boolean isDirty() {
        boolean anyDirty = false;
        for (V v : this.values()) {
            anyDirty = anyDirty || (v instanceof Dirtyable) ? ((Dirtyable) v).isDirty() : false;
        }
        return anyDirty || dirtyFlag.isDirty();
    }

    public void clearDirty() {
        for (V v : this.values()) {
            if (v instanceof Dirtyable) {
                ((Dirtyable) v).clearDirty();
            }
        }
        dirtyFlag.clearDirty();
    }

    public int size() {
        return delegate.size();
    }

    public boolean isEmpty() {
        return delegate.isEmpty();
    }

    public boolean containsKey(Object key) {
        return delegate.containsKey(key);
    }

    public boolean containsValue(Object value) {
        return delegate.containsValue(value);
    }

    public V get(Object key) {
        return delegate.get(key);
    }

    public V put(K key, V value) {
        checkPutWillMakeDirty(key, value);
        return delegate.put(key, value);
    }

    private void checkPutWillMakeDirty(K key, V value) {
        if (containsKey(key)) {
            dirtyFlag.makeDirty(valueChanged(value, get(key)));
        } else {
            dirtyFlag.makeDirty(true);
        }
    }

    private static <V> boolean valueChanged(V value, V oldValue) {
        return (value == null && oldValue != null)
                || (value != null && !value.equals(oldValue));
    }

    public V remove(Object key) {
        dirtyFlag.makeDirty(containsKey(key));
        return delegate.remove(key);
    }

    public void putAll(Map<? extends K, ? extends V> m) {
        for (Entry<? extends K, ? extends V> entry : m.entrySet()) {
            checkPutWillMakeDirty(entry.getKey(), entry.getValue());
        }
        delegate.putAll(m);
    }

    public void clear() {
        if (delegate.size() != 0) {
            dirtyFlag.makeDirty(true);
        }
        delegate.clear();
    }

    public Set<K> keySet() {
        return delegate.keySet();
    }

    public Collection<V> values() {
        return new DirtyCollectionWrapper<>(delegate.values(), dirtyFlag);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set<Entry<K, V>> entrySet() {
        Collection<DirtyEntryWrapper<K, V>> dirtyEntrySet = Collections2.transform(
                delegate.entrySet(),
                new Function<Entry<K, V>, DirtyEntryWrapper<K, V>>() {
                    @Override
                    public DirtyEntryWrapper<K, V> apply(java.util.Map.Entry<K, V> input) {
                        return new DirtyEntryWrapper<>(input, dirtyFlag);
                    }
                });
        return new DirtySetWrapper(dirtyEntrySet, dirtyFlag);
    }

    @Override
    public boolean equals(Object o) {
        return delegate.equals(o);
    }

    @Override
    public int hashCode() {
        return delegate.hashCode();
    }

}
