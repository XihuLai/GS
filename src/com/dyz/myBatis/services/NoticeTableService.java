package com.dyz.myBatis.services;

import java.sql.SQLException;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.NoticeTableMapper;
import com.dyz.myBatis.daoImp.NoitceTableDaoImp;
import com.dyz.myBatis.model.NoticeTable;

/**
 * Created by kevin on 2016/6/21.
 */
public class NoticeTableService {
    private NoticeTableMapper noticeTableMapper;

    private static NoticeTableService noticeTableService = new NoticeTableService();

    public static NoticeTableService getInstance(){
        return noticeTableService;
    }

    public void initSetSession(SqlSessionFactory sqlSessionFactory){
        noticeTableMapper = new NoitceTableDaoImp(sqlSessionFactory);
    }

    /**
     *
     * @param account
     * @throws SQLException
     */
    public void updateAccount(NoticeTable noticeTable) {
        try {
            int index = noticeTableMapper.updateByPrimaryKey(noticeTable);
            System.out.println("===index====> "+index);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    /**
     *
     * @param account
     * @throws SQLException
     */
    public void updateByPrimaryKeySelective(NoticeTable noticeTable){
        try{
            int index = noticeTableMapper.updateByPrimaryKeySelective(noticeTable);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    
    /**
     * 获取最近一次公告
     */
    public NoticeTable selectRecentlyObject(){
    	NoticeTable noticeTable = null;
    	 try{
    		 noticeTable = noticeTableMapper.selectRecentlyObject();
         }catch (Exception e){
             System.out.println(e.getMessage());
         }
    	 return noticeTable;
    }
}
