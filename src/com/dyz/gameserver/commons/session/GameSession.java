package com.dyz.gameserver.commons.session;

import com.alibaba.fastjson.JSONObject;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ResponseMsg;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.response.offline.OffLineResponse;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.gameserver.sprite.base.GameObj;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.session.AttributeKey;
import org.apache.mina.core.session.IoSession;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
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
			System.out.println("session == "+session+" session.isConnected ==  "+session.isConnected()+" session.isClosing =  "+session.isClosing());
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
		System.out.println("关闭SESSION -- > "+session.getRemoteAddress());
		if(session != null) {
			//关闭session的时候(掉线) 如果只其一个用户还在房间，则踢出用户并解散房间，并向其他玩家发送消息
			GameSession playerObj = (GameSession) session.getAttribute(KEY_PLAYER_SESSION);
			Avatar avatar = playerObj.getRole(Avatar.class);
			RoomLogic roomLogic =RoomManager.getInstance().getRoom(avatar.avatarVO.getRoomId());
			if(avatar.getRoomVO().getPlayerList().size() >= 2){
				//房间还有其他玩家，则向其他玩家发送离线玩家消息  
				for (Avatar ava :roomLogic.getPlayerList()) {
					if(avatar.getUuId() != ava.getUuId()){
						//发送离线通知
						ava.getSession().sendMsg(new OffLineResponse(1,avatar.getUuId()+""));
					}
				}
			}
			else{
				RoomManager.getInstance().destroyRoom(avatar.getRoomVO());
				//房间只有一个人且掉线，则踢出用户并解散房间
				avatar.avatarVO.setRoomId(0);
				avatar.setRoomVO(new RoomVO());
				avatar.destroyObj();
				roomLogic = null;
			}
			session.close(false);
			System.out.println("关闭SESSION -- >  session.close(false);");
		}
	}

	@Override
	public void destroyObj() {
		close();
	}
}
