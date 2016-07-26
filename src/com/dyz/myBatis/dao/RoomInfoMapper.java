package com.dyz.myBatis.dao;

import com.dyz.myBatis.model.RoomInfo;

public interface RoomInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoomInfo record);

    int insertSelective(RoomInfo record);

    RoomInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RoomInfo record);

    int updateByPrimaryKey(RoomInfo record);
}