package com.dyz.gameserver.context;

import com.dyz.gameserver.Avatar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理整个服上的玩家/创建房间数量
 *1，查看当前在线人数，当天在线人数最高数记录，各个游戏在线人数。
 *2，当天创建房间总数，当前在玩的创建的房间总数，各个游戏当前创建房间数，各个游戏当天创建的房间总数。
 *4. 查看玩家的当前信息（是否在线，IP，所在游戏，房间号，房卡余额，总消耗房卡数）。
 * @author  kevin
 * @version V1.0
 */
public class GameServerContext {
	/**
	 * 所有在线的玩家
	 */
	private static Map<Integer,Avatar> ALL_ONLINE_PLAYER = new ConcurrentHashMap<Integer, Avatar>();
	/**
	 * 所有掉线的玩家
	 */
	private static Map<Integer,Avatar> ALL_OFFLINE_PLAYER = new ConcurrentHashMap<Integer, Avatar>();
	/**
	 * 把用户添加到在线hashMap中
	 * Character character
	 * @param avatar
	 */
	public static  void add_onLine_Character(Avatar avatar){
		ALL_ONLINE_PLAYER.put(avatar.getUuId(), avatar);
	}

	/**
	 * 把用户从在线hashmap中删除
	 * @param avatar
	 */
	public static  void remove_onLine_Character(Avatar avatar){
		ALL_ONLINE_PLAYER.remove(avatar.getUuId());
	}

	/**
	 * 把用户添加到掉线hashMap中,同时移除在线map中
	 * Character character
	 * @param avatar
	 */
	public static  void add_offLine_Character(Avatar avatar){
		ALL_OFFLINE_PLAYER.put(avatar.getUuId(), avatar);
		ALL_ONLINE_PLAYER.remove(avatar.getUuId());
	}

	/**
	 *从离线列表中得到用户
	 * @param uuid
	 * @return avatar
     */
	public static  Avatar getAvatarFromOff(int uuid){
		Avatar avatar = ALL_OFFLINE_PLAYER.get(uuid);
		return avatar;
	}
	/**
	 * 从在线列表中得到用户
	 * @param uuid
	 * @return avatar
	 */
	public static   Avatar getAvatarFromOn(int uuid){
		Avatar avatar = ALL_ONLINE_PLAYER.get(uuid);
		return avatar;
	}
	/**
	 * 把用户从掉线hashmap中删除
	 * @param avatar
	 */
	public static  void remove_offLine_Character(Avatar avatar){
		ALL_OFFLINE_PLAYER.remove(avatar.getUuId());
	}

}
