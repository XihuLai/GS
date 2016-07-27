package com.dyz.gameserver.net.codec;

import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.dyz.gameserver.commons.message.ResponseMsg;

public class GameMsgEncoder extends ProtocolEncoderAdapter {
	private static final Logger logger = LoggerFactory.getLogger(GameMsgEncoder.class);
	public GameMsgEncoder() {
	}

	// 在此处实现对ResponseMsg的编码工作，并把它写入输出流中
	public void encode(IoSession session, Object message,
			ProtocolEncoderOutput out) throws Exception {
			ResponseMsg value = (ResponseMsg) message;
			
		IoBuffer io = value.entireMsg();
//	    System.out.println("io长度"+io.getLong()+"-----getHexDump"+io.getHexDump());
//	    System.out.println(io.toString());
		out.write(value.entireMsg());
		out.flush();
		value.release();
		//logger.info("服务端发送给客户端信息加码");
	}

	public void dispose() throws Exception {
	}
}