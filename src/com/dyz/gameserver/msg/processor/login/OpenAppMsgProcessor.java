package com.dyz.gameserver.msg.processor.login;

import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.login.OpenAppResponse;

/**
 * 
 *用户打开应用时，做一些初始化操作
 * @author  daiyongzhi
 * @date 2015年1月28日 下午1:15:21
 * @version V1.0
 */
public class OpenAppMsgProcessor extends MsgProcessor implements INotAuthProcessor{

	@Override
	public void process(GameSession gameSession, ClientRequest request)
			throws Exception {
		gameSession.sendMsg(new OpenAppResponse(1,"welecome !"));
		
		
	}

}
