package com.dyz.gameserver.msg.processor.host;

import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.manager.GameSessionManager;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.host.IndexInfosResponse;


/**
 * 后台管理员登录后，返回在线人数，注册用户数量，开放数量等信息
 * @author luck
 *
 */
public class IndexInfosProcessor extends MsgProcessor implements
INotAuthProcessor{

	@Override
	public void process(GameSession gameSession, ClientRequest request) throws Exception {
		
	}
	@Override
	public void handle(GameSession gameSession, ClientRequest request) {
			
			//2:当前在线房间总数
			int onlineRoomsCount = RoomManager.getInstance().getRoomsCount();
			//7:当前在线人数
			int onlineAccountCount = GameSessionManager.getInstance().sessionMap.size();
			
			String str = onlineRoomsCount+","+onlineAccountCount+","+GameSessionManager.topOnlineAccountCount;
			
			gameSession.sendMsg(new IndexInfosResponse(1,str));
		
	}
}
