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

import java.util.Iterator;

/**
 * Sets the dirty flag if the iterator's remove method is called.
 *
 * @author: wenc.hao
 * @date: 2018/2/12 16:30
 * @since: v2.0.0
 */
final class DirtyIteratorWrapper<T> implements Iterator<T> {

    private final DirtyFlag dirtyFlag;
    private Iterator<T> delegateIterator;

    DirtyIteratorWrapper(Iterator<T> delegateIterator, DirtyFlag dirtyFlag) {
        this.delegateIterator = delegateIterator;
        this.dirtyFlag = dirtyFlag;
    }

    @Override
    public boolean hasNext() {
        return delegateIterator.hasNext();
    }

    @Override
    public T next() {
        return delegateIterator.next();
    }

    @Override
    public void remove() {
        dirtyFlag.makeDirty(true);
        delegateIterator.remove();
    }
}