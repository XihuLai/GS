package com.dyz.myBatis.dao;

import com.dyz.myBatis.model.StandingsDetail;

public interface StandingsDetailMapper {
    int deleteByPrimaryKey(Integer id);

    int save(StandingsDetail record);

    int saveSelective(StandingsDetail record);

    StandingsDetail selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StandingsDetail record);

    int updateByPrimaryKey(StandingsDetail record);
}