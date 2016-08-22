package com.dyz.myBatis.dao;

import com.dyz.myBatis.model.PrizeRule;

public interface PrizeRuleMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PrizeRule record);

    int insertSelective(PrizeRule record);

    PrizeRule selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PrizeRule record);

    int updateByPrimaryKey(PrizeRule record);
}