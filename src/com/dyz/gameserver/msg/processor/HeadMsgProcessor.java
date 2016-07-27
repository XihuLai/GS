package com.dyz.gameserver.msg.processor;

import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.HeadResponse;

/**
 * Created by kevin on 2016/7/26.
 */
public class HeadMsgProcessor extends MsgProcessor implements
        INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        String index = request.getString();
        //for(int i=0;i<10;i++) {
            gameSession.sendMsg(new HeadResponse(1, index+""));
        //}
        System.out.println("revice = "+index);
    }
}
