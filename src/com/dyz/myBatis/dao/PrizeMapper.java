package com.dyz.myBatis.dao;

import java.util.List;

import com.dyz.myBatis.model.Prize;

public interface PrizeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Prize record);

    int insertSelective(Prize record);

    Prize selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Prize record);

    int updateByPrimaryKey(Prize record);
    
    /**
     * 获取所有的奖品信息
     * @return
     */
    List<Prize> selectAllPrizes();
}