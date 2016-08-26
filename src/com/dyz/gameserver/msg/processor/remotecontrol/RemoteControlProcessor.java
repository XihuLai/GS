package com.dyz.gameserver.msg.processor.remotecontrol;

import java.io.IOException;

import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;

public class RemoteControlProcessor extends MsgProcessor implements
INotAuthProcessor {

	@Override
	public void process(GameSession gameSession, ClientRequest request) throws Exception {
		
	}
	@Override
	public void handle(GameSession gameSession, ClientRequest request) {
		try {
			if(gameSession.getAddress().equals("IP地址")){
				String content = request.getString();    
				
			}
			else{
				System.out.println("非法请求");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
