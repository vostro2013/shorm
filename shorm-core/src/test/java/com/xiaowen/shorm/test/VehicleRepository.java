package com.xiaowen.shorm.test;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.client.HTableInterface;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.data.hadoop.hbase.RowMapper;
import org.springframework.data.hadoop.hbase.TableCallback;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author: xiaowen
 * @date: 2018/8/14
 * @since:
 */
@Repository
public class VehicleRepository {
    @Autowired
    private HbaseTemplate hbaseTemplate;

    private static final int SCANNER_CACHING_PROPERTIES_DEFAULT = 0;

    private int scannerCaching = SCANNER_CACHING_PROPERTIES_DEFAULT;

    private String tableName = "vehicle_date:snappy_diff_location_report_20180620";

    public static String CF_INFO = "LR";

    private byte[] qC = Bytes.toBytes("C");

    public Vehicle get(String key) {
        return hbaseTemplate.get(tableName, String.valueOf(key), new RowMapper<Vehicle>() {
            @Override
            public Vehicle mapRow(Result result, int rowNum) {
                return new Vehicle(Bytes.toString(result.getRow()),
                        Bytes.toString(result.getValue(CF_INFO.getBytes(), qC)));
            }
        });
    }

    public List<Vehicle> findAll() {
        return hbaseTemplate.find(tableName, CF_INFO, new RowMapper<Vehicle>() {
            @Override
            public Vehicle mapRow(Result result, int rowNum) {
                return new Vehicle(Bytes.toString(result.getRow()),
                        Bytes.toString(result.getValue(CF_INFO.getBytes(), qC)));
            }
        });
    }

    public List<Vehicle> findByQuery(String startKey, String endKey) {
        try {
            final Scan scan = new Scan();
            scan.setCaching(this.scannerCaching);

            if (StringUtils.isNotEmpty(startKey)) {
                scan.setStartRow(startKey.getBytes());
            }
            if (StringUtils.isNotEmpty(endKey)) {
                scan.setStopRow(endKey.getBytes());
            }

            return hbaseTemplate.find(tableName, scan, new RowMapper<Vehicle>() {
                @Override
                public Vehicle mapRow(Result result, int rowNum) throws Exception {
                    return new Vehicle(Bytes.toString(result.getRow()),
                            Bytes.toString(result.getValue(CF_INFO.getBytes(), qC)));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Vehicle save(final String tableName, final Vehicle vehicle) {
        return hbaseTemplate.execute(tableName, new TableCallback<Vehicle>() {
            public Vehicle doInTable(HTableInterface table) throws Throwable {
                Put p = new Put(Bytes.toBytes(vehicle.getRowkey()));
                p.add(CF_INFO.getBytes(), qC, Bytes.toBytes(vehicle.getC()));
                table.put(p);
                return vehicle;
            }
        });
    }
}
