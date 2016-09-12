package com.dyz.myBatis.dao;

import com.dyz.myBatis.model.RoomInfo;
import com.dyz.myBatis.model.Standings;

public interface StandingsMapper {
    int deleteByPrimaryKey(Integer id);

    int save(Standings record);

    int saveSelective(Standings record);

    Standings selectByPrimaryKey(Integer id);

    Standings selectByRoomId(Integer roomId);

    int updateByPrimaryKeySelective(Standings record);

    int updateByPrimaryKey(Standings record);
}