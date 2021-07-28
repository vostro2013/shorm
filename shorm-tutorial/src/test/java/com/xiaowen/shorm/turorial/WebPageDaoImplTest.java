package com.xiaowen.shorm.turorial;

import com.xiaowen.shorm.turorial.dao.WebPageDao;
import com.xiaowen.shorm.turorial.dao.entity.WebPage;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * @author: xiaowen
 * @date: 2018/3/25
 * @since:
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:spring/spring-hbase.xml", "classpath:spring/spring-dao.xml"})
public class WebPageDaoImplTest {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebPageDaoImplTest.class);

    @Autowired
    private WebPageDao webPageDao;

    @Test
    public void get() {
        WebPage webPage = webPageDao.get("com.example/http");
        LOGGER.info(webPage.toString());
        Assert.assertNotNull(webPage);
    }
}