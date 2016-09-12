package com.dyz.myBatis.dao;

import java.util.Date;

import com.dyz.myBatis.model.RoomInfo;

public interface RoomInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RoomInfo record);

    int insertSelective(RoomInfo record);

    RoomInfo selectByPrimaryKey(Integer id);
    
    RoomInfo selectRoomId(Integer roomid);

    int updateByPrimaryKeySelective(RoomInfo record);

    int updateByPrimaryKey(RoomInfo record);
    
    int selectCount();
    
    int selectTodayCount(Date date);
}