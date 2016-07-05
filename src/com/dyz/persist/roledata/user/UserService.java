package com.dyz.persist.roledata.user;

import com.dyz.persist.util.DBUtil;

import java.sql.SQLException;
import java.util.List;

public class UserService {

	private final UserDAO userDao = new UserDAOImpl(DBUtil.getRoledataSqlMapClient());
	
	private static UserService userService = new UserService();
	
	private UserService(){}
	
	public static UserService getInstance(){
		return userService;
	}
	
	public void insertUser(User user) throws SQLException{
		userDao.insertForId(user);
	}
	
	public User selectUser(String phone,String passwd) throws SQLException{
		UserExample userExample = new UserExample();
		userExample.createCriteria().andPhonenumberEqualTo(phone).andPasswdEqualTo(passwd);
		List<User> users = userDao.selectByExample(userExample);
		if(users!=null && users.size()>0){
			return users.get(0);
		}else{
			return null;
		}
	}
	
	
}
