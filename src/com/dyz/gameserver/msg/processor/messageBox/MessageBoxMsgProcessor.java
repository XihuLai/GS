package com.dyz.gameserver.msg.processor.messageBox;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.logic.RoomLogic;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.messageBox.MessageBoxResponse;

import java.util.List;

/**
 * Created by kevin on 2016/8/2.
 */
public class MessageBoxMsgProcessor extends MsgProcessor implements INotAuthProcessor {
    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        String code = request.getString();
        RoomLogic roomLogic = RoomManager.getInstance().getRoom(gameSession.getRole(Avatar.class).getRoomVO().getRoomId());
        if(roomLogic != null){
            int selfUuid = gameSession.getRole(Avatar.class).getUuId();
            List<Avatar> playerList = roomLogic.getPlayerList();
            if(playerList != null){
                for(int i=0;i<playerList.size();i++){
                    if(playerList.get(i).getUuId() != selfUuid){
                        playerList.get(i).getSession().sendMsg(new MessageBoxResponse(code));
                    }
                }
            }
        }else{
            gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000005));
        }

    }
}
