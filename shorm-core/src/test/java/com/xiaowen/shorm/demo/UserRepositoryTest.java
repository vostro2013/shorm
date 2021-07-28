package com.xiaowen.shorm.demo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 * @author: xiaowen
 * @date: 2018/3/25
 * @since:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-dao.xml", "classpath:spring/spring-hbase.xml"})
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void findAll() {
        List<User> users = userRepository.findAll();
        System.out.printf(users.toString());
    }
}
