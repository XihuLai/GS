package com.weipai.mapper;

import com.dyz.gameserver.gamerecord.GameRecord;

public interface GameRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GameRecord record);

    int insertSelective(GameRecord record);

    GameRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GameRecord record);

    int updateByPrimaryKey(GameRecord record);
}