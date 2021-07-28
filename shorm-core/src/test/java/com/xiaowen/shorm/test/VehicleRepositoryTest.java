package com.xiaowen.shorm.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author: xiaowen
 * @date: 2018/8/14
 * @since:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-dao.xml",
        "classpath:spring/spring-hbase.xml", "classpath:spring/spring-thread-pool.xml"})
public class VehicleRepositoryTest {
    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private ThreadPoolTaskExecutor taskExecutor;

    @Test
    public void get() {
        Vehicle vehicle = vehicleRepository.get("10000441_1529263119");
        System.out.println(vehicle.toString());
    }
    @Test
    public void findAll() {
        List<Vehicle> vehicles = vehicleRepository.findAll();
        System.out.printf(vehicles.toString());
    }

    @Test
    public void findByQuery() {
        Long start = System.currentTimeMillis();
        List<Vehicle> vehicles = vehicleRepository.findByQuery("10277975_152945385" , "10278974_152945485");
        System.out.printf("查询总时长：" + (System.currentTimeMillis() - start) + "; 车辆点位数：" + vehicles.size());

        start = System.currentTimeMillis();
        System.out.println("开始插入数据");
        for (Vehicle vehicle : vehicles) {
            vehicleRepository.save("vehicle_date:snappy_diff_location_report_xiaowen", vehicle);
        }
//        int count = 1000;
//        int loop = (vehicles.size() - 1) / count + 1;
//        int startIndex = 0;
//        int endIndex = 0;
//
//        for (int i = 0; i < loop; i++) {
//            startIndex = i * count;
//            if ((i + 1) * count > vehicles.size()) {
//                endIndex = vehicles.size();
//            } else {
//                endIndex = (i + 1) * count;
//            }
//
//            this.taskExecutor.execute(new SaveProcessor(vehicles.subList(startIndex, endIndex), vehicleRepository));
//        }
//        System.out.println("插入数据总时长：" + (System.currentTimeMillis() - start));
    }

    class SaveProcessor implements Runnable {

        private List<Vehicle> vehicles;

        private VehicleRepository vehicleRepository;

        private SaveProcessor(List<Vehicle> vehicles, VehicleRepository vehicleRepository) {
            this.vehicles = vehicles;
            this.vehicleRepository = vehicleRepository;
        }


        @Override
        public void run() {
            for (Vehicle vehicle : vehicles) {
                vehicleRepository.save("vehicle_date:snappy_diff_location_report_xiaowen", vehicle);
            }
        }
    }
}
