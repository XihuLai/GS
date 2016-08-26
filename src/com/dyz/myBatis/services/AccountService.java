package com.dyz.myBatis.services;

import java.sql.SQLException;
import java.util.List;

import org.apache.ibatis.session.SqlSessionFactory;

import com.dyz.myBatis.dao.AccountMapper;
import com.dyz.myBatis.daoImp.AccountDaoImp;
import com.dyz.myBatis.model.Account;
import com.dyz.myBatis.model.AccountExample;

/**
 * Created by kevin on 2016/6/21.
 */
public class AccountService {
    private AccountMapper accMap;

    private static AccountService accountService = new AccountService();

    public static AccountService getInstance(){
        return accountService;
    }

    public void initSetSession(SqlSessionFactory sqlSessionFactory){
        accMap = new AccountDaoImp(sqlSessionFactory);
    }

    /**
     *
     * @param account
     * @throws SQLException
     */
    /*public void updateAccount(Account account) {
        try {
            int index = accMap.updateByPrimaryKey(account);
            System.out.println("===index====> "+index);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }*/
    /**
     *
     * @param account
     * @throws SQLException
     */
    public void updateByPrimaryKeySelective(Account account){
        try{
            int index = accMap.updateByPrimaryKeySelective(account);
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    /**
     * 创建新用户
     * @param account
     * @return 插入信息表中id
     * @throws SQLException
     */
    public int createAccount(Account account) throws SQLException{
        int index = accMap.insertSelective(account);
        System.out.println("-account insert index->>" + index);
        return index;
    }

    public Account selectAccount(String openId) {
        AccountExample accountExample = new AccountExample();
        accountExample.createCriteria().andOpenidEqualTo(openId);
        try {
            List<Account> accounts = accMap.selectByExample(accountExample);
            if(accounts!=null && accounts.size()>0){
                return accounts.get(0);
            }else{
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    public List<Account> selectAll(){
        List<Account> accounts = null;
        try {
            accounts = accMap.selectAllAccounts();
        }catch (Exception e){
            e.printStackTrace();
        }
        return accounts;
    }

    /**
     * 创建新用户
     * @param
     * @return 插入信息表中id
     * @throws SQLException
     */
    public int selectMaxId() throws SQLException{
        int index = accMap.selectMaxId();
        System.out.println("-account selectMaxId index->>" + index);
        return index;
    }
     
    public Account selectByPrimaryKey(Integer id){
    	
    	return accMap.selectByPrimaryKey(id);
    }
    
    /**
     * 获取所有前一天玩过游戏的玩家
     */
    public  List<Account> selectIsGames(){
    	
		return accMap.selectIsGames();
    }
    /**
     * 获取所有游戏的玩家
     */
    public  List<Account> selectAllAccounts(){
    	
		return accMap.selectAllAccounts();
    }
    
    public int updatePrizeCount(Integer prizecount){
    	
    	return accMap.updatePrizeCount(prizecount);
    }
}
