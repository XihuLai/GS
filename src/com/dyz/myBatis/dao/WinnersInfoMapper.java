package com.dyz.myBatis.dao;

import java.util.List;

import com.dyz.myBatis.model.WinnersInfo;

/**
 * 抽奖信息记录
 * @author luck
 *
 */
public interface WinnersInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int save(WinnersInfo record);

    int saveSelective(WinnersInfo record);

    WinnersInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(WinnersInfo record);

    int updateByPrimaryKey(WinnersInfo record);
    /**
     * 有状态则根据状态赛选出中奖人信息，status为空则选出全部中奖信息
     * @param status
     * @return
     */
    List<WinnersInfo> selectWinnersInfoByMap(String status);
}