package com.dyz.gameserver.net;

import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.bootstrap.GameServer;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.persist.util.TimeUitl;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MinaMsgHandler extends IoHandlerAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(MinaMsgHandler.class);
	
	@Override
	public void sessionCreated(IoSession session) throws Exception {
		new GameSession(session);
		logger.info("a session create from ip {}",session.getRemoteAddress());
	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		ClientRequest clientRequest = (ClientRequest) message;
		GameSession gameSession = GameSession.getInstance(session);
		if (gameSession == null) {
			return;
		}
		
		GameServer.msgDispatcher.dispatchMsg(gameSession,clientRequest);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		//强制退出
		logger.error("服务器出错 {}",cause.getMessage());
		//
		//cause.printStackTrace();
	}

	/**
	 * 关闭SESSION
	 * @param session
	 * @throws Exception
     */
	@Override
	public void sessionClosed(IoSession session) throws Exception {
		logger.info("a session closed ip:{}",session.getRemoteAddress());
		GameSession gameSession = GameSession.getInstance(session);
		if(gameSession!=null){
			Avatar avatar = gameSession.getRole(Avatar.class);
			//Session关闭的时候检测用户是否为null，如果不为null，消除用户数据
			if(avatar!=null){
				//avatar.destroy();因为有断线重联功能，所以在这里不能清除角色数据
				//把用户从在线列表中移除，并加入到离线列表
				GameServerContext.remove_onLine_Character(avatar);
				GameServerContext.add_offLine_Character(avatar);
				avatar.avatarVO.setIsOnLine(false);;
				//把session从GameSessionManager移除
				GameSessionManager.getInstance().removeGameSession(avatar);
				//把用户数据保留半个小时
				TimeUitl.delayDestroy(avatar,60*30*1000);
			}
		}

	}
	
	
}
