package com.dyz.gameserver.commons.initial;

import java.util.Properties;



public  abstract class Params {
	
	static Properties properties = AppCf.getProperties();
	//新玩家初始房卡数量
	public static final  Integer initialRoomCard = Integer.valueOf(properties.get("initialRoomCard").toString());
	//新玩家初始房卡数量
	public static final  Integer initialPrizeCount = Integer.valueOf(properties.get("initialPrizeCount").toString());
	
}
