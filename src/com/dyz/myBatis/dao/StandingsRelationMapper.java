package com.dyz.myBatis.dao;

import java.util.List;

import com.dyz.myBatis.model.StandingsRelation;

public interface StandingsRelationMapper {
    int deleteByPrimaryKey(Integer id);

    int save(StandingsRelation record);

    int saveSelective(StandingsRelation record);

    StandingsRelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StandingsRelation record);

    int updateByPrimaryKey(StandingsRelation record);
    
    List<Integer> selectDetailIdsByStandingsId(Integer standingsId);
}