package com.dyz.myBatis.dao;

import com.dyz.myBatis.model.NoticeTable;

public interface NoticeTableMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NoticeTable record);

    int insertSelective(NoticeTable record);

    NoticeTable selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(NoticeTable record);

    int updateByPrimaryKey(NoticeTable record);
    /**
     * 获取最近的一次公告
     * @return
     */
    NoticeTable selectRecentlyObject();
}