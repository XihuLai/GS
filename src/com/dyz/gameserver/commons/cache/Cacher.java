package com.dyz.gameserver.commons.cache;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author luck
 *缓存后台管理系统需要的各类信息
 */
public class Cacher {

	private static final Logger logger = LoggerFactory.getLogger(Cacher.class);
	
	private  int gamerCount;//统计登录人数
	
	//private  Map<Integer,String> map = new HashMap<Integer,String>();
	
	private static Cacher cacher;
	
	private Cacher(){
	}
	
	public static Cacher getInstance(){
		if(cacher == null){
			cacher = new Cacher();
		}
		return cacher;
	}

	public synchronized void addGamerCount(){
		if(gamerCount >= 0){
			gamerCount = gamerCount +1;
		}
		else{
			logger.info("在线人数增加---有误");
		}
	}
	public synchronized void reduceGamerCount(){
		if((gamerCount - 1) >= 0 ){
			gamerCount = gamerCount - 1;
		}
		else{
			logger.info("在线人数减---有误");
			gamerCount = 0;
		}
	}
	
	public int getGamerCount(){
		return gamerCount;
	}
}
