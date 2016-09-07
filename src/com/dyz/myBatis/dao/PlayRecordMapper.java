package com.dyz.myBatis.dao;

import com.dyz.myBatis.model.PlayRecord;

public interface PlayRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int save(PlayRecord record);

    int saveSelective(PlayRecord record);

    PlayRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PlayRecord record);

    int updateByPrimaryKeyWithBLOBs(PlayRecord record);

    int updateByPrimaryKey(PlayRecord record);
    
    PlayRecord selectByStandingsDetailId(Integer id);
}