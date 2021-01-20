package com.cheetah.community.dao.impl;

import com.cheetah.community.dao.TestDao;
import org.springframework.stereotype.Repository;

@Repository
public class TestDaoImpl implements TestDao {
    @Override
    public String select() {
        return "select test";
    }
}
