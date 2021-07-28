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

import com.xiaowen.shorm.persistency.BeanFactory;
import com.xiaowen.shorm.persistency.Persistent;
import com.xiaowen.shorm.util.ReflectionUtils;

import java.lang.reflect.Constructor;

/**
 * A default implementation of the {@link BeanFactory} interface. Constructs
 * the keys using by reflection, {@link Persistent} objects by calling
 *
 * @author: wenc.hao
 * @date: 2018/2/12 16:30
 * @since: v2.0.0
 */
public class BeanFactoryImpl<K, T extends Persistent> implements BeanFactory<K, T> {

    /**
     * Class of the key to be used
     */
    private Class<K> keyClass;

    /**
     * Class of the persistent objects to be stored
     */
    private Class<T> persistentClass;

    /**
     * Constructor of the key
     */
    private Constructor<K> keyConstructor;

    /**
     * Object's key
     */
    private K key;

    /**
     * Persistent object of class T
     */
    private T persistent;

    /**
     * Flag to be used to determine if a key is persistent or not
     */
    private boolean isKeyPersistent = false;

    /**
     * Default constructor for this class.
     *
     * @param keyClass
     * @param persistentClass
     */
    public BeanFactoryImpl(Class<K> keyClass, Class<T> persistentClass) {
        this.keyClass = keyClass;
        this.persistentClass = persistentClass;

        try {
            if (ReflectionUtils.hasConstructor(keyClass)) {
                this.keyConstructor = ReflectionUtils.getConstructor(keyClass);
                this.key = keyConstructor.newInstance(ReflectionUtils.EMPTY_OBJECT_ARRAY);
            }
            this.persistent = ReflectionUtils.newInstance(persistentClass);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        isKeyPersistent = Persistent.class.isAssignableFrom(keyClass);
    }

    @Override
    public K newKey() throws Exception {
        return keyClass.newInstance();
    }

    @Override
    public T newPersistent() {
        return (T) persistent.newInstance();
    }

    @Override
    public K getCachedKey() {
        return key;
    }

    @Override
    public T getCachedPersistent() {
        return persistent;
    }

    @Override
    public Class<K> getKeyClass() {
        return keyClass;
    }

    @Override
    public Class<T> getPersistentClass() {
        return persistentClass;
    }

    public boolean isKeyPersistent() {
        return isKeyPersistent;
    }
}
