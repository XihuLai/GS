package com.dyz.gameserver.msg.processor.draw;

import com.alibaba.fastjson.JSONObject;
import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ClientRequest;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.msg.processor.common.INotAuthProcessor;
import com.dyz.gameserver.msg.processor.common.MsgProcessor;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.draw.DrawResponse;
import com.dyz.myBatis.model.Prize;
import com.dyz.myBatis.services.AccountService;
import com.dyz.myBatis.services.PrizeService;
import com.dyz.persist.util.GlobalUtil;
import com.dyz.persist.util.PrizeProbability;

import java.util.List;

/**
 * 抽奖处理类
 * @author luck
 *
 */
public class DrawProcessor extends MsgProcessor implements
INotAuthProcessor  {

	@Override
	public void process(GameSession gameSession, ClientRequest request) throws Exception {
		if(GlobalUtil.checkIsLogin(gameSession)) {
			String type = request.getString();//传入类型 0：获取所有奖品信息    1：获取随机获得奖品id    
			Avatar avatar = gameSession.getRole(Avatar.class);
			if (avatar != null) {
				if(type.equals("0")){
					//返回获取所有奖品信息
					prizesInformation(gameSession);
				}
				else if(type.equals("1")){
					//随机获取奖品id
					if(avatar.avatarVO.getAccount().getPrizecount() > 0 && avatar.avatarVO.getAccount().getIsGame().equals("1")) {
						getPrizeInfo(gameSession);
						avatar.avatarVO.getAccount().setPrizecount(avatar.avatarVO.getAccount().getPrizecount()-1);
						//修改表中抽奖次数
						AccountService.getInstance().updatePrizeCount(avatar.avatarVO.getAccount().getPrizecount()-1);
						
					}else{
						avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000020));
					}
				}
			}
			else{
				//system.out.println("账户信息有误");
			}
		}
		else{
			//system.out.println("账户未登录");
		}
	}	
	/**
	 * 返回所有的抽奖奖品信息
	 */
	public void  prizesInformation(GameSession gameSession){
		JSONObject json  = new JSONObject();
		json.put("data", PrizeService.getInstance().selectAllPrizes());
		json.put("type", "0");
		gameSession.sendMsg(new DrawResponse(1,json));
	}
	/**
	 * 抽取随机奖品，并返回id
	 */
	public void getPrizeInfo(GameSession gameSession){
		//type    0：获取奖品信息    1：获取随机获得奖品id   
		//取随机数 0-9999  数组下标对应的数就是奖品在表中的id
		JSONObject json  = new JSONObject();
		int randomNum = (int)Math.floor(Math.random()*9999);
		json.put("data", PrizeProbability.prizeList.get(randomNum));
		json.put("type", "1");
		gameSession.sendMsg(new DrawResponse(1,json));
	}
}
