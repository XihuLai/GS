package com.dyz.myBatis.dao;

import java.util.List;

import com.dyz.myBatis.model.StandingsAccountRelation;

public interface StandingsAccountRelationMapper {
    int deleteByPrimaryKey(Integer id);

    int save(StandingsAccountRelation record);

    int saveSelective(StandingsAccountRelation record);

    StandingsAccountRelation selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(StandingsAccountRelation record);

    int updateByPrimaryKey(StandingsAccountRelation record);
    
    List<Integer> selectNearestStandingsIdByAccountId(Integer accountId);
}