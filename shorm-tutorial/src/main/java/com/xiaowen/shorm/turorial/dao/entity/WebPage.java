/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the"
 * License"); you may not use this file except in compliance
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
package com.xiaowen.shorm.turorial.dao.entity;

import com.xiaowen.shorm.annotations.Column;
import com.xiaowen.shorm.annotations.Family;
import com.xiaowen.shorm.annotations.Table;
import com.xiaowen.shorm.persistency.Dirtyable;
import com.xiaowen.shorm.persistency.Persistent;
import com.xiaowen.shorm.persistency.impl.DirtyListWrapper;
import com.xiaowen.shorm.persistency.impl.DirtyMapWrapper;
import com.xiaowen.shorm.persistency.impl.PersistentBase;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.data.RecordBuilder;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificRecordBuilderBase;

import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

@Table(name = "web_page", key = "id", keyClass = String.class)
public class WebPage extends PersistentBase implements SpecificRecord, Persistent {
    public static final Schema SCHEMA$ = new Schema.Parser().parse("{\"type\":\"record\",\"name\":\"WebPage\",\"namespace\":\"com.xiaowen.shorm.turorial.entity\",\"fields\":[{\"name\":\"url\",\"type\":[\"null\",\"string\"],\"default\":null},{\"name\":\"content\",\"type\":[\"null\",\"bytes\"],\"default\":null},{\"name\":\"parsedContent\",\"type\":{\"type\":\"array\",\"items\":\"string\"},\"default\":null},{\"name\":\"outlinks\",\"type\":{\"type\":\"map\",\"values\":\"string\"},\"default\":{}},{\"name\":\"headers\",\"type\":[\"null\",{\"type\":\"map\",\"values\":[\"null\",\"string\"]}],\"default\":null},{\"name\":\"metadata\",\"type\":{\"type\":\"record\",\"name\":\"Metadata\",\"fields\":[{\"name\":\"version\",\"type\":\"int\",\"default\":0},{\"name\":\"data\",\"type\":{\"type\":\"map\",\"values\":\"string\"},\"default\":null}]},\"default\":null},{\"name\":\"byteData\",\"type\":{\"type\":\"map\",\"values\":\"bytes\"},\"default\":{}},{\"name\":\"stringData\",\"type\":{\"type\":\"map\",\"values\":\"string\"},\"default\":{}}],\"default\":null}");
    private static final long serialVersionUID = 1505947786650219220L;

    /**
     * Enum containing all data bean's fields.
     */
    public static enum Field {
        URL(0, "url"),
        CONTENT(1, "content"),
        PARSED_CONTENT(2, "parsedContent"),
        OUTLINKS(3, "outlinks"),
        HEADERS(4, "headers"),
        METADATA(5, "metadata"),
        BYTE_DATA(6, "byteData"),
        STRING_DATA(7, "stringData"),;
        /**
         * Field's index.
         */
        private int index;

        /**
         * Field's name.
         */
        private String name;

        /**
         * Field's constructor
         *
         * @param index field's index.
         * @param name  field's name.
         */
        Field(int index, String name) {
            this.index = index;
            this.name = name;
        }

        /**
         * Gets field's index.
         *
         * @return int field's index.
         */
        public int getIndex() {
            return index;
        }

        /**
         * Gets field's name.
         *
         * @return String field's name.
         */
        public String getName() {
            return name;
        }

        /**
         * Gets field's attributes to string.
         *
         * @return String field's attributes to string.
         */
        @Override
        public String toString() {
            return name;
        }
    }

    ;

    public static final String[] _ALL_FIELDS = {
            "url",
            "content",
            "parsedContent",
            "outlinks",
            "headers",
            "metadata",
            "byteData",
            "stringData",
    };

    /**
     * Gets the total field count.
     *
     * @return int field count
     */
    public int getFieldsCount() {
        return WebPage._ALL_FIELDS.length;
    }

    @Family(name = "common")
    private Character cfCommon;

    @Family(name = "content")
    private Character cfContent;

    @Family(name = "parsed_content")
    private Character cfParsedContent;

    @Family(name = "outlinks")
    private Character cfOutlinks;

    @Column(name = "url", family = "common", qualifier = "url")
    private CharSequence url;

    @Column(name = "content", family = "content", qualifier = "content")
    private ByteBuffer content;

    @Column(name = "parsedContent", family = "parsed_content", qualifier = "parsed_content")
    private List<CharSequence> parsedContent;

    @Column(name = "outlinks", family = "outlinks", qualifier = "outlinks")
    private Map<CharSequence, CharSequence> outlinks;

    @Column(name = "headers", family = "common", qualifier = "headers")
    private Map<CharSequence, CharSequence> headers;

    @Column(name = "metadata", family = "common", qualifier = "common")
    private Metadata metadata;

    @Column(name = "byteData", family = "content", qualifier = "byte_data")
    private Map<CharSequence, ByteBuffer> byteData;

    @Column(name = "stringData", family = "content", qualifier = "string_data")
    private Map<CharSequence, CharSequence> stringData;

    @Override
    public Schema getSchema() {
        return SCHEMA$;
    }

    // Used by DatumWriter.  Applications should not call.
    @Override
    public Object get(int field$) {
        switch (field$) {
            case 0:
                return url;
            case 1:
                return content;
            case 2:
                return parsedContent;
            case 3:
                return outlinks;
            case 4:
                return headers;
            case 5:
                return metadata;
            case 6:
                return byteData;
            case 7:
                return stringData;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    // Used by DatumReader.  Applications should not call.
    @SuppressWarnings(value = "unchecked")
    @Override
    public void put(int field$, java.lang.Object value) {
        switch (field$) {
            case 0:
                url = (CharSequence) (value);
                break;
            case 1:
                content = (ByteBuffer) (value);
                break;
            case 2:
                parsedContent = (List<CharSequence>) ((value instanceof Dirtyable) ? value : new DirtyListWrapper((java.util.List) value));
                break;
            case 3:
                outlinks = (Map<CharSequence, CharSequence>) ((value instanceof Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 4:
                headers = (Map<CharSequence, CharSequence>) (value);
                break;
            case 5:
                metadata = (Metadata) (value);
                break;
            case 6:
                byteData = (Map<CharSequence, ByteBuffer>) ((value instanceof Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            case 7:
                stringData = (Map<CharSequence, CharSequence>) ((value instanceof Dirtyable) ? value : new DirtyMapWrapper((Map) value));
                break;
            default:
                throw new AvroRuntimeException("Bad index");
        }
    }

    /**
     * Gets the value of the 'url' field.
     */
    public CharSequence getUrl() {
        return url;
    }

    /**
     * Sets the value of the 'url' field.
     *
     * @param value the value to set.
     */
    public void setUrl(CharSequence value) {
        this.url = value;
        setDirty(0);
    }

    /**
     * Checks the dirty status of the 'url' field. A field is dirty if it represents a change that has not yet been written to the database.
     */
    public boolean isUrlDirty() {
        return isDirty(0);
    }

    /**
     * Gets the value of the 'content' field.
     */
    public ByteBuffer getContent() {
        return content;
    }

    /**
     * Sets the value of the 'content' field.
     *
     * @param value the value to set.
     */
    public void setContent(ByteBuffer value) {
        this.content = value;
        setDirty(1);
    }

    /**
     * Checks the dirty status of the 'content' field. A field is dirty if it represents a change that has not yet been written to the database.
     */
    public boolean isContentDirty() {
        return isDirty(1);
    }

    /**
     * Gets the value of the 'parsedContent' field.
     */
    public List<CharSequence> getParsedContent() {
        return parsedContent;
    }

    /**
     * Sets the value of the 'parsedContent' field.
     *
     * @param value the value to set.
     */
    public void setParsedContent(List<CharSequence> value) {
        this.parsedContent = (value instanceof Dirtyable) ? value : new DirtyListWrapper(value);
        setDirty(2);
    }

    /**
     * Checks the dirty status of the 'parsedContent' field. A field is dirty if it represents a change that has not yet been written to the database.
     */
    public boolean isParsedContentDirty() {
        return isDirty(2);
    }

    /**
     * Gets the value of the 'outlinks' field.
     */
    public Map<CharSequence, CharSequence> getOutlinks() {
        return outlinks;
    }

    /**
     * Sets the value of the 'outlinks' field.
     *
     * @param value the value to set.
     */
    public void setOutlinks(Map<CharSequence, CharSequence> value) {
        this.outlinks = (value instanceof Dirtyable) ? value : new DirtyMapWrapper(value);
        setDirty(3);
    }

    /**
     * Checks the dirty status of the 'outlinks' field. A field is dirty if it represents a change that has not yet been written to the database.
     */
    public boolean isOutlinksDirty() {
        return isDirty(3);
    }

    /**
     * Gets the value of the 'headers' field.
     */
    public Map<CharSequence, CharSequence> getHeaders() {
        return headers;
    }

    /**
     * Sets the value of the 'headers' field.
     *
     * @param value the value to set.
     */
    public void setHeaders(Map<CharSequence, CharSequence> value) {
        this.headers = value;
        setDirty(4);
    }

    /**
     * Checks the dirty status of the 'headers' field. A field is dirty if it represents a change that has not yet been written to the database.
     */
    public boolean isHeadersDirty() {
        return isDirty(4);
    }

    /**
     * Gets the value of the 'metadata' field.
     */
    public Metadata getMetadata() {
        return metadata;
    }

    /**
     * Sets the value of the 'metadata' field.
     *
     * @param value the value to set.
     */
    public void setMetadata(Metadata value) {
        this.metadata = value;
        setDirty(5);
    }

    /**
     * Checks the dirty status of the 'metadata' field. A field is dirty if it represents a change that has not yet been written to the database.
     */
    public boolean isMetadataDirty() {
        return isDirty(5);
    }

    /**
     * Gets the value of the 'byteData' field.
     */
    public Map<CharSequence, ByteBuffer> getByteData() {
        return byteData;
    }

    /**
     * Sets the value of the 'byteData' field.
     *
     * @param value the value to set.
     */
    public void setByteData(Map<CharSequence, ByteBuffer> value) {
        this.byteData = (value instanceof Dirtyable) ? value : new DirtyMapWrapper(value);
        setDirty(6);
    }

    /**
     * Checks the dirty status of the 'byteData' field. A field is dirty if it represents a change that has not yet been written to the database.
     */
    public boolean isByteDataDirty() {
        return isDirty(6);
    }

    /**
     * Gets the value of the 'stringData' field.
     */
    public Map<CharSequence, CharSequence> getStringData() {
        return stringData;
    }

    /**
     * Sets the value of the 'stringData' field.
     *
     * @param value the value to set.
     */
    public void setStringData(Map<CharSequence, CharSequence> value) {
        this.stringData = (value instanceof Dirtyable) ? value : new DirtyMapWrapper(value);
        setDirty(7);
    }

    /**
     * Checks the dirty status of the 'stringData' field. A field is dirty if it represents a change that has not yet been written to the database.
     */
    public boolean isStringDataDirty() {
        return isDirty(7);
    }

    /**
     * Creates a new WebPage RecordBuilder
     */
    public static WebPage.Builder newBuilder() {
        return new WebPage.Builder();
    }

    /**
     * Creates a new WebPage RecordBuilder by copying an existing Builder
     */
    public static WebPage.Builder newBuilder(WebPage.Builder other) {
        return new WebPage.Builder(other);
    }

    /**
     * Creates a new WebPage RecordBuilder by copying an existing WebPage instance
     */
    public static WebPage.Builder newBuilder(WebPage other) {
        return new WebPage.Builder(other);
    }

    private static ByteBuffer deepCopyToReadOnlyBuffer(ByteBuffer input) {
        ByteBuffer copy = ByteBuffer.allocate(input.capacity());
        int position = input.position();
        input.reset();
        int mark = input.position();
        int limit = input.limit();
        input.rewind();
        input.limit(input.capacity());
        copy.put(input);
        input.rewind();
        copy.rewind();
        input.position(mark);
        input.mark();
        copy.position(mark);
        copy.mark();
        input.position(position);
        copy.position(position);
        input.limit(limit);
        copy.limit(limit);
        return copy.asReadOnlyBuffer();
    }

    /**
     * RecordBuilder for WebPage instances.
     */
    public static class Builder extends SpecificRecordBuilderBase<WebPage> implements RecordBuilder<WebPage> {
        private CharSequence url;
        private ByteBuffer content;
        private List<CharSequence> parsedContent;
        private Map<CharSequence, CharSequence> outlinks;
        private Map<CharSequence, CharSequence> headers;
        private Metadata metadata;
        private Map<CharSequence, ByteBuffer> byteData;
        private Map<CharSequence, CharSequence> stringData;

        /**
         * Creates a new Builder
         */
        private Builder() {
            super(WebPage.SCHEMA$);
        }

        /**
         * Creates a Builder by copying an existing Builder
         */
        private Builder(WebPage.Builder other) {
            super(other);
        }

        /**
         * Creates a Builder by copying an existing WebPage instance
         */
        private Builder(WebPage other) {
            super(WebPage.SCHEMA$);
            if (isValidValue(fields()[0], other.url)) {
                this.url = (CharSequence) data().deepCopy(fields()[0].schema(), other.url);
                fieldSetFlags()[0] = true;
            }
            if (isValidValue(fields()[1], other.content)) {
                this.content = (ByteBuffer) data().deepCopy(fields()[1].schema(), other.content);
                fieldSetFlags()[1] = true;
            }
            if (isValidValue(fields()[2], other.parsedContent)) {
                this.parsedContent = (List<CharSequence>) data().deepCopy(fields()[2].schema(), other.parsedContent);
                fieldSetFlags()[2] = true;
            }
            if (isValidValue(fields()[3], other.outlinks)) {
                this.outlinks = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[3].schema(), other.outlinks);
                fieldSetFlags()[3] = true;
            }
            if (isValidValue(fields()[4], other.headers)) {
                this.headers = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[4].schema(), other.headers);
                fieldSetFlags()[4] = true;
            }
            if (isValidValue(fields()[5], other.metadata)) {
                this.metadata = (Metadata) data().deepCopy(fields()[5].schema(), other.metadata);
                fieldSetFlags()[5] = true;
            }
            if (isValidValue(fields()[6], other.byteData)) {
                this.byteData = (Map<CharSequence, ByteBuffer>) data().deepCopy(fields()[6].schema(), other.byteData);
                fieldSetFlags()[6] = true;
            }
            if (isValidValue(fields()[7], other.stringData)) {
                this.stringData = (Map<CharSequence, CharSequence>) data().deepCopy(fields()[7].schema(), other.stringData);
                fieldSetFlags()[7] = true;
            }
        }


        /**
         * Gets the value of the 'url' field
         */
        public CharSequence getUrl() {
            return url;
        }

        /**
         * Sets the value of the 'url' field
         */
        public WebPage.Builder setUrl(CharSequence value) {
            validate(fields()[0], value);
            this.url = value;
            fieldSetFlags()[0] = true;
            return this;
        }

        /**
         * Checks whether the 'url' field has been set
         */
        public boolean hasUrl() {
            return fieldSetFlags()[0];
        }

        /**
         * Clears the value of the 'url' field
         */
        public WebPage.Builder clearUrl() {
            url = null;
            fieldSetFlags()[0] = false;
            return this;
        }

        /**
         * Gets the value of the 'content' field
         */
        public ByteBuffer getContent() {
            return content;
        }

        /**
         * Sets the value of the 'content' field
         */
        public WebPage.Builder setContent(ByteBuffer value) {
            validate(fields()[1], value);
            this.content = value;
            fieldSetFlags()[1] = true;
            return this;
        }

        /**
         * Checks whether the 'content' field has been set
         */
        public boolean hasContent() {
            return fieldSetFlags()[1];
        }

        /**
         * Clears the value of the 'content' field
         */
        public WebPage.Builder clearContent() {
            content = null;
            fieldSetFlags()[1] = false;
            return this;
        }

        /**
         * Gets the value of the 'parsedContent' field
         */
        public List<CharSequence> getParsedContent() {
            return parsedContent;
        }

        /**
         * Sets the value of the 'parsedContent' field
         */
        public WebPage.Builder setParsedContent(List<CharSequence> value) {
            validate(fields()[2], value);
            this.parsedContent = value;
            fieldSetFlags()[2] = true;
            return this;
        }

        /**
         * Checks whether the 'parsedContent' field has been set
         */
        public boolean hasParsedContent() {
            return fieldSetFlags()[2];
        }

        /**
         * Clears the value of the 'parsedContent' field
         */
        public WebPage.Builder clearParsedContent() {
            parsedContent = null;
            fieldSetFlags()[2] = false;
            return this;
        }

        /**
         * Gets the value of the 'outlinks' field
         */
        public Map<CharSequence, CharSequence> getOutlinks() {
            return outlinks;
        }

        /**
         * Sets the value of the 'outlinks' field
         */
        public WebPage.Builder setOutlinks(Map<CharSequence, CharSequence> value) {
            validate(fields()[3], value);
            this.outlinks = value;
            fieldSetFlags()[3] = true;
            return this;
        }

        /**
         * Checks whether the 'outlinks' field has been set
         */
        public boolean hasOutlinks() {
            return fieldSetFlags()[3];
        }

        /**
         * Clears the value of the 'outlinks' field
         */
        public WebPage.Builder clearOutlinks() {
            outlinks = null;
            fieldSetFlags()[3] = false;
            return this;
        }

        /**
         * Gets the value of the 'headers' field
         */
        public Map<CharSequence, CharSequence> getHeaders() {
            return headers;
        }

        /**
         * Sets the value of the 'headers' field
         */
        public WebPage.Builder setHeaders(Map<CharSequence, CharSequence> value) {
            validate(fields()[4], value);
            this.headers = value;
            fieldSetFlags()[4] = true;
            return this;
        }

        /**
         * Checks whether the 'headers' field has been set
         */
        public boolean hasHeaders() {
            return fieldSetFlags()[4];
        }

        /**
         * Clears the value of the 'headers' field
         */
        public WebPage.Builder clearHeaders() {
            headers = null;
            fieldSetFlags()[4] = false;
            return this;
        }

        /**
         * Gets the value of the 'metadata' field
         */
        public Metadata getMetadata() {
            return metadata;
        }

        /**
         * Sets the value of the 'metadata' field
         */
        public WebPage.Builder setMetadata(Metadata value) {
            validate(fields()[5], value);
            this.metadata = value;
            fieldSetFlags()[5] = true;
            return this;
        }

        /**
         * Checks whether the 'metadata' field has been set
         */
        public boolean hasMetadata() {
            return fieldSetFlags()[5];
        }

        /**
         * Clears the value of the 'metadata' field
         */
        public WebPage.Builder clearMetadata() {
            metadata = null;
            fieldSetFlags()[5] = false;
            return this;
        }

        /**
         * Gets the value of the 'byteData' field
         */
        public Map<CharSequence, ByteBuffer> getByteData() {
            return byteData;
        }

        /**
         * Sets the value of the 'byteData' field
         */
        public WebPage.Builder setByteData(Map<CharSequence, ByteBuffer> value) {
            validate(fields()[6], value);
            this.byteData = value;
            fieldSetFlags()[6] = true;
            return this;
        }

        /**
         * Checks whether the 'byteData' field has been set
         */
        public boolean hasByteData() {
            return fieldSetFlags()[6];
        }

        /**
         * Clears the value of the 'byteData' field
         */
        public WebPage.Builder clearByteData() {
            byteData = null;
            fieldSetFlags()[6] = false;
            return this;
        }

        /**
         * Gets the value of the 'stringData' field
         */
        public Map<CharSequence, CharSequence> getStringData() {
            return stringData;
        }

        /**
         * Sets the value of the 'stringData' field
         */
        public WebPage.Builder setStringData(Map<CharSequence, CharSequence> value) {
            validate(fields()[7], value);
            this.stringData = value;
            fieldSetFlags()[7] = true;
            return this;
        }

        /**
         * Checks whether the 'stringData' field has been set
         */
        public boolean hasStringData() {
            return fieldSetFlags()[7];
        }

        /**
         * Clears the value of the 'stringData' field
         */
        public WebPage.Builder clearStringData() {
            stringData = null;
            fieldSetFlags()[7] = false;
            return this;
        }

        public WebPage build() {
            try {
                WebPage record = new WebPage();
                record.url = fieldSetFlags()[0] ? this.url : (CharSequence) defaultValue(fields()[0]);
                record.content = fieldSetFlags()[1] ? this.content : (ByteBuffer) defaultValue(fields()[1]);
                record.parsedContent = fieldSetFlags()[2] ? this.parsedContent : (List<CharSequence>) new DirtyListWrapper((java.util.List) defaultValue(fields()[2]));
                record.outlinks = fieldSetFlags()[3] ? this.outlinks : (Map<CharSequence, CharSequence>) new DirtyMapWrapper((Map) defaultValue(fields()[3]));
                record.headers = fieldSetFlags()[4] ? this.headers : (Map<CharSequence, CharSequence>) defaultValue(fields()[4]);
                record.metadata = fieldSetFlags()[5] ? this.metadata : (Metadata) Metadata.newBuilder().build();
                record.byteData = fieldSetFlags()[6] ? this.byteData : (Map<CharSequence, ByteBuffer>) new DirtyMapWrapper((Map) defaultValue(fields()[6]));
                record.stringData = fieldSetFlags()[7] ? this.stringData : (Map<CharSequence, CharSequence>) new DirtyMapWrapper((Map) defaultValue(fields()[7]));
                return record;
            } catch (Exception e) {
                throw new AvroRuntimeException(e);
            }
        }
    }

    public WebPage newInstance() {
        return newBuilder().build();
    }
}