package com.dyz.gameserver.logic;

import com.alibaba.fastjson.JSONObject;
import com.context.ConnectAPI;
import com.context.ErrorCode;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.session.GameSession;
import com.dyz.gameserver.context.GameServerContext;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.joinroom.JoinRoomNoice;
import com.dyz.gameserver.msg.response.joinroom.JoinRoomResponse;
import com.dyz.gameserver.msg.response.login.BackLoginResponse;
import com.dyz.gameserver.msg.response.login.OtherBackLoginResonse;
import com.dyz.gameserver.msg.response.outroom.DissolveRoomResponse;
import com.dyz.gameserver.msg.response.outroom.OutRoomResponse;
import com.dyz.gameserver.msg.response.startgame.PrepareGameResponse;
import com.dyz.gameserver.msg.response.startgame.StartGameResponse;
import com.dyz.gameserver.msg.response.ting.TingResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.gameserver.pojo.HuReturnObjectVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.myBatis.model.Account;
import com.dyz.myBatis.services.AccountService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by kevin on 2016/6/18.
 * 房间逻辑
 */
public class RoomLogic {
    private List<Avatar> playerList;
    private boolean isBegin = false;
    private  Avatar createAvator;
    private RoomVO roomVO;
    private PlayCardsLogic playCardsLogic;
    /**
     * //同意解散房间的人数
     */
    private int dissolveCount = 1;
    /**
     *记录是否已经有人申请解散房间
     */
    private boolean dissolve = true;
    /**
     * 是否已经解散房间
     */
    private  boolean hasDissolve = false; 
    /**
     *记录拒绝解散房间的人数，两个人及以上就不解散房间
     */
    private int refuse = 0 ;
    /**
     * 房间属性 1-为普通房间
     */
    private int roomType = 1;
    /**
     * 是否添加字牌
     */
    private boolean addWordCard = false;
  //战绩存取每一局的id
  	List<Integer> standingsDetailsIds = new ArrayList<Integer>();
    /**
     * 房间使用次数
     */
    private int count=0;
    public RoomLogic(RoomVO roomVO){
        this.roomVO = roomVO;
        count = roomVO.getRoundNumber();
    }

    /**
     * 创建房间,默认进入准备状态
     * @param avatar
     */
    public void CreateRoom(Avatar avatar){
        createAvator = avatar;
        roomVO.setPlayerList(new ArrayList<AvatarVO>());
        avatar.avatarVO.setIsReady(true);
        playerList = new ArrayList<Avatar>();
        avatar.avatarVO.setMain(true);
        avatar.setRoomVO(roomVO);
        playerList.add(avatar);
        roomVO.getPlayerList().add(avatar.avatarVO);
    }

    /**
     * 进入房间,默认进入准备状态
     * @param avatar
     */
    public  boolean intoRoom(Avatar avatar){
    	synchronized(roomVO){
    		if(playerList.size() == ConnectAPI.PLAYERS_NUMBER){
    			try {
    				avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000011));
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			return false;
    		}else {
    			for (int i = 0; i < playerList.size(); i++) {
    				if(avatar.getUuId() == playerList.get(i).getUuId() ){
    					//如果用户在本房间中，则直接返回房间信息
    					returnBackAction(avatar);
    					return true;
    				}
				}
    			avatar.avatarVO.setMain(false);
    			//avatar.avatarVO.setIsReady(true);
    			avatar.avatarVO.setIsReady(false);
    			avatar.avatarVO.setRoomId(roomVO.getRoomId());//房间号也放入avatarvo中
    			avatar.setRoomVO(roomVO);
    			noticJoinMess(avatar);//通知房间里面的其他几个玩家
    			playerList.add(avatar);
    			roomVO.getPlayerList().add(avatar.avatarVO);
    			RoomManager.getInstance().addUuidAndRoomId(avatar.avatarVO.getAccount().getUuid(), roomVO.getRoomId());
    			avatar.getSession().sendMsg(new JoinRoomResponse(1, roomVO));
    			try {
    				Thread.sleep(500);
    			} catch (InterruptedException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    			return true;
    			/* if(playerList.size() == 4){
            	//当人数4个时自动开始游戏
                //checkCanBeStartGame();当最后一个人加入时，不需要检测其他玩家是否准备(一局结束后开始才需要检测玩家是否准备)
                Timer timer = new Timer();
                TimerTask tt=new TimerTask() {
                    @Override
                    public void run() {
                    	createAvator.updateRoomCard(-1);//开始游戏，减去房主的房卡
                    	try {
							Thread.sleep(3000);
							startGameRound();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
                    }
                };
                timer.schedule(tt, 1000);
            }*/
    		}
    	}
    }
    /**
     * 当有人加入房间且总人数不够4个时，对其他玩家进行通知
     */
    private void noticJoinMess(Avatar avatar){
    	for (int i = 0; i < playerList.size(); i++) {
            playerList.get(i).getSession().sendMsg(new JoinRoomNoice(1,avatar.avatarVO));
		}
    }
    
    /**
     * 检测是否可以开始游戏
     * @throws IOException 
     */
    public void checkCanBeStartGame() throws IOException{
    	//system.out.println("检测是否可以开始游戏");
    	if(playerList.size() == ConnectAPI.PLAYERS_NUMBER){
    		//房间里面4个人且都准备好了则开始游戏
    		List<AvatarVO> avatarVos = new ArrayList<>();
    		for(int i=0;i<playerList.size();i++){
    			if(!playerList.get(i).avatarVO.getIsReady()){
    				//还有人没有准备
    				avatarVos.add(playerList.get(i).avatarVO);
    			}
    		}
    		if(avatarVos.size() == 0){
    			if(count <= 0){
    				//房间次数已经为0
    				for (Avatar avatar : playerList) {
    					avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000010));
    				}
    			}else{
    				isBegin = true;
    				//所有人都准备好了
    				//system.out.println("所有人都准备好了");
    				startGameRound();
    			}
    		}
    	}
    }

    /**
     * 退出房间
     * @param avatar
     */
    public void exitRoom(Avatar avatar){
    	
        JSONObject json = new JSONObject();
//		accountName:”名字”//退出房间玩家的名字(为空则表示是通知的自己)
//		status_code:”0”//”0”退出成功，”1” 退出失败
//		mess：”消息”
//      type："0"  0退出房间    1解散房间
        json.put("accountName", avatar.avatarVO.getAccount().getNickname());
        json.put("status_code", "0");
        json.put("uuid", avatar.getUuId());
        
        
        if(avatar.avatarVO.isMain()){
        	//群主退出房间就是解散房间
        	json.put("type", "1");
        	exitRoomDetail(json);
        }
        else{
        	json.put("type", "0");
      	    //退出房间。通知房间里面的其他玩家
        	exitRoomDetail(avatar, json);
        	
        	/*for (int i= 0 ; i < playerList.size(); i++) {
        		//通知房间里面的其他玩家
        		playerList.get(i).getSession().sendMsg(new OutRoomResponse(1, json.toString()));
        	}*/
//        	avatar.avatarVO.setRoomId(0);
//        	avatar.setRoomVO(new RoomVO());
//        	playerList.remove(avatar);
//        	roomVO.getPlayerList().remove(avatar.avatarVO);
        	//如果该房间里面的人数只有一个人且不是房主时，解散房间（不可能出现这样的情况）
        	/*if(playerList.size() == 1 && !playerList.get(0).avatarVO.isMain() ){
	        	  json.put("type", "1");
	          	  for (int i= 0 ; i < playerList.size(); i++) {
	          			  playerList.get(i).getSession().sendMsg(new OutRoomResponse(1, json.toString()));
	          			  roomVO.getPlayerList().remove(playerList.get(i).avatarVO);
	          			  playerList.get(i).setRoomVO(new RoomVO());
	          			  playerList.get(i).avatarVO.setRoomId(0);
	        		}
	          	  //销毁房间
	          	  RoomManager.getInstance().destroyRoom(roomVO);
	        	  playerList.clear();
	        	  roomVO.setRoomId(0);
	        	  roomVO = null;
        	}*/
        }
    }

    /**
     * 申请解散房间
     */
    public void dissolveRoom(Avatar avatar , int roomId , String type){
    	//向其他几个玩家发送解散房间信息  
    	JSONObject json;
    	//为0时表示是申请解散房间，1表示同意解散房间  2表示不同意解散房间  3表示解散房间(大部分人同意解散房间)
    	//dissolveCount  = playerList.size();
    	if(type.equals("0")){
    		dissolve = false;
    		dissolveCount = 1;
    		json = new JSONObject();
    		json.put("type", "0");
    		json.put("uuid", avatar.getUuId());
    		json.put("accountName", avatar.avatarVO.getAccount().getNickname());
    		//申请解散房间
    		if(playerList.size() == 1){
    			//如果只有房主一个人时，点申请解散,直接调用退出房间
    			 json = new JSONObject();
    			 json.put("accountName", avatar.avatarVO.getAccount().getNickname());
    		     json.put("status_code", "0");
    		     json.put("uuid", avatar.getUuId());
    		 	json.put("type", "1");
    			exitRoomDetail(json);
    		}else{
    			for (Avatar ava : playerList) {
    				ava.getSession().sendMsg(new DissolveRoomResponse(roomId, json.toString()));
    			}
    		}
    	}
    	else if(type.equals("2")){
    		json = new JSONObject();
    		json.put("type", "2");
    		json.put("uuid", avatar.getUuId());
    		json.put("accountName", avatar.avatarVO.getAccount().getNickname());
    		//拒绝解散房间，向其他玩家发送消息
    		for (Avatar ava : playerList) {
    			ava.getSession().sendMsg(new DissolveRoomResponse(roomId, json.toString()));
    		}
    		refuse = refuse+1;
    		if(refuse == 2){
    			//system.out.println("拒绝解散房间");
    			//重置申请状态， 
    			refuse = 0;
    			dissolve = true;
    			dissolveCount = 1;
    		}
    	}
    	else if(type.equals("1")){
    		//同意解散房间
    		dissolveCount = dissolveCount+1;
    		json = new JSONObject();
    		json.put("type", "1");
    		json.put("uuid", avatar.getUuId());
    		json.put("accountName", avatar.avatarVO.getAccount().getNickname());
    		//同意解散房间，向其他玩家发送消息
    		for (Avatar ava : playerList) {
    			ava.getSession().sendMsg(new DissolveRoomResponse(roomId, json.toString()));
    		}
    		//下面是判断是否所有人都同意解散房间
    		int onlineCount = 0;
    		for (Avatar avat : playerList) {
    			if(avat.avatarVO.getIsOnLine()){
    				onlineCount++;
    			}
    		}
    		
    		if(onlineCount <= dissolveCount+1 && !hasDissolve ){
    			RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).count = 0;
    			hasDissolve = true;
    			//先结算信息，里面同时调用了解散房间的信息
    			playCardsLogic.settlementData("2");
    			/*json = new JSONObject();
    			json.put("type", "3");
    			//所有人都同意了解散房间
    			AvatarVO avatarVO;
    			GameSession gamesession;
    			for (Avatar avat : playerList) {
    				avatarVO = new AvatarVO();
    				avatarVO.setAccount(avat.avatarVO.getAccount());
    				avat.getSession().sendMsg(new DissolveRoomResponse(1, json.toString()));
    				gamesession = avat.getSession();
    				avat = new Avatar();
    				avat.avatarVO = avatarVO;
    				gamesession.setRole(avat);
    				gamesession.setLogin(true);
    				avat.setSession(gamesession);
    				avat.avatarVO.setIsOnLine(true);
    				GameServerContext.add_onLine_Character(avat);
    			}
    			playerList.clear();
    			roomVO.getPlayerList().clear();
    			RoomManager.getInstance().destroyRoom(roomVO);
    			roomVO = null;*/
    		}
    	}
    }
    /**
     * 玩家选择放弃操作
     * @param avatar
     * @param  //1-胡，2-杠，3-碰，4-吃
     */
    public void gaveUpAction(Avatar avatar){
        playCardsLogic.gaveUpAction(avatar);
    }

    /**
     * 出牌
     * @return
     */
    public void chuCard(Avatar avatar, int cardIndex){
        playCardsLogic.putOffCard(avatar,cardIndex);
    }

    /**
     * 摸牌
     */
    public void pickCard(){
    	
        playCardsLogic.pickCard();
    }
    /**
     * 吃牌
     * @param avatar
     * @return
     */
    public boolean chiCard(Avatar avatar,CardVO cardVo){
    	return playCardsLogic.chiCard(avatar,cardVo);
    }
    /**
     * 碰牌
     * @param avatar
     * @return
     */
    public boolean pengCard(Avatar avatar,int cardIndex){
    	return playCardsLogic.pengCard( avatar, cardIndex);
    }
    /**
     * 杠牌
     * @param avatar
     * @return
     */
    public boolean gangCard(Avatar avatar,int cardPoint,int gangType){
    	return playCardsLogic.gangCard( avatar, cardPoint,gangType);
    }
    /**
     * 胡牌
     * @param avatar
     * @return
     */
    public boolean huPai(Avatar avatar,int cardIndex,String type){
    	return playCardsLogic.huPai( avatar, cardIndex,type);
    	
    }
    
    /**
     * 游戏准备
     * @param avatar
     * @throws IOException 
     */
    public void readyGame(Avatar avatar) throws IOException{
    		  //返回房间
    		if(avatar.avatarVO.getRoomId() != roomVO.getRoomId()){
    			////system.out.println("你不是这个房间的");
    			try {
    				avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000006));
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    			return;
    		}
    		avatar.avatarVO.setIsReady(true);
    		int avatarIndex = playerList.indexOf(avatar);
			boolean isRun = avatar.avatarVO.isRun();
			boolean isDunLa = avatar.avatarVO.isDunorla();
    		//成功则返回
    		for (Avatar ava : playerList) {
    			ava.getSession().sendMsg(new PrepareGameResponse(1,avatarIndex, isRun, isDunLa));
    		}
            checkCanBeStartGame();
    }
    /**
     * 开始一回合新的游戏
     */
    private void startGameRound(){
       
         if(count <= 0){
            //房间次数用完了,通知所有玩家
        	for (Avatar avatar : playerList) {
        		try {
					avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000010) );
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
        	
        }else{
	        count--;
	        roomVO.setCurrentRound(roomVO.getCurrentRound() +1);
	        if((count +1) != roomVO.getRoundNumber()){
	        	//说明不是第一局
	        	Avatar avatar = playCardsLogic.bankerAvatar;
	        	playCardsLogic = new PlayCardsLogic();
	        	playCardsLogic.bankerAvatar = avatar;
	        	//摸牌玩家索引初始值为庄家索引
	        	playCardsLogic.setPickAvatarIndex(playerList.indexOf(avatar));
	        }
	        else{
	        	/*
	        	 *修改为 第一局结束 才扣房卡
	        	 * //第一局 减房卡
	        	int currentCard = 0;
	        	if(roomVO.getRoundNumber() == 4){
	        		currentCard = -1;
	        	}
	        	else{
	        		currentCard = 0 - roomVO.getRoundNumber()/8;
	        	}
	        	createAvator.updateRoomCard(currentCard);//开始游戏，减去房主的房卡,同时更新缓存里面对象的房卡(已经在此方法中修改)
	        	
	        	int roomCard = createAvator.avatarVO.getAccount().getRoomcard();
	        	createAvator.getSession().sendMsg(new RoomCardChangerResponse(1,roomCard));*/
	        	playCardsLogic = new PlayCardsLogic();
	        }

			 playCardsLogic.setCreateRoomRoleId(createAvator.getUuId());
	        playCardsLogic.setPlayerList(playerList);
	        playCardsLogic.initCard(roomVO);
	        //system.out.println("下局开始时人数："+playerList.size());
	        //创建房间信息，游戏记录信息
			//RoomInfoService.getInstance().createRoomInfo(roomVO);
					
	        
	       /* int count = 0;
	        if(roomVO.getRoomType() ==1){
	        	//划水麻将，发了牌之后剩余的牌数量
	        	if(roomVO.getHong()){
	        		count = 4*28-13*4-1;
	        	}
	        	else{
	        		count = 4*27-13*4-1;
	        	}
	        }
	        else if(roomVO.getRoomType() ==2){
	        	//划水麻将
	        	
	        }
	        else{
	        	//长沙麻将
	        	
	        }*/
	        //playCardsLogic.updateSurplusCardCount(count);
	        Avatar avatar;
	        Account account ;
	        for(int i=0;i<playerList.size();i++){
	        	//清除各种数据  1：本局胡牌时返回信息组成对象 ，
	        	avatar = playerList.get(i);
	        	avatar.avatarVO.setHuReturnObjectVO(new HuReturnObjectVO());
	            avatar.getSession().sendMsg(new StartGameResponse(1,avatar.avatarVO.getPaiArray(),playerList.indexOf(playCardsLogic.bankerAvatar)));
	            //修改玩家是否玩一局游戏的状态
	            account = AccountService.getInstance().selectByPrimaryKey(avatar.avatarVO.getAccount().getId());
	            if(account.getIsGame().equals("0")){
	            	account.setIsGame("1");
	            	AccountService.getInstance().updateByPrimaryKeySelective(account);
	            	avatar.avatarVO.getAccount().setIsGame("1");
	            }
	        }
        }
    }
    
    /**
     * 前后端握手消息处理
     * @param avatar
     */
    public void shakeHandsMsg(Avatar avatar){
    	playCardsLogic.shakeHandsMsg(avatar);
    }
    /**
     * 开始下一局前，玩家准备
     * @param avatar
     */
    public void readyNext(Avatar avatar){
    	playerList.get(playerList.indexOf(avatar)).avatarVO.setIsReady(true);
    	int hasReady = 0;
    	for (Avatar ava : playerList) {
			if(ava.avatarVO.getIsReady()){
				hasReady++;
			}
		}
    	if(hasReady == ConnectAPI.PLAYERS_NUMBER){
    		//如果四家人都准备好了
    		startGameRound();
    	}
    }
    /**
     * 断线重连，如果房间还未被解散的时候，则返回整个房间信息
     * @param avatar
     */
    public void returnBackAction(Avatar avatar){
    	if(playCardsLogic == null){
        		//只是在房间，游戏尚未开始,打牌逻辑为空
        	for (int i = 0; i < playerList.size(); i++) {
        		if(playerList.get(i).getUuId() != avatar.getUuId()){
        			//给其他三个玩家返回重连用户信息
        			playerList.get(i).getSession().sendMsg(new OtherBackLoginResonse(1, avatar.getUuId()+""));
        		}
        	}
        	avatar.getSession().sendMsg(new BackLoginResponse(1, roomVO));
    	}
    	else{
    		playCardsLogic.returnBackAction(avatar);
    	}
    }
    

    public RoomVO getRoomVO() {
        return roomVO;
    }

	public List<Avatar> getPlayerList() {
		return playerList;
	}

	public void setPlayerList(List<Avatar> playerList) {
		this.playerList = playerList;
	}

	public int getCount() {
		return count;
	}

	public boolean isDissolve() {
		return dissolve;
	}

	public void setDissolve(boolean dissolve) {
		this.dissolve = dissolve;
	}

	public void setDissolveCount(int dissolveCount) {
		this.dissolveCount = dissolveCount;
	}
	/**
	 * 断线重连返回最后操作信息
	 * @param avatar
	 */
	public void LoginReturnInfo(Avatar avatar){
		playCardsLogic.LoginReturnInfo(avatar);
	}
	
	
	/**
	 * 解散房间，销毁房间逻辑
	 */
	public void destoryRoomLogic(){
		AvatarVO avatarVO;
		GameSession gamesession;
		JSONObject json  = new JSONObject();
		json.put("type","3");
		hasDissolve = false;
		for (Avatar avat : playerList) {
			avatarVO = new AvatarVO();
			avatarVO.setAccount(avat.avatarVO.getAccount());
			avatarVO.setIP(avat.avatarVO.getIP());
			avat.getSession().sendMsg(new DissolveRoomResponse(1, json.toString()));
			gamesession = avat.getSession();
			avat = new Avatar();
			avat.avatarVO = avatarVO;
			gamesession.setRole(avat);
			gamesession.setLogin(true);
			avat.setSession(gamesession);
			avat.avatarVO.setIsOnLine(true);
			GameServerContext.add_onLine_Character(avat);
			RoomManager.getInstance().removeUuidAndRoomId(avat.avatarVO.getAccount().getUuid(), roomVO.getRoomId());
		}
		RoomManager.getInstance().destroyRoom(roomVO);
		roomVO = null;
	}
	/**
	 * 房主退出房间，及解散房间，详细清除数据,销毁房间逻辑
	 * @param
	 */
	public void exitRoomDetail(JSONObject json){
		AvatarVO avatarVO;
		GameSession gamesession;
		hasDissolve = false;
		for (Avatar avat : playerList) {
			avatarVO = new AvatarVO();
			avatarVO.setAccount(avat.avatarVO.getAccount());
			gamesession = avat.getSession();
			avatarVO.setIP(avat.avatarVO.getIP());
			gamesession.sendMsg(new OutRoomResponse(1, json.toString()));
			avat = new Avatar();
			avat.avatarVO = avatarVO;
			gamesession.setRole(avat);
			gamesession.setLogin(true);
			avat.setSession(gamesession);
			avat.avatarVO.setIsOnLine(true);
			GameServerContext.add_onLine_Character(avat);
			RoomManager.getInstance().removeUuidAndRoomId(avat.avatarVO.getAccount().getUuid(), roomVO.getRoomId());
		}
		RoomManager.getInstance().destroyRoom(roomVO);
		roomVO = null;
	}
	/**
	 * 房主外的玩家退出房间，详细清除单个数据
	 * @param avatar
	 */
	public void exitRoomDetail(Avatar avatar ,JSONObject json){
		
		for (int i= 0 ; i < playerList.size(); i++) {
    		//通知房间里面的其他玩家
			playerList.get(i).getSession().sendMsg(new OutRoomResponse(1, json.toString()));
    	}
		roomVO.getPlayerList().remove(avatar.avatarVO);
		playerList.remove(avatar);
		
		AvatarVO avatarVO;
		GameSession gamesession;
		avatarVO = new AvatarVO();
		avatarVO.setIP(avatar.avatarVO.getIP());
		avatarVO.setAccount(avatar.avatarVO.getAccount());
		gamesession = avatar.getSession();
		avatar = new Avatar();
		avatar.avatarVO = avatarVO;
		gamesession.setRole(avatar);
		gamesession.setLogin(true);
		avatar.setSession(gamesession);
		avatar.avatarVO.setIsOnLine(true);
		GameServerContext.add_onLine_Character(avatar);
		RoomManager.getInstance().removeUuidAndRoomId(avatar.avatarVO.getAccount().getUuid(), roomVO.getRoomId());
		//
	}

	public List<Integer> getStandingsDetailsIds() {
		return standingsDetailsIds;
	}

	public void setStandingsDetailsIds(List<Integer> standingsDetailsIds) {
		this.standingsDetailsIds = standingsDetailsIds;
	}

	public void ting(Avatar av, boolean bt) {
		av.setbTing(bt);
		if (bt) {
			int idx = playerList.indexOf(av);
			for (int i = 0; i < playerList.size(); i++) {
				playerList.get(i).getSession().sendMsg(new TingResponse(1, bt, idx));
			}
		}
	}
}
