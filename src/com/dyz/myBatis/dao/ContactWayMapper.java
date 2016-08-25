package com.dyz.myBatis.dao;

import com.dyz.myBatis.model.ContactWay;

public interface ContactWayMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(ContactWay record);

    int insertSelective(ContactWay record);

    ContactWay selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(ContactWay record);

    int updateByPrimaryKey(ContactWay record);
}