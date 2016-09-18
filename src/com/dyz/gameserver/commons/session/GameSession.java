package com.dyz.gameserver.commons.session;

import java.net.InetSocketAddress;
import java.net.SocketAddress;

import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ResponseMsg;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.response.offline.OffLineResponse;
import com.dyz.gameserver.sprite.base.GameObj;
import com.dyz.persist.util.TimeUitl;
/**
 * 游戏中的session回话，封装了mina的session
 * @author dyz
 *
 */
public class GameSession implements GameObj {
	/**
	 * IoSession
	 */
	private IoSession session;
	/**
	 * 用户的服务器地址
	 */
	private String address;
	/**
	 * 记录心跳时间， 间隔，到达一定时间就表示前端断线了
	 */
	private int time = 0;
	
	/**
	 *
	 */
	private Object role;
	
	private boolean isLogin=false;
	
	private static final AttributeKey KEY_PLAYER_SESSION = new AttributeKey(GameSession.class, "player.session");
	
	public GameSession(IoSession session){
		this.session = session;
		this.session.setAttribute(KEY_PLAYER_SESSION, this);
		SocketAddress socketaddress = session.getRemoteAddress();
		InetSocketAddress s = (InetSocketAddress) socketaddress;
		address = s.getAddress().getHostAddress();
		//存当前用户相关的服务器地址
		
	}

	/**
	 * 得到一个GameSession的实例化对象
	 * @param session
	 * @return
     */
	public static GameSession getInstance(IoSession session) {
		Object playerObj = session.getAttribute(KEY_PLAYER_SESSION);
		session.getService().getManagedSessions();
		return (GameSession) playerObj;
	}
	
	/**
	 * 发送消息给客户端
	 * @param msg
	 * @return
	 * @throws InterruptedException 
	 */
	public WriteFuture sendMsg(ResponseMsg msg)  {
		if (session == null || !session.isConnected() || session.isClosing()) {
			//system.out.println("session == "+session+" session.isConnected ==  "+session.isConnected()+" session.isClosing =  "+session.isClosing());
			return null;
		}
		return session.write(msg);
	}

	/**
	 *
	 * @return
     */
	public String getAddress(){
		return this.address;
	}

	/**
	 *
	 * @param isLogin
     */
	public  void setLogin(boolean isLogin){
		this.isLogin=isLogin;
	}

	/**
	 * 是否登录
	 * @return
     */
	public boolean isLogin(){
		return this.isLogin;
	}

	/**
	 * 保存角色信息
	 * @param obj
     */
	public void setRole(Object obj){
		this.role = obj;
	}
	
	@SuppressWarnings("unchecked")
	/**
	 * 得到角色信息
	 */
	public <T> T getRole(Class<T> t){
		return (T)this.role;
	}

	/**
	 * 关闭SESSION
	 */
	public void close(){
		if(session != null ) {
			session.close(false);// 2016-8-29短线时不关闭用户session，等
			Avatar avatar = getRole(Avatar.class);
			if(avatar != null){
				GameSessionManager.getInstance().removeGameSession(avatar);
				avatar.avatarVO.setIsOnLine(false);
				//把用户数据保留半个小时
				TimeUitl.delayDestroy(avatar,60*30*1000);
				if(avatar.avatarVO.getRoomId() != 0){
					RoomLogic roomLogic =RoomManager.getInstance().getRoom(avatar.avatarVO.getRoomId());
					if(roomLogic != null ){
						if(avatar.getRoomVO().getPlayerList().size() >= 2){
							//房间还有其他玩家，则向其他玩家发送离线玩家消息  
							for (Avatar ava :roomLogic.getPlayerList()) {
								if(avatar.getUuId() != ava.getUuId()){
									//发送离线通知
									ava.getSession().sendMsg(new OffLineResponse(1,avatar.getUuId()+""));
									//同意解散房间人数 设置为0,有人掉线就取消解散房间
									roomLogic.setDissolveCount(1);
									roomLogic.setDissolve(true);
								}
							}
						}
					}
				}
			}
		}
	}
    /**
     * 用户断线超过30秒后  断开session，至于用户信息.......
     */
	public void clearAllInfo(){
		session.close(false);
		
	}
	
	
	@Override
	public void destroyObj() {
		close();
	}

	public int getTime() {
		return time;
	}

	public void addTime(int i) {
		if(i == 0){
			this.time = i;
		}
		else{
			this.time = this.time + i;
		}
	}
	
}
