package com.dyz.gameserver.msg.processor.heartbeat;


import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;

/**
 * 心跳协议
 * 
 */
public class HeadMsgProcessor extends MsgProcessor implements
        INotAuthProcessor  {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
    	//放入时间工具里面   当gamesession里面的time为10时，自动断线该用户
    	gameSession.addTime(0);
    }
}
