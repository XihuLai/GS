package com.dyz.gameserver.net;

import com.dyz.gameserver.bootstrap.GameServer;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 游戏和后台之间的传入
 * @author luck
 *
 */
public class MinaHostMsgHandler extends IoHandlerAdapter{
	
	private static final Logger logger = LoggerFactory.getLogger(MinaHostMsgHandler.class);
//	@Override
//	public void sessionCreated(IoSession session) throws Exception {
//
//	}
	@Override
	public void sessionOpened(IoSession session) throws Exception{
		new GameSession(session);
		logger.info("a session create from ip {}",session.getRemoteAddress());

	}
	
	@Override
	public void messageReceived(IoSession session, Object message)
			throws Exception {
		ClientRequest clientRequest = (ClientRequest) message;
		GameSession gameSession = GameSession.getInstance(session);
		if (gameSession == null) {
			logger.info("gameSession == null");
			return;
		}
		
		GameServer.msgDispatcher.dispatchMsg(gameSession,clientRequest);
	}
	
	@Override
	public void exceptionCaught(IoSession session, Throwable cause)
			throws Exception {
		//强制退出
		logger.error("服务器出错 {}",cause.getMessage());
		cause.printStackTrace();
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
		/*if(gameSession!=null){
			gameSession.close();
		}*/

	}
	
	
}
