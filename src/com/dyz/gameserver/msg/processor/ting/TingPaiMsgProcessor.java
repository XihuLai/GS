package com.dyz.gameserver.msg.processor.ting;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.pojo.RoomVO;

import net.sf.json.JSONObject;

/**
 * Created by Westlake on 17/1/6.
 */

public class TingPaiMsgProcessor extends MsgProcessor implements
        INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
        Avatar p = gameSession.getRole(Avatar.class);
        if(p != null && roomLogic != null){
            JSONObject json = JSONObject.fromObject(request.getString());
            boolean bt = (boolean)json.get("ting");
            roomLogic.ting(p, bt);
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000023));
        }

    }
}
