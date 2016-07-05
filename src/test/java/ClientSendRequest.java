package test.java;

import com.dyz.gameserver.commons.message.MsgBodyWrap;
import com.dyz.gameserver.net.codec.MsgProtocol;
import org.apache.mina.core.buffer.IoBuffer;

/**
 * Created by kevin on 2016/6/23.
 */
public class ClientSendRequest{
    /**
     * 必须调用此方法设置消息号
     *
     * @param msgCode
     */
    public ClientSendRequest(int msgCode) {
        setMsgCode(msgCode);
    }

    protected MsgBodyWrap output = MsgBodyWrap.newInstance4Out();
    private int msgCode;
    /**必须调用此方法设置消息号*/


    public void setMsgCode(int code) {
        msgCode = code;
    }

    public IoBuffer entireMsg() {

        byte[] body = output.toByteArray();
			/* 标志 byte 长度short */
        int length = MsgProtocol.flagSize+MsgProtocol.lengthSize+MsgProtocol.msgCodeSize+ body.length;
        IoBuffer buf = IoBuffer.allocate(length);
        buf.put(MsgProtocol.defaultFlag);//flag
        buf.putInt(body.length+MsgProtocol.msgCodeSize);//lengh
        buf.putInt(msgCode);
        buf.put(body);
        buf.flip();
        return buf;
    }

    /**
     * 释放资源(数据流、对象引用)
     */
    public void release() {
        if (output != null) {
            output.close();
        }
        output = null;
    }

}
