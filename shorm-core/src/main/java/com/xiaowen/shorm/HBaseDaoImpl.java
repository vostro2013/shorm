package com.xiaowen.shorm;

import com.xiaowen.shorm.annotations.Column;
import com.xiaowen.shorm.annotations.Family;
import com.xiaowen.shorm.annotations.Table;
import com.xiaowen.shorm.mapper.HBaseColumn;
import com.xiaowen.shorm.mapper.HBaseMapping;
import com.xiaowen.shorm.persistency.BeanFactory;
import com.xiaowen.shorm.persistency.Persistent;
import com.xiaowen.shorm.persistency.impl.BeanFactoryImpl;
import com.xiaowen.shorm.persistency.impl.DirtyListWrapper;
import com.xiaowen.shorm.persistency.impl.DirtyMapWrapper;
import com.xiaowen.shorm.persistency.impl.PersistentBase;
import com.xiaowen.shorm.query.HBaseQuery;
import com.xiaowen.shorm.util.AvroUtils;
import com.xiaowen.shorm.util.HBaseByteInterface;
import com.xiaowen.shorm.util.HBaseFilterUtil;
import org.apache.avro.Schema;
import org.apache.avro.util.Utf8;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;

/**
 * 泛化的 HBase Dao 基础类
 * @param <K> key
 * @param <T> Persistent
 *
 * @author: wenc.hao
 * @date: 2018/2/12 16:30
 * @since: v2.0.0
 */
@Repository
public class HBaseDaoImpl<K, T extends PersistentBase> implements HBaseDao<K, T> {
    private static final Logger LOGGER = LoggerFactory.getLogger(HBaseDaoImpl.class);

    protected BeanFactory<K, T> beanFactory;

    /**
     * The schema of the persistent class
     */
    protected Schema schema;

    /**
     * A map of field names to Field objects containing schema's fields
     */
    protected Map<String, Schema.Field> fieldMap;

    @Resource(name = "hbaseConfiguration")
    protected Configuration conf;

    @Autowired
    protected HbaseTemplate hbaseTemplate;

    private volatile HBaseMapping mapping;

    private static final int SCANNER_CACHING_PROPERTIES_DEFAULT = 0;

    private int scannerCaching = SCANNER_CACHING_PROPERTIES_DEFAULT;


    private String tableName;

    private HBaseFilterUtil<K, T> filterUtil;

    public HBaseDaoImpl() {
    }

    public HBaseDaoImpl(Class<K> keyClass, Class<T> persistentClass) {
        initialize(keyClass, persistentClass);
    }

    public void initialize(Class<K> keyClass, Class<T> persistentClass) {
        try {
            if (this.beanFactory == null) {
                this.beanFactory = new BeanFactoryImpl<>(keyClass, persistentClass);
            }
            schema = this.beanFactory.getCachedPersistent().getSchema();
            fieldMap = AvroUtils.getFieldMap(schema);
            mapping = tableClass(persistentClass);
            filterUtil = new HBaseFilterUtil<>();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Override
    public T get(K key) {
        return get(key, getFieldsToQuery(null));
    }

    @Override
    public T get(K key, final String[] fields) {
        try {
            return hbaseTemplate.get(tableName, String.valueOf(key), new RowMapper<T>() {
                @Override
                public T mapRow(Result result, int rowNum) throws Exception {
                    return newInstance(result, fields);
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void put(final K key, final T obj) {
        try {
            hbaseTemplate.execute(tableName, new TableCallback<T>() {
                @Override
                public T doInTable(HTableInterface table) throws Throwable {
                    Schema schema = obj.getSchema();
                    byte[] keyRaw = HBaseByteInterface.toBytes(key);
                    Put put = new Put(keyRaw);
                    Delete delete = new Delete(keyRaw);
                    List<Schema.Field> fields = schema.getFields();
                    for (int i = 0; i < fields.size(); i++) {
                        if (!obj.isDirty(i)) {
                            continue;
                        }
                        Schema.Field field = fields.get(i);
                        Object o = obj.get(i);
                        HBaseColumn column = mapping.getColumn(field.name());
                        if (column == null) {
                            throw new RuntimeException("HBase mapping for field ["
                                    + obj.getClass().getName() + "#" + field.name() + "] not found.");
                        }
                        addPutsAndDeletes(put, delete, o, field.schema().getType(), field.schema(), column, column.getQualifier());
                    }
                    if (put.size() > 0) {
                        table.put(put);
                    }
                    return obj;
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public boolean delete(final K key) {
        try {
            hbaseTemplate.execute(tableName, new TableCallback<T>() {
                @Override
                public T doInTable(HTableInterface table) throws Throwable {
                    table.delete(new Delete(HBaseByteInterface.toBytes(key)));
                    return null;
                }
            });
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public long deleteByQuery(final HBaseQuery<K, T> query) {
        try {
            List<K> keys = null;
            // Query
            // check if query.fields is null
            query.setFields(getFieldsToQuery(null));
            if (query.getStartKey() != null && query.getStartKey().equals(query.getEndKey())) {
                T result = get(query.getStartKey());
                if (result != null) {
                    keys = new ArrayList<>(1);
                    keys.add(query.getStartKey());
                }
            } else {
                final Scan scan = new Scan();

                scan.setCaching(this.getScannerCaching());

                if (query.getStartKey() != null) {
                    scan.setStartRow(HBaseByteInterface.toBytes(query.getStartKey()));
                }
                if (query.getEndKey() != null) {
                    scan.setStopRow(HBaseByteInterface.toBytes(query.getEndKey()));
                }
                addFields(scan, query);
                if (query.getFilter() != null) {
                    boolean succeeded = filterUtil.setFilter(scan, query.getFilter(), mapping);
                    if (succeeded) {
                        // don't need local filter
                        query.setLocalFilterEnabled(false);
                    }
                }

                keys = hbaseTemplate.find(tableName, scan, new RowMapper<K>() {
                    @Override
                    public K mapRow(Result result, int rowNum) throws Exception {
                        return newKey(result);
                    }
                });
            }
            // Delete
            int countOfDelete = 0;
            if (keys != null && keys.size() > 0) {
                String[] fields = getFieldsToQuery(query.getFields());
                // find whether all fields are queried, which means that complete rows will be deleted
                final boolean isAllFields = Arrays.equals(fields, getFields());
                final List<K> finalKeys = keys;
                countOfDelete = hbaseTemplate.execute(tableName, new TableCallback<Integer>() {
                    @Override
                    public Integer doInTable(HTableInterface table) throws Throwable {
                        ArrayList<Delete> deletes = new ArrayList<>();
                        for (K key : finalKeys) {
                            Delete delete = new Delete(HBaseByteInterface.toBytes(key));
                            deletes.add(delete);
                            if (!isAllFields) {
                                addFields(delete, query);
                            }
                        }
                        table.delete(deletes);
                        return deletes.size();
                    }
                });
            }
            return countOfDelete;
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> findByQuery(HBaseQuery<K, T> query) {
        try {
            // check if query.fields is null
            query.setFields(getFieldsToQuery(null));

            if (query.getStartKey() != null && query.getStartKey().equals(query.getEndKey())) {
                List<T> result = new ArrayList<>();
                T object = this.get(query.getStartKey(), query.getFields());
                if (object != null) {
                    result.add(object);
                }
                return result;
            } else {
                final Scan scan = new Scan();

                scan.setCaching(this.getScannerCaching());

                if (query.getStartKey() != null) {
                    scan.setStartRow(HBaseByteInterface.toBytes(query.getStartKey()));
                }
                if (query.getEndKey() != null) {
                    scan.setStopRow(HBaseByteInterface.toBytes(query.getEndKey()));
                }
                addFields(scan, query);
                if (query.getFilter() != null) {
                    boolean succeeded = filterUtil.setFilter(scan, query.getFilter(), mapping);
                    if (succeeded) {
                        // don't need local filter
                        query.setLocalFilterEnabled(false);
                    }
                }

                return hbaseTemplate.find(tableName, scan, new RowMapper<T>() {
                    @Override
                    public T mapRow(Result result, int rowNum) throws Exception {
                        return newInstance(result, getFieldsToQuery(null));
                    }
                });
            }
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public List<T> findAll() {
        return hbaseTemplate.find(tableName, "f", new RowMapper<T>() {
            @Override
            public T mapRow(Result result, int rowNum) throws Exception {
                return newInstance(result, getFieldsToQuery(null));
            }
        });
    }

    private void addPutsAndDeletes(Put put, Delete delete, Object o, Schema.Type type,
                                   Schema schema, HBaseColumn hcol, byte[] qualifier) throws IOException {
        switch (type) {
            case UNION:
                if (isNullable(schema) && o == null) {
                    if (qualifier == null) {
                        delete.deleteFamily(hcol.getFamily());
                    } else {
                        delete.deleteColumn(hcol.getFamily(), qualifier);
                    }
                } else {
//                    int index = GenericData.get().resolveUnion(schema, o);
                    int index = getResolvedUnionIndex(schema);
                    // if more than 2 type in union, serialize directly for now
                    if (index > 1) {
                        byte[] serializedBytes = HBaseByteInterface.toBytes(o, schema);
                        put.add(hcol.getFamily(), qualifier, serializedBytes);
                    } else {
                        Schema resolvedSchema = schema.getTypes().get(index);
                        addPutsAndDeletes(put, delete, o, resolvedSchema.getType(), resolvedSchema, hcol, qualifier);
                    }
                }
                break;
            case MAP:
                // if it's a map that has been modified, then the content should be replaced by the new one
                // This is because we don't know if the content has changed or not.
                if (qualifier == null) {
                    delete.deleteFamily(hcol.getFamily());
                } else {
                    delete.deleteColumn(hcol.getFamily(), qualifier);
                }
                @SuppressWarnings({"rawtypes", "unchecked"})
                Set<Map.Entry> set = ((Map) o).entrySet();
                for (@SuppressWarnings("rawtypes") Map.Entry entry : set) {
                    byte[] qual = HBaseByteInterface.toBytes(entry.getKey());
                    addPutsAndDeletes(put, delete, entry.getValue(), schema.getValueType()
                            .getType(), schema.getValueType(), hcol, qual);
                }
                break;
            case ARRAY:
                List<?> array = (List<?>) o;
                int j = 0;
                for (Object item : array) {
                    addPutsAndDeletes(put, delete, item, schema.getElementType().getType(),
                            schema.getElementType(), hcol, Bytes.toBytes(j++));
                }
                break;
            default:
                byte[] serializedBytes = HBaseByteInterface.toBytes(o, schema);
                put.add(hcol.getFamily(), qualifier, serializedBytes);
                break;
        }
    }

    private boolean isNullable(Schema unionSchema) {
        for (Schema innerSchema : unionSchema.getTypes()) {
            if (innerSchema.getType().equals(Schema.Type.NULL)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether the fields argument is null, and if so returns all the fields
     * of the Persistent object, else returns the argument.
     */
    protected String[] getFieldsToQuery(String[] fields) {
        if (fields != null) {
            return fields;
        }
        return getFields();
    }

    protected String[] getFields() {
        List<Schema.Field> schemaFields = beanFactory.getCachedPersistent().getSchema().getFields();

        List<Schema.Field> list = new ArrayList<>();
        for (Schema.Field field : schemaFields) {
            if (!Persistent.DIRTY_BYTES_FIELD_NAME.equalsIgnoreCase(field.name())) {
                list.add(field);
            }
        }
        schemaFields = list;

        String[] fieldNames = new String[schemaFields.size()];
        for (int i = 0; i < fieldNames.length; i++) {
            fieldNames[i] = schemaFields.get(i).name();
        }

        return fieldNames;
    }

    private K newKey() {
        try {
            return beanFactory.newKey();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private K newKey(Result result) {
        try {
            return HBaseByteInterface.fromBytes(this.beanFactory.getKeyClass(), result.getRow());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private T newPersistent() {
        try {
            return beanFactory.newPersistent();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private void addFields(Get get, String[] fieldNames) {
        for (String f : fieldNames) {
            HBaseColumn col = mapping.getColumn(f);
            if (col == null) {
                throw new RuntimeException("HBase mapping for field [" + f + "] not found.");
            }
            Schema fieldSchema = fieldMap.get(f).schema();
            addFamilyOrColumn(get, col, fieldSchema);
        }
    }

    private void addFamilyOrColumn(Get get, HBaseColumn col, Schema fieldSchema) {
        switch (fieldSchema.getType()) {
            case UNION:
                int index = getResolvedUnionIndex(fieldSchema);
                Schema resolvedSchema = fieldSchema.getTypes().get(index);
                addFamilyOrColumn(get, col, resolvedSchema);
                break;
            case MAP:
            case ARRAY:
                get.addFamily(col.getFamily());
                break;
            default:
                get.addColumn(col.getFamily(), col.getQualifier());
                break;
        }
    }

    private void addFields(Scan scan, HBaseQuery<K, T> query) throws IOException {
        String[] fields = query.getFields();
        for (String f : fields) {
            HBaseColumn col = mapping.getColumn(f);
            if (col == null) {
                throw new RuntimeException("HBase mapping for field [" + f + "] not found.");
            }
            Schema fieldSchema = fieldMap.get(f).schema();
            addFamilyOrColumn(scan, col, fieldSchema);
        }
    }

    private void addFamilyOrColumn(Scan scan, HBaseColumn col, Schema fieldSchema) {
        switch (fieldSchema.getType()) {
            case UNION:
                int index = getResolvedUnionIndex(fieldSchema);
                Schema resolvedSchema = fieldSchema.getTypes().get(index);
                addFamilyOrColumn(scan, col, resolvedSchema);
                break;
            case MAP:
            case ARRAY:
                scan.addFamily(col.getFamily());
                break;
            default:
                scan.addColumn(col.getFamily(), col.getQualifier());
                break;
        }
    }

    // TODO: HBase Get, Scan, Delete should extend some common interface with
    // addFamily, etc
    private void addFields(Delete delete, HBaseQuery<K, T> query) throws IOException {
        String[] fields = query.getFields();
        for (String f : fields) {
            HBaseColumn col = mapping.getColumn(f);
            if (col == null) {
                throw new RuntimeException("HBase mapping for field [" + f + "] not found.");
            }
            Schema fieldSchema = fieldMap.get(f).schema();
            addFamilyOrColumn(delete, col, fieldSchema);
        }
    }

    private void addFamilyOrColumn(Delete delete, HBaseColumn col, Schema fieldSchema) {
        switch (fieldSchema.getType()) {
            case UNION:
                int index = getResolvedUnionIndex(fieldSchema);
                Schema resolvedSchema = fieldSchema.getTypes().get(index);
                addFamilyOrColumn(delete, col, resolvedSchema);
                break;
            case MAP:
            case ARRAY:
                delete.deleteFamily(col.getFamily());
                break;
            default:
                delete.deleteColumn(col.getFamily(), col.getQualifier());
                break;
        }
    }

    private void addTimeRange(Get get, HBaseQuery<K, T> query) throws IOException {
        if (query.getStartTime() > 0 || query.getEndTime() > 0) {
            if (query.getStartTime() == query.getEndTime()) {
                get.setTimeStamp(query.getStartTime());
            } else {
                long startTime = query.getStartTime() > 0 ? query.getStartTime() : 0;
                long endTime = query.getEndTime() > 0 ? query.getEndTime() : Long.MAX_VALUE;
                get.setTimeRange(startTime, endTime);
            }
        }
    }

    private T newInstance(Result result, String[] fields) throws IOException {
        if (result == null || result.isEmpty()) {
            return null;
        }

        T persistent = newPersistent();
        for (String f : fields) {
            HBaseColumn col = mapping.getColumn(f);
            if (col == null) {
                throw new RuntimeException("HBase mapping for field [" + f + "] not found.");
            }
            Schema.Field field = fieldMap.get(f);
            Schema fieldSchema = field.schema();
            setField(result, persistent, col, field, fieldSchema);
        }
        persistent.clearDirty();
        return persistent;
    }

    private void setField(Result result, T persistent, HBaseColumn col, Schema.Field field, Schema fieldSchema) throws IOException {
        switch (fieldSchema.getType()) {
            case UNION:
                int index = getResolvedUnionIndex(fieldSchema);
                // if more than 2 type in union, deserialize directly for now
                if (index > 1) {
                    byte[] val = result.getValue(col.getFamily(), col.getQualifier());
                    if (val == null) {
                        return;
                    }
                    setField(persistent, field, val);
                } else {
                    Schema resolvedSchema = fieldSchema.getTypes().get(index);
                    setField(result, persistent, col, field, resolvedSchema);
                }
                break;
            case MAP:
                NavigableMap<byte[], byte[]> qualMap = result.getNoVersionMap().get(col.getFamily());
                if (qualMap == null) {
                    return;
                }
                Schema valueSchema = fieldSchema.getValueType();
                Map<Utf8, Object> map = new HashMap<>();
                for (Map.Entry<byte[], byte[]> e : qualMap.entrySet()) {
                    map.put(new Utf8(Bytes.toString(e.getKey())), HBaseByteInterface.fromBytes(valueSchema, e.getValue()));
                }
                setField(persistent, field, map);
                break;
            case ARRAY:
                qualMap = result.getFamilyMap(col.getFamily());
                if (qualMap == null) {
                    return;
                }
                valueSchema = fieldSchema.getElementType();
                ArrayList<Object> arrayList = new ArrayList<>();
                DirtyListWrapper<Object> dirtyListWrapper = new DirtyListWrapper<>(arrayList);
                for (Map.Entry<byte[], byte[]> e : qualMap.entrySet()) {
                    dirtyListWrapper.add(HBaseByteInterface.fromBytes(valueSchema, e.getValue()));
                }
                setField(persistent, field, arrayList);
                break;
            default:
                byte[] val = result.getValue(col.getFamily(), col.getQualifier());
                if (val == null) {
                    return;
                }
                setField(persistent, field, val);
                break;
        }
    }

    // TODO temporary solution, has to be changed after implementation of saving the index of union type
    private int getResolvedUnionIndex(Schema unionScema) {
        if (unionScema.getTypes().size() == 2) {
            // schema [type0, type1]
            Schema.Type type0 = unionScema.getTypes().get(0).getType();
            Schema.Type type1 = unionScema.getTypes().get(1).getType();

            // Check if types are different and there's a "null", like ["null","type"] or ["type","null"]
            if (!type0.equals(type1) && (type0.equals(Schema.Type.NULL) || type1.equals(Schema.Type.NULL))) {
                if (type0.equals(Schema.Type.NULL)) {
                    return 1;
                } else {
                    return 0;
                }
            }
        }
        return 2;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void setField(T persistent, Schema.Field field, Map map) {
        persistent.put(field.pos(), new DirtyMapWrapper(map));
    }

    private void setField(T persistent, Schema.Field field, byte[] val) throws IOException {
        persistent.put(field.pos(), HBaseByteInterface.fromBytes(field.schema(), val));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void setField(T persistent, Schema.Field field, List list) {
        persistent.put(field.pos(), new DirtyListWrapper(list));
    }

    private HBaseMapping tableClass(Class<?> tableClass) {
        HBaseMapping.HBaseMappingBuilder mappingBuilder = new HBaseMapping.HBaseMappingBuilder();
        Table aTable = (Table) tableClass.getAnnotation(Table.class);
        if (aTable != null) {
            tableName = aTable.name();
            mappingBuilder.setTableName(tableName);
            Field[] fields = tableClass.getDeclaredFields();
            int length = fields.length;
            for (int index = 0; index < length; ++index) {
                Field field = fields[index];
                Annotation[] annotations = field.getDeclaredAnnotations();
                for (Annotation annotation : annotations) {
                    if (annotation instanceof Column) {
                        Column column = (Column) annotation;
                        mappingBuilder.addField(column.name(), column.family(), column.qualifier());
                        mappingBuilder.addColumnFamily(tableName, column.family());
                    } else if (annotation instanceof Family) {
                        Family family = (Family) annotation;
                        mappingBuilder.addFamilyProps(tableName, family.name(), family.compression(),
                                family.blockCache(), family.blockSize(), family.bloomFilter(),
                                family.maxVersions(), family.timeToLive(), family.inMemory());
                    }
                }
            }
        }
        return mappingBuilder.build();
    }

    /**
     * Gets the Scanner Caching optimization value
     *
     * @return The value used internally in {@link Scan#setCaching(int)}
     */
    public int getScannerCaching() {
        return this.scannerCaching;
    }

    /**
     * Sets the value for Scanner Caching optimization
     *
     * @param numRows the number of rows for caching >= 0
     * @return Fluent interface;
     * @see Scan#setCaching(int)
     */
    public HBaseDao<K, T> setScannerCaching(int numRows) {
        if (numRows < 0) {
            LOGGER.warn("Invalid Scanner Caching optimization value. Cannot set to: " + numRows + ".");
            return this;
        }
        this.scannerCaching = numRows;
        return this;
    }
}
