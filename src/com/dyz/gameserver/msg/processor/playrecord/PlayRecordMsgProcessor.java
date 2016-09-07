package com.dyz.gameserver.msg.processor.playrecord;

import com.context.ErrorCode;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.playrecord.playrecordResponse;
import com.dyz.myBatis.model.PlayRecord;
import com.dyz.myBatis.services.PlayRecordService;
import com.dyz.persist.util.StringUtil;

/**
 * 获取游戏回放
 * @author luck
 *
 */
public class PlayRecordMsgProcessor  extends MsgProcessor implements
INotAuthProcessor{

	@Override
	public void process(GameSession gameSession, ClientRequest request) throws Exception {
          String id = request.getString();
          if(StringUtil.isInteger(id,0,0)){
        	  PlayRecord playRecord = PlayRecordService.getInstance().selectByStandingsDetailId(Integer.parseInt(id));
        	  gameSession.sendMsg(new playrecordResponse(1, playRecord.getPlayrecord()));
          }
          else{
        	  gameSession.sendMsg(new ErrorResponse(ErrorCode.Error_000019));
          }
	}
	
}
