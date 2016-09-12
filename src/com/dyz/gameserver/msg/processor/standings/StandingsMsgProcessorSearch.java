package com.dyz.gameserver.msg.processor.standings;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.standings.StandingsDetailResponse;
import com.dyz.gameserver.msg.response.standings.StandingsDetailResponseSearch;
import com.dyz.gameserver.msg.response.standings.StandingsResponse;
import com.dyz.myBatis.model.RoomInfo;
import com.dyz.myBatis.model.Standings;
import com.dyz.myBatis.model.StandingsAccountRelation;
import com.dyz.myBatis.model.StandingsDetail;
import com.dyz.myBatis.services.RoomInfoService;
import com.dyz.myBatis.services.StandingsAccountRelationService;
import com.dyz.myBatis.services.StandingsDetailService;
import com.dyz.myBatis.services.StandingsRelationService;
import com.dyz.myBatis.services.StandingsService;
import com.dyz.persist.util.DateUtil;
import com.dyz.persist.util.GlobalUtil;
import com.dyz.persist.util.StringUtil;


/**
 * 通过roomid返回战绩
 * @author luck
 *
 */
public class StandingsMsgProcessorSearch extends MsgProcessor implements
INotAuthProcessor {

	@Override
	public void process(GameSession gameSession, ClientRequest request) throws Exception {
	       if(GlobalUtil.checkIsLogin(gameSession)) {
	    	   String roomId = request.getString();
	            Avatar avatar = gameSession.getRole(Avatar.class);
	            if (avatar == null) {
	                //system.out.println("用户是空的");
	            }else{
	            	int id = 0;
	            	JSONArray  array;
//	            	if(roomId.equals("0")){
//	            		array = new JSONArray();
//	            		//房间战绩
//	            		int accountId = avatar.avatarVO.getAccount().getId();
//	            		ids = StandingsAccountRelationService.getInstance().selectNearestStandingsIdByAccountId(accountId);
//	            		Standings standings;
//	            		for (Integer i : ids) {
//	            			json =new JSONObject();
//	            			standings = StandingsService.getInstance().selectByPrimaryKey(i);
//	            			json.put("roomId",RoomInfoService.getInstance().selectByPrimaryKey(standings.getRoomid()).getRoomid());
//	            			json.put("data", standings);
//	            			array.add(json);
//						}
//	            		gameSession.sendMsg(new StandingsResponse(1, array.toJSONString()));
//	            	}
//	            	else{
	            		//每一局战绩(id为standingsId)
	            		Standings standings;
	            		array = new JSONArray();
	            		JSONObject json = new JSONObject();;
	            		id = RoomInfoService.getInstance().selectRoomId(Integer.parseInt(roomId)).getId();
	            		standings = StandingsService.getInstance().selectByRoomId(id);
	            		json.put("data", standings);
	            		json.put("roomId",Integer.parseInt(roomId));
	            		array.add(json);
	            		gameSession.sendMsg(new StandingsDetailResponseSearch(1, array.toJSONString()));
//	            	}
	            }
	        }else{
	            //system.out.println("该用户还没有登录");
	            gameSession.destroyObj();
	        }
	}
}
