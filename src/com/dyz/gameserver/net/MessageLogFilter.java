package com.dyz.gameserver.net;

import org.apache.log4j.Logger;
import org.apache.mina.core.filterchain.IoFilter.NextFilter;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.write.WriteRequest;
import org.apache.mina.filter.logging.LoggingFilter;

import com.dyz.gameserver.commons.message.ClientRequest;

public class MessageLogFilter extends LoggingFilter {
	private Logger logger = Logger.getLogger(this.getClass());
	@Override
    public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		nextFilter.messageReceived(session, message);
		ClientRequest request = (ClientRequest)message;//
//		if(request.getMsgCode()!=48)
//		logger.info(request.getMsgCode()+"消息内容为====="+new String(request.getContent()));

    }

    @Override
    public void messageSent(NextFilter nextFilter, IoSession session, WriteRequest writeRequest) throws Exception {
    	nextFilter.messageSent(session, writeRequest);
    	if(!writeRequest.getOriginalRequest().getMessage().getClass().toString().contains("HeadResponse"))
    	logger.info(writeRequest.getOriginalRequest().getMessage().getClass()+"有消息发送"+writeRequest.getOriginalRequest().getMessage());
        
    }
    @Override
    public void sessionIdle(NextFilter nextFilter, IoSession session, IdleStatus status) throws Exception {
        
    }
}
