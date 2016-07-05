package com.dyz.gameserver.msg.processor.createroom;

import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.createroom.CreateRoomResponse;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.JsonUtilTool;

/**
 * Created by kevin on 2016/6/21.
 */
public class CreateRoomMsgProcssor extends MsgProcessor implements
        INotAuthProcessor {

    public CreateRoomMsgProcssor(){

    }

    @Override
    public void process(GameSession gameSession, ClientRequest request) throws Exception {
        String message = request.getString();
        RoomVO roomVO = (RoomVO) JsonUtilTool.fromJson(message, RoomVO.class);
        if(gameSession.isLogin()) {
            Avatar avatar = gameSession.getRole(Avatar.class);
            if (avatar == null) {
                System.out.println("用户是空的，不能创建房间");
            }else{
                if(avatar.avatarVO.getAccount().getRoomcard() > 0) {
                    if(avatar.avatarVO.getRoomId() == 0) {
                        RoomManager.getInstance().createRoom(avatar,roomVO);
                        avatar.updateRoomCard(-1);
                        System.out.println("房间创建成功"+roomVO.getMa()+"--"+roomVO.getName()+"roomId:"+roomVO.getRoomId());
                        gameSession.sendMsg(new CreateRoomResponse(1,roomVO.getRoomId()+""));
                    }else{
                        System.out.println("你已经在房间里了，不能再创建房间");
                        gameSession.sendMsg(new CreateRoomResponse(0,ErrorCode.Error_000013));
                    }
                }else{
                    System.out.println("房间卡没有了");
                    gameSession.sendMsg(new CreateRoomResponse(0,ErrorCode.Error_000014));
                }
            }
        }else{
            System.out.println("该用户还没有登录");
            gameSession.destroy();
        }

    }
}
