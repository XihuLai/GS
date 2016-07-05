package com.dyz.gameserver.commons.message;

import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessorRegister;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

/**
 * 消息分发器，根据消息号，找到相应的消息处理器
 * @author kevin
 *
 */
public class MsgDispatcher {

	private static final Logger logger = LoggerFactory.getLogger(MsgDispatcher.class);
	
	private Map<Integer, MsgProcessor> processorsMap = new HashMap<Integer, MsgProcessor>();
	
	public MsgDispatcher(){
		for(MsgProcessorRegister register :MsgProcessorRegister.values()){
			processorsMap.put(register.getMsgCode(), register.getMsgProcessor());
		}
		logger.info("初始化 消息处理器成功。。。");
	}

	/**
	 * 通过协议号得到MsgProcessor
	 * @param msgCode
	 * @return
     */
	public MsgProcessor getMsgProcessor(int msgCode){
		return processorsMap.get(msgCode);
	}

	/**
	 * 派发消息协议
	 * @param gameSession
	 * @param clientRequest
     */
	public void dispatchMsg( GameSession gameSession,ClientRequest clientRequest) {
		
		int msgCode = clientRequest.getMsgCode();
		if(msgCode == 1000){//客户端请求断开链接
			gameSession.close();
		}
		//if(msgCode%2==0){//请求协议号必须是奇数
		//	return;
		//}
		MsgProcessor processor = getMsgProcessor(msgCode);
		if(gameSession.isLogin() || processor instanceof INotAuthProcessor){
			processor.handle(gameSession, clientRequest);
		}
		
	}

}
