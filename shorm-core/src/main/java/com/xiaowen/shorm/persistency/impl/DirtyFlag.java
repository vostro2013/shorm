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

import com.xiaowen.shorm.persistency.Dirtyable;

/**
 * The dirty flag.
 *
 * @author: wenc.hao
 * @date: 2018/2/12 16:30
 * @since: v2.0.0
 */
final class DirtyFlag implements Dirtyable {

    private boolean dirty;

    public DirtyFlag(boolean dirty) {
        this.dirty = dirty;
    }

    public DirtyFlag() {
        this.dirty = false;
    }

    @Override
    public boolean isDirty() {
        return dirty;
    }

    @Override
    public void clearDirty() {
        this.dirty = false;
    }

    /**
     * Set this DirtyFlag to dirty if the <tt>dirty</tt> operand is true. If
     * not, the state of the flag remains unchanged.
     *
     * @param dirty Weather or not to set this flag to dirty. If false, the state
     *              is unchanged.
     */
    public void makeDirty(boolean dirty) {
        this.dirty = this.dirty || dirty;
    }

}
