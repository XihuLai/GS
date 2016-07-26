package com.dyz.myBatis.dao;

import com.dyz.myBatis.model.GameAccountIndex;;

public interface GameAccountIndexMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(GameAccountIndex record);

    int insertSelective(GameAccountIndex record);

    GameAccountIndex selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(GameAccountIndex record);

    int updateByPrimaryKey(GameAccountIndex record);
}