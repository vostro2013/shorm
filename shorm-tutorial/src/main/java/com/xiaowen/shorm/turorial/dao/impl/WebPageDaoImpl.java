package com.xiaowen.shorm.turorial.dao.impl;

import com.xiaowen.shorm.HBaseDaoImpl;
import com.xiaowen.shorm.turorial.dao.WebPageDao;
import com.xiaowen.shorm.turorial.dao.entity.WebPage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.hadoop.hbase.HbaseTemplate;
import org.springframework.stereotype.Repository;

/**
 * @author: xiaowen
 * @date: 2018/3/25
 * @since:
 */
@Repository
public class WebPageDaoImpl extends HBaseDaoImpl<String, WebPage> implements WebPageDao {
    private static final Logger LOGGER = LoggerFactory.getLogger(WebPageDaoImpl.class);

    public WebPageDaoImpl() {
        super(String.class, WebPage.class);
    }
}
