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
import com.dyz.gameserver.msg.response.roomcard.RoomCardChangerResponse;
import com.dyz.myBatis.model.Account;
import com.dyz.myBatis.model.Prize;
import com.dyz.myBatis.model.Techargerecord;
import com.dyz.myBatis.model.WinnersInfo;
import com.dyz.myBatis.services.AccountService;
import com.dyz.myBatis.services.PrizeService;
import com.dyz.myBatis.services.TechargerecordService;
import com.dyz.myBatis.services.WinnersInfoService;
import com.dyz.persist.util.DateUtil;
import com.dyz.persist.util.GlobalUtil;
import com.dyz.persist.util.PrizeProbability;

import java.text.ParseException;
import java.util.Date;
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
				Account account = avatar.avatarVO.getAccount();
				if(type.equals("0")){
					//返回获取所有奖品信息
					prizesInformation(gameSession);
				}
				else if(type.equals("1") && account.getPrizecount() >=1){
					//随机获取奖品id
					if(account.getPrizecount() > 0 && account.getIsGame().equals("1")) {
						getPrizeInfo(gameSession,account);
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
	public void getPrizeInfo(GameSession gameSession,Account account){
		//type    0：获取奖品信息    1：获取随机获得奖品id   
		//取随机数 0-9999  数组下标对应的数就是奖品在表中的id
		JSONObject json  = new JSONObject();
		int randomNum = (int)Math.floor(Math.random()*(PrizeProbability.prizeList.size()-1));
		int prizeId = PrizeProbability.prizeList.get(randomNum);
		json.put("data", prizeId);
		json.put("type", "1");
		gameSession.sendMsg(new DrawResponse(1,json));
		//记录中奖信息
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e2) {
			e2.printStackTrace();
		}
		WinnersInfo winnersInfo = new WinnersInfo();
		winnersInfo.setAccountId(gameSession.getRole(Avatar.class).avatarVO.getAccount().getId());
		try {
			winnersInfo.setCreatetime(DateUtil.toChangeDate(new Date(), DateUtil.maskC));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		winnersInfo.setMark("中奖");
		winnersInfo.setPrizeId(prizeId);
		winnersInfo.setStatus("0");
		//如果抽中了房卡，自动给其充入账号里面
		Prize prize =  PrizeService.getInstance().selectByPrimaryKey(prizeId);
		if(prize.getType().equals("0")){
			int awardRoomCard = prize.getPrizecount();
			//自动给玩家充值,修改用户信息，并通知APP端
			int roomCard = account.getRoomcard() + awardRoomCard;
			int totalRoomCard = account.getTotalcard()+awardRoomCard;
			int prizecount = account.getPrizecount()-1;
			Account a = new Account();
			a.setId(account.getId());
			a.setRoomcard(roomCard);
			a.setTotalcard(totalRoomCard);
			a.setPrizecount(prizecount);
			AccountService.getInstance().updateByPrimaryKeySelective(a);
			//修改缓存中的信息
			account.setRoomcard(roomCard);
			account.setTotalcard(totalRoomCard);
			account.setPrizecount(prizecount);
			//通知app端房卡变动
			gameSession.sendMsg(new RoomCardChangerResponse(1,roomCard));
			//增加充值日志记录
			Techargerecord techargerecord = new Techargerecord();
			techargerecord.setAccountId(account.getId());
			try {
				techargerecord.setCreatetime(DateUtil.toChangeDate(new Date(), DateUtil.maskC));
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			techargerecord.setManagerUpId(1);
			techargerecord.setMark("抽奖充值");
			techargerecord.setStatus("0");
			techargerecord.setTechargemoney(awardRoomCard);
			try {
				TechargerecordService.getInstance().saveSelective(techargerecord);
				//修改中奖记录
				winnersInfo.setAwardtime(DateUtil.toChangeDate(new Date(), DateUtil.maskC));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			winnersInfo.setStatus("1");
		}
		WinnersInfoService.getInstance().saveSelective(winnersInfo);
		
	}
}
