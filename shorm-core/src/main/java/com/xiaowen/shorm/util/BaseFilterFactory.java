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

import com.xiaowen.shorm.persistency.impl.PersistentBase;

/**
 * Base filter factory.
 *
 * @author: wenc.hao
 * @date: 2018/3/7 11:28
 * @since: v2.0.0
 */
public abstract class BaseFilterFactory<K, T extends PersistentBase> implements FilterFactory<K, T> {

    private HBaseFilterUtil<K, T> util;

    @Override
    public HBaseFilterUtil<K, T> getHBaseFilterUtil() {
        return util;
    }

    @Override
    public void setHBaseFilterUtil(HBaseFilterUtil<K, T> util) {
        this.util = util;
    }
}