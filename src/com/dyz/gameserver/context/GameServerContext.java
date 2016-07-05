package com.dyz.gameserver.context;

import com.dyz.gameserver.Avatar;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 管理整个服上的玩家
 *
 * @author  kevin
 * @date 2015年1月30日 下午5:36:54
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
	public static void add_onLine_Character(Avatar avatar){
		ALL_ONLINE_PLAYER.put(avatar.getUuId(), avatar);
	}

	/**
	 * 把用户从在线hashmap中删除
	 * @param avatar
	 */
	public static void remove_onLine_Character(Avatar avatar){
		ALL_ONLINE_PLAYER.remove(avatar.getUuId());
	}

	/**
	 * 把用户添加到掉线hashMap中
	 * Character character
	 * @param avatar
	 */
	public static void add_offLine_Character(Avatar avatar){
		ALL_OFFLINE_PLAYER.put(avatar.getUuId(), avatar);
	}

	/**
	 *
	 * @param uuid
	 * @return avatar
     */
	public static Avatar getAvatarFromOff(int uuid){
		Avatar avatar = null;
		avatar = ALL_OFFLINE_PLAYER.get(uuid);
		return avatar;
	}

	/**
	 * 把用户从掉线hashmap中删除
	 * @param avatar
	 */
	public static void remove_offLine_Character(Avatar avatar){
		ALL_OFFLINE_PLAYER.remove(avatar.getUuId());
	}
}
