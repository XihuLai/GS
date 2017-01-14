package com.dyz.gameserver.logic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.context.ConnectAPI;
import com.context.ErrorCode;
import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ResponseMsg;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.chi.ChiResponse;
import com.dyz.gameserver.msg.response.chupai.ChuPaiResponse;
import com.dyz.gameserver.msg.response.common.ReturnInfoResponse;
import com.dyz.gameserver.msg.response.common.ReturnOnLineResponse;
import com.dyz.gameserver.msg.response.followBanker.FollowBankerResponse;
import com.dyz.gameserver.msg.response.gang.GangResponse;
import com.dyz.gameserver.msg.response.gang.OtherGangResponse;
import com.dyz.gameserver.msg.response.hu.HuPaiAllResponse;
import com.dyz.gameserver.msg.response.hu.HuPaiResponse;
import com.dyz.gameserver.msg.response.login.BackLoginResponse;
import com.dyz.gameserver.msg.response.login.OtherBackLoginResonse;
import com.dyz.gameserver.msg.response.peng.PengResponse;
import com.dyz.gameserver.msg.response.pickcard.OtherPickCardResponse;
import com.dyz.gameserver.msg.response.pickcard.PickCardResponse;
import com.dyz.gameserver.msg.response.pickcard.PickFlowerCardResponse;
import com.dyz.gameserver.msg.response.roomcard.RoomCardChangerResponse;
import com.dyz.gameserver.pojo.*;
import com.dyz.myBatis.model.*;
import com.dyz.myBatis.services.*;
import com.dyz.persist.util.*;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;
import java.util.Map.Entry;


/**
 * Created by kevin on 2016/6/18.
 * 玩牌逻辑
 */
public class PlayCardsLogic {

    private int paiCount;
    /**
     * 当前出牌人的索引
     */
    private int curAvatarIndex;
    /**
     * 当前摸牌人的索引(初始值为庄家索引)
     */
    private int pickAvatarIndex;
    /**
     * 整张桌子上所有牌的数组
     */
    private List<Integer> listCard=null;
    /**
     * 有人要胡的數組
     */
    private List<Avatar> huAvatar = new ArrayList<>();
    /**
     *有人要碰的數組
     */
    private List<Avatar> penAvatar = new ArrayList<>();
    /**
     *有人要杠的數組
     */
    private List<Avatar> gangAvatar = new ArrayList<>();
    /**
     *有人要咋吃的數組
     */
    private List<Avatar> chiAvatar = new ArrayList<>();
    /**
     * 起手胡
     */
    private List<Avatar> qishouHuAvatar = new ArrayList<>();
    /**
     * 存抓的码
     */
//    List<Integer> mas = new ArrayList<Integer>();
    /**
     * 存抓的有效码
     */
//    List<Integer> validMa = new ArrayList<Integer>();
    /**
     * 下张牌的索引
     */
    private int nextCardindex = 0;
    /**
     * 上一家出的牌的点数
     */
    private int putOffCardPoint;
//    /**
//     * 判断是否是抢胡
//     */
//    private boolean qianghu = false;
    /**
     * 当前玩家摸的牌的点数
     */
    private int currentCardPoint = -2;
    /**
     * 4家玩家信息集合
     */
    private List<Avatar> playerList;
    /**
     * 判断是否可以同时几个人胡牌
     */
//    private int huCount=0;
    /**
     * 庄家
     */
    public Avatar bankerAvatar = null;
    /**
     * 房间信息
     */
    private RoomVO roomVO;
    /**
     * 记录本次游戏是否已经胡了，控制摸牌
     */
    private boolean hasHu;
    /**
     * 记录某个玩家断线时最后一条消息
     */
    //private ResponseMsg responseMsg;
    /**
     * 记录某个玩家断线时发送最后一条消息的玩家
     */
    //private Avatar lastAvtar;

	private NormalHuPai normalHuPai;
    /**
     * String有胡家uuid:码牌1:码牌2  组成
     */
//    private String allMas;
    /**
     * 控制胡牌返回次数
     */
//    int numb = 1;//目前只能胡一次
	//是否跟庄
	boolean followBanke = true;
	//跟庄的次数
	int followNumber = 0;
	//是否被跟庄，最后结算的时候用
	boolean isFollow = false;
	//记录抢杠胡 多响情况
	boolean  hasPull = true;
	
	 //游戏回放，
    PlayRecordGameVO playRecordGame;
    /**
     * 和前段握手，判断是否丢包的情况，丢包则继续发送信息
     *Integer为用户uuid
     */
    //private List<Integer> shakeHandsInfo = new  ArrayList<Integer>();
    private Map<Integer , ResponseMsg>  shakeHandsInfo= new  HashMap<Integer,ResponseMsg>();
    
    
    
	public void setPickAvatarIndex(int pickAvatarIndex) {
		this.pickAvatarIndex = pickAvatarIndex;
	}
	public Map<Integer , ResponseMsg> getShakeHandsInf() {
		return shakeHandsInfo;
	}
	public void updateShakeHandsInfo(Integer uuid ,  ResponseMsg msg) {
		shakeHandsInfo.put(uuid, msg);
	}

//	public String getAllMas() {
//		return allMas;
//	}
	public List<Avatar> getPlayerList() {
		return playerList;
	}
	/**
	 * 房主ID
	 */
	private int theOwner;
	public void setCreateRoomRoleId(int value){
		theOwner = value;
	}

	public void setPlayerList(List<Avatar> playerList) {
		this.playerList = playerList;
	}

	public PlayCardsLogic(){
		normalHuPai = new NormalHuPai();
	}
	/**
	 * 初始化牌
	 */
	public void initCard(RoomVO value) {
		roomVO = value;
		paiCount = 34;
//		if(roomVO.getRoomType() == 1){//房间型号1，2，3暂时废弃，只留4，5，6，7，8
//			//转转麻将
//			paiCount = 27;
//			if(roomVO.getHong()){
//				paiCount = 34;
//			}
//		}else if(roomVO.getRoomType() == 2){
//			//划水麻将
//			if(roomVO.isAddWordCard()) {
//				paiCount = 34;
//			}else{
//				paiCount = 27;
//			}
//		}else if(roomVO.getRoomType() == 3){
//			//长沙麻将
//			paiCount = 27;
//		}else 
	    if(roomVO.isAddFlowerCard()){
			paiCount += 8;
		}
		listCard = new ArrayList<Integer>();
		for (int i = 0; i < paiCount; i++) {
			for (int k = 0; k < 4; k++) {
				if(i < 27) {
					listCard.add(i);
				}else if(i > 26 && i< 34){
					if(roomVO.isAddWordCard())
						listCard.add(i);
					else{
						break;
					}
				}
				else {
					listCard.add(i);
					break;
				}
			}
		}

		for(int i=0;i<playerList.size();i++){
			playerList.get(i).avatarVO.setPaiArray(new int[2][paiCount]);
		}
		//洗牌
		shuffleTheCards();
		//发牌
		dealingTheCards();
	}

	/**
	 * 随机洗牌
	 */
	public void shuffleTheCards() {
		Collections.shuffle(listCard);
		Collections.shuffle(listCard);
	}
	/**
	 * 检测玩家是否胡牌了
	 * @param avatar
	 * @param cardIndex
	 * @param type     当type为""
	 */
	public boolean checkAvatarIsHuPai(Avatar avatar,int cardIndex,String type){
		if(checkHu(avatar,cardIndex)){
			return true;
		}else{
			return false;
		}
	}
	/**
	 * 摸牌
	 *
	 *
	 */
    public void pickCard(){
      	clearAvatar();
        //摸牌
        pickAvatarIndex = getNextAvatarIndex();
        //pickAvatarIndex = nextIndex;
        //本次摸得牌点数，下一张牌的点数，及本次摸的牌点数
        int tempPoint = getNextCardPoint();
    	//System.out.println("摸牌："+tempPoint+"----上一家出牌"+putOffCardPoint+"--摸牌人索引:"+pickAvatarIndex);
        if(tempPoint != -1&&tempPoint<34) {//所摸的不是财神牌
        	//回放记录
        	PlayRecordOperation(pickAvatarIndex,tempPoint,2,-1,null,null);
        	
        	currentCardPoint = tempPoint;
        	Avatar avatar = playerList.get(pickAvatarIndex);
        	avatar.avatarVO.setHasMopaiChupai(true);//修改出牌 摸牌状态
//        	avatar.qiangHu = true;
        	avatar.canHu = true;
//        	avatar.avatarVO.setHuType(0);//重置划水麻将胡牌格式
            //记录摸牌信息
        	 //avatar.canHu = true;
        	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	avatar.getSession().sendMsg(new PickCardResponse(1, tempPoint));
            for(int i=0;i<playerList.size();i++){
                if(i != pickAvatarIndex){
                    playerList.get(i).getSession().sendMsg(new OtherPickCardResponse(1,pickAvatarIndex));
                }else {
                	playerList.get(i).gangIndex.clear();//每次摸牌就先清除缓存里面的可以杠的牌下标
				}
            }
            //判断自己摸上来的牌自己是否可以胡
            StringBuffer sb = new StringBuffer();
            //摸起来也要判断是否可以杠，胡
            avatar.putCardInList(tempPoint);
            if (avatar.checkSelfGang()) {
            	gangAvatar.add(avatar);
            	sb.append("gang");
            	for (int i : avatar.gangIndex) {
            		sb.append(":"+i);
				}
            	sb.append(",");
            	//avatar.gangIndex.clear();//9-18出牌了才清楚(在杠时断线重连后需要这里面的数据)
            }
            if(checkAvatarIsHuPai(avatar,100,"mo")){
            	huAvatar.add(avatar);
            	sb.append("hu,");
            }
            if(sb.length()>2){
            	//System.out.println(sb);
				avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
            }
            
        }else if(tempPoint != -1&&tempPoint>=34) {//所摸的是花牌的处理
        	PlayRecordOperation(pickAvatarIndex,tempPoint,2,-1,null,null);//回放记录
        	Avatar avatar = playerList.get(pickAvatarIndex);
        	avatar.putCardInList(tempPoint);//放入牌组
        	for(int i=0;i<playerList.size();i++){//通知所有玩家摸到一张花牌
                if(i != pickAvatarIndex){
                    playerList.get(i).getSession().sendMsg(new PickFlowerCardResponse(1,pickAvatarIndex,tempPoint));//提醒其他玩家抓到花牌
                }else {
                	avatar.getSession().sendMsg(new PickCardResponse(1, tempPoint));//返回摸到的牌，客户端对花牌做额外的处理
				}
            }
        	pickAvatarIndex = getPreAvatarIndex();//当前摸牌人归位
        	pickCard();//继续摸牌处理
        	return;
        }
        else{
        	//System.out.println("流局");
            PlayRecordOperation(pickAvatarIndex,-1,9,-1,null,null);
        	//流局处理，直接算分
        	settlementData("1");
        }
    }
    /**
     *
     */
    /**
     * 杠了别人(type)/自己摸杠了自后摸牌起来 然后再检测是否可以胡  可以杠等情况
     * @param avatar
     */
    public void pickCardAfterGang(Avatar avatar){
    	
        //本次摸得牌点数，下一张牌的点数，及本次摸的牌点数
        int tempPoint = getNextCardPoint();
        currentCardPoint = tempPoint;
    	//System.out.println("摸牌!--"+tempPoint);
        if(tempPoint != -1) {
        	//int avatarIndex = playerList.indexOf(avatar); // 2016-8-2注释
        	pickAvatarIndex = playerList.indexOf(avatar);
        	// Avatar avatar = playerList.get(pickAvatarIndex);
            //记录摸牌信息
            for(int i=0;i<playerList.size();i++){
                if(i != pickAvatarIndex){
                    playerList.get(i).getSession().sendMsg(new OtherPickCardResponse(1,pickAvatarIndex));
                }else {
                	playerList.get(i).gangIndex.clear();//每次出牌就先清除缓存里面的可以杠的牌下标
					playerList.get(i).getSession().sendMsg(new PickCardResponse(1, tempPoint));
					//摸牌之后就重置可否胡别人牌的标签
					playerList.get(i).canHu = true;
					//System.out.println("摸牌玩家------index"+pickAvatarIndex+"名字"+playerList.get(i).avatarVO.getAccount().getNickname());
				}
            }
            //记录摸牌信息
            PlayRecordOperation(pickAvatarIndex,currentCardPoint,2,-1,null,null);
            
            //判断自己摸上来的牌自己是否可以胡
            StringBuffer sb = new StringBuffer();
            //摸起来也要判断是否可以杠，胡
            avatar.putCardInList(tempPoint);
            if (avatar.checkSelfGang()) {
            	gangAvatar.add(avatar);
            	sb.append("gang");
            	for (int i : avatar.gangIndex) {
            		sb.append(":"+i);
				}
            	sb.append(",");
            	//avatar.gangIndex.clear();
            }
            if(checkAvatarIsHuPai(avatar,100,"ganghu")){
            	//检测完之后不需要移除
            	huAvatar.add(avatar);
            	sb.append("hu,");
            }
            if(sb.length()>2){
            	//System.out.println(sb);
				avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
            }
            
        }
        else{
        	//流局
        	//system.out.println("流局");
        	  //记录摸牌信息
            PlayRecordOperation(pickAvatarIndex,-1,9,-1,null,null);
        	settlementData("1");
        }
    }
    /**
     * 获取下一位摸牌人的索引
     * @return
     */
    public int getNextAvatarIndex(){
        int nextIndex = curAvatarIndex + 1;
        if(nextIndex >= 4){
            nextIndex = 0;
        }
        return nextIndex;
    }
    /**
     * 获取上一位摸牌人的索引
     * @return
     */
    public int getPreAvatarIndex(){
        int nextIndex = curAvatarIndex - 1;
        if(nextIndex <= 0){
            nextIndex = 4;
        }
        return nextIndex;
    }
    /**
     * 玩家选择放弃操作
     * @param avatar
     * @param
     *
     */
    public void gaveUpAction(Avatar avatar){
    	//放弃的时候，至少一个数组不为空才行
    	if(validateStatus()){
    		avatar.huAvatarDetailInfo.clear();
    		//System.out.println(JsonUtilTool.toJson(avatar.getRoomVO()));
    		if(pickAvatarIndex == playerList.indexOf(avatar)){
    			//如果是自己摸的过，则 canHu = true；
    			avatar.canHu = true;
    			clearAvatar();
    		}else{
    			//如果别人打的牌过，
    			//放弃胡，则检测有没人杠
    			clearAvatar();
    			avatar.canHu = false;

    			if(huAvatar.size() == 0){
    				for(Avatar item : gangAvatar){
    					if (item.gangQuest) {
//    						avatar.qiangHu = false;
    						//进行这个玩家的杠操作，并且把后面的碰，吃数组置为0;
    						gangCard(item,putOffCardPoint,1);
    						clearArrayAndSetQuest();
    						//system.out.println("********过了但是还有人gang");
    						return;
    					}
    				}
    				for(Avatar item : penAvatar) {
    					if (item.pengQuest) {
    						//进行这个玩家的碰操作，并且把后面的吃数组置为0;
    						pengCard(item, putOffCardPoint);
    						clearArrayAndSetQuest();
    						//system.out.println("********过了但是还有人pen");
    						return;
    					}
    				}
    				for(Avatar item : chiAvatar){
    					if (item.chiQuest) {
    						//进行这个玩家的吃操作
    						CardVO cardVo = new CardVO();
    						cardVo.setCardPoint(putOffCardPoint);
    						chiCard(item,cardVo);
    						clearArrayAndSetQuest();
    						//system.out.println("********过了但是还有人吃");
    						return;
    					}
    				}
    			}
    			//如果都没有人胡，没有人杠，没有人碰，没有人吃的情况下。则下一玩家摸牌
    			chuPaiCallBack();
    		}
    	}
    }

    /**
     * 清理胡杠碰吃数组，并把玩家的请求状态全部设置为false;
     */
    public void clearArrayAndSetQuest(){
        while (gangAvatar.size() >0){
            gangAvatar.remove(0).setQuestToFalse();
        }
        while (penAvatar.size() >0){
            penAvatar.remove(0).setQuestToFalse();
        }
        while (chiAvatar.size() >0){
            chiAvatar.remove(0).setQuestToFalse();
        }
    }

    /**
     * 出牌
     * @param avatar
     * @param cardPoint
     */
    public void putOffCard(Avatar avatar,int cardPoint){
		//avatar.gangIndex.clear();//每次出牌就清除 缓存里面的可以杠的牌下标
		//System.err.println("出牌："+cardPoint);
//    	avatar.avatarVO.setHuType(0);//重置划水麻将胡牌格式
    	//出牌信息放入到缓存中，掉线重连的时候，返回房间信息需要
        avatar.avatarVO.updateChupais(cardPoint);
        avatar.avatarVO.setHasMopaiChupai(true);//修改出牌 摸牌状态
    	//已经出牌就清除所有的吃，碰，杠，胡的数组
    	clearAvatar();
    	
        putOffCardPoint = cardPoint;
        //system.out.println("出牌点数"+putOffCardPoint+"---出牌人索引:"+playerList.indexOf(avatar));
        curAvatarIndex = playerList.indexOf(avatar);
    	PlayRecordOperation(curAvatarIndex,cardPoint,1,-1,null,null);
        avatar.pullCardFormList(putOffCardPoint);
        for(int i=0;i<playerList.size();i++){
            //不能返回给自己
        	playerList.get(i).gangIndex.clear();//每次出牌就先清除缓存里面的可以杠的牌下标
            if(i != curAvatarIndex) {
                playerList.get(i).getSession().sendMsg(new ChuPaiResponse(1, putOffCardPoint, curAvatarIndex));
               // //system.out.println("发送打牌消息----"+playerList.get(i).avatarVO.getAccount().getNickname());
            } else {
				System.out.println("检查能否听 - " + curAvatarIndex);
				if (!roomVO.isYikouxiangCard()
						&& !avatar.getbTing()
						&& checkSelfTing(avatar)) {
					avatar.getSession().sendMsg(new ReturnInfoResponse(1, "canting"));
				}
			}
    	}

		Avatar ava;
		StringBuffer sb;
		for(int i=0;i<playerList.size();i++){
			ava = playerList.get(i);
			if(ava.getUuId() != avatar.getUuId()) {
				sb = new StringBuffer();
				//判断吃，碰， 胡 杠的时候需要把以前吃，碰，杠胡的牌踢出再计算
				if(avatar.getRoomVO().getZiMo() != 3
						&& ava.canHu
						&& checkAvatarIsHuPai(ava,putOffCardPoint,"chu")){
					//胡牌状态为可胡的状态时才行
					huAvatar.add(ava);
					sb.append("hu,");
				}

				if (ava.checkGang(putOffCardPoint)) {
					gangAvatar.add(ava);
					//同时传会杠的牌的点数
					sb.append("gang:"+putOffCardPoint+",");
				}

				if (ava.checkPeng(putOffCardPoint)) {
					penAvatar.add(ava);
					sb.append("peng,");
				}

				if ( roomVO.isCanchi()  && ava.checkChi(putOffCardPoint) && getNextAvatarIndex() == i){
					//只有下一家才能吃
					chiAvatar.add(ava);
					sb.append("chi");
				}
				if(sb.length()>1 &&
						(!roomVO.isYikouxiangCard() ||
								checkOtherTing(ava, putOffCardPoint))){
					//system.out.println(sb);
					try {
						System.out.println("开始中断执行别的线程"+System.currentTimeMillis());
						Thread.sleep(200);
						System.out.println("结束中断回到当前线程"+System.currentTimeMillis());
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					ava.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
//						responseMsg = new ReturnInfoResponse(1, sb.toString());
//						lastAvtar = ava;
				}
			}
		}
        //如果没有吃，碰，杠，胡的情况，则下家自动摸牌
        chuPaiCallBack();
    }
    
    /**
     * 吃牌
     * @param avatar
     * @param
     * @return
     */
    public boolean chiCard(Avatar avatar , CardVO cardVo){
    	boolean flag = false;
    	int cardIndex = cardVo.getCardPoint();
    	int onePoint = cardVo.getOnePoint();
    	int twoPoint = cardVo.getTwoPoint();
    	//这里可能是自己能胡能碰能杠 但是选择碰
    	if(cardIndex != putOffCardPoint ){
    		System.out.println("传入错误的牌:传入的牌"+cardIndex+"---上一把出牌："+putOffCardPoint);
    	}
		if(cardIndex < 0){
			try {
				avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000019));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		if(huAvatar.contains(avatar)){
			 huAvatar.remove(avatar);
		 }
		 if(gangAvatar.contains(avatar)){
			 gangAvatar.remove(avatar);
		 }
		 if(penAvatar.contains(avatar)){
			 penAvatar.remove(avatar);
		 }
    	//if((huAvatar.size() == 0 || huAvatar.contains(avatar))  && penAvatar.size() >= 1)) {
    	if((penAvatar.size() == 0 && huAvatar.size() == 0)||//没有胡牌和碰牌
    			( huAvatar.contains(avatar) && huAvatar.size() ==1 && penAvatar.size() ==0)||//只有自己胡没有人碰
    			(penAvatar.contains(avatar) && penAvatar.size() ==1 && huAvatar.size() ==0)||//没人胡只有自己碰
    			(huAvatar.contains(avatar) && penAvatar.contains(avatar) && penAvatar.size() ==1 && huAvatar.size() ==1)) {//只有自己胡只有自己碰
    		    avatar.avatarVO.setHasMopaiChupai(true);//修改出牌 摸牌状态
    			 if(chiAvatar.contains(avatar)){
    				//回放记录
    		        PlayRecordOperation(playerList.indexOf(avatar),cardIndex,3,-1,null,null);
    				 //把出的牌从出牌玩家的chupais中移除掉
    				 playerList.get(curAvatarIndex).avatarVO.removeLastChupais();
    				 chiAvatar.remove(avatar);
    				 //更新牌组
    				 flag = avatar.putCardInList(cardIndex);
    				 avatar.setCardListStatus(cardIndex,1);
    				 //把各个玩家碰的牌记录到缓存中去,牌的index
    				 avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("chi", cardIndex+","+onePoint+","+twoPoint);
    				 //avatar.getResultRelation().put(key, value);
    				 clearArrayAndSetQuest();
    				 for (int i=0;i<playerList.size();i++){
    					 if(playerList.get(i).getUuId() == avatar.getUuId()){
    						 //碰了的牌放入到avatar的resultRelation  Map中
    						 playerList.get(i).putResultRelation(4,cardIndex+","+onePoint+","+twoPoint);
    						 playerList.get(i).avatarVO.getPaiArray()[1][cardIndex]=4;//要留有一个地方放吃牌的其他牌
    						 playerList.get(i).avatarVO.getPaiArray()[1][onePoint]=4;//如果一个字为4，两个字为8依次累加
    						 playerList.get(i).avatarVO.getPaiArray()[1][twoPoint]=4;//
    					 }
    					 playerList.get(i).getSession().sendMsg(new ChiResponse(1,cardIndex+","+onePoint+","+twoPoint));
    				 }
//    				 responseMsg = new PengResponse(1,cardIndex,playerList.indexOf(avatar));
//    				 lastAvtar = avatar;
    				 //更新摸牌人信息 2016-8-3
    				 pickAvatarIndex = playerList.indexOf(avatar);
    				 curAvatarIndex = playerList.indexOf(avatar);
    				 currentCardPoint  = -2;
    			// }
    		 }
    		}else{
             if(chiAvatar.size() > 0) {
            	 for (Avatar ava : chiAvatar) {
            		 ava.chiQuest = true;
				}
             }
           }
		return flag;
    }
    /**
     *碰牌
     * @param avatar
     * @return
     */
    public boolean pengCard(Avatar avatar , int cardIndex){
    	boolean flag = false;
    	//这里可能是自己能胡能碰能杠 但是选择碰
    	if(cardIndex != putOffCardPoint ){
    		System.out.println("传入错误的牌:传入的牌"+cardIndex+"---上一把出牌："+putOffCardPoint);
    	}
		if(cardIndex < 0){
			try {
				avatar.getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000019));
			} catch (IOException e) {
				e.printStackTrace();
			}
			return false;
		}
		if(huAvatar.contains(avatar)){
			 huAvatar.remove(avatar);
		 }
		 if(gangAvatar.contains(avatar)){
			 gangAvatar.remove(avatar);
		 }
		 chiAvatar.clear();
    	//if((huAvatar.size() == 0 || huAvatar.contains(avatar))  && penAvatar.size() >= 1)) {
    	if((penAvatar.size() >= 1 && huAvatar.size() == 0) ||
    			( huAvatar.contains(avatar) && huAvatar.size() ==1 && penAvatar.size() ==1)) {
    		    avatar.avatarVO.setHasMopaiChupai(true);//修改出牌 摸牌状态
    			 if(penAvatar.contains(avatar)){
    				//回放记录
    		        PlayRecordOperation(playerList.indexOf(avatar),cardIndex,4,-1,null,null);
    				 //把出的牌从出牌玩家的chupais中移除掉
    				 playerList.get(curAvatarIndex).avatarVO.removeLastChupais();
    				 penAvatar.remove(avatar);
    				 //更新牌组
    				 flag = avatar.putCardInList(cardIndex);
    				 avatar.setCardListStatus(cardIndex,1);
    				 //把各个玩家碰的牌记录到缓存中去,牌的index
    				 avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("peng", cardIndex+"");
    				 //avatar.getResultRelation().put(key, value);
    				 clearArrayAndSetQuest();
    				 for (int i=0;i<playerList.size();i++){
    					 if(playerList.get(i).getUuId() == avatar.getUuId()){
    						 //碰了的牌放入到avatar的resultRelation  Map中
    						 playerList.get(i).putResultRelation(1,cardIndex+"");
    						 playerList.get(i).avatarVO.getPaiArray()[1][cardIndex]=1;
    					 }
    					 playerList.get(i).getSession().sendMsg(new PengResponse(1,cardIndex,playerList.indexOf(avatar)));
    				 }
//    				 responseMsg = new PengResponse(1,cardIndex,playerList.indexOf(avatar));
//    				 lastAvtar = avatar;
    				 //更新摸牌人信息 2016-8-3
    				 pickAvatarIndex = playerList.indexOf(avatar);
    				 curAvatarIndex = playerList.indexOf(avatar);
    				 currentCardPoint  = -2;
    			// }
    		 }
    		}else{
             if(penAvatar.size() > 0) {
            	 for (Avatar ava : penAvatar) {
            		 ava.pengQuest = true;
				}
             }
           }
		return flag;
    }
    /**
     *杠牌
     * @param avatar
     * @return
     */
    public boolean gangCard(Avatar avatar , int cardPoint,int gangType){
        boolean flag = false;
        int avatarIndex = playerList.indexOf(avatar);
        if(huAvatar.contains(avatar)){
            huAvatar.remove(avatar);
        }
        penAvatar.clear();
        chiAvatar.clear();
        if(gangAvatar.size() > 0) {
            if(huAvatar.size() == 0){
                avatar.avatarVO.setHasMopaiChupai(true);//修改出牌 摸牌状态
                if(gangAvatar.contains(avatar)){
                    gangAvatar.remove(avatar);
                    //判断杠的类型，自杠，还是点杠
                    String str;
                    int type;
                    int score; //杠牌分数(转转麻将)      杠牌番数(划水麻将)
                    String recordType;//暗杠 4 ， 明杠 5(用于统计不同type下的次数和得分)
                    String endStatisticstype;
                    int playRecordType;//游戏回放 记录杠的类型
                    if(avatar.getUuId() == playerList.get(pickAvatarIndex).getUuId()){
                        //自杠(明杠或暗杠)，，这里的明杠时需要判断本房间是否是抢杠胡的情况，
                        String strs = avatar.getResultRelation().get(1);
                        if(strs != null && strs.contains(cardPoint+"")){//自杠明杠
                            playRecordType = 3;
                            //明杠（划水麻将里面的过路杠）

                            //存储杠牌的信息，
                            avatar.putResultRelation(2,cardPoint+"");
                            avatar.avatarVO.getPaiArray()[1][cardPoint] = 2;//疑有重复
                            //长沙麻将
                            str = "0:"+cardPoint+":"+Rule.Gang_ming;
                            type = 0;
                            score = Rule.scoreMap.get(Rule.Gang_ming);
                            recordType ="5";
                            endStatisticstype = "minggang";
                        }
                        else{//自杠暗杠
                            playRecordType = 2;
                            //存储杠牌的信息，
                            avatar.putResultRelation(2,cardPoint+"");
                            avatar.avatarVO.getPaiArray()[1][cardPoint] = 2;
                            str = "0:"+cardPoint+":"+Rule.Gang_an;
                            type = 1;
                            score = Rule.scoreMap.get(Rule.Gang_an);
                            recordType ="4";
                            endStatisticstype = "angang";
                        }
                        for (Avatar ava : playerList) {
                            if(ava.getUuId() == avatar.getUuId()){
                                //修改玩家整个游戏总分和杠的总分
                                avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType,score*3);
                                //整个房间统计每一局游戏 杠，胡的总次数
                                roomVO.updateEndStatistics(ava.getUuId()+"", endStatisticstype, 1);
                            }
                            else{
                                //修改其他三家的分数
                                ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType,-1*score);
                            }
                        }
                        flag = true;
                    }
                    else{//点杠
                        //存储杠牌的信息，
                        playRecordType = 1;

                        avatar.putResultRelation(2,cardPoint+"");
                        avatar.avatarVO.getPaiArray()[1][cardPoint] = 2;
                        //点杠(分在明杠里面)（划水麻将里面的放杠）
                        //把出的牌从出牌玩家的chupais中移除掉
                        playerList.get(curAvatarIndex).avatarVO.removeLastChupais();

                        //更新牌组(点杠时才需要更新)   自摸时不需要更新
                        flag = avatar.putCardInList(cardPoint);

                        score = Rule.scoreMap.get(Rule.Gang_dian);;
                        recordType = "4";
                        str = playerList.get(curAvatarIndex).getUuId()+":"+cardPoint+":"+Rule.Gang_dian;
                        type = 0;
                        endStatisticstype = "minggang";
                        //减点杠玩家的分数
                        playerList.get(curAvatarIndex).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1*score);
                        //增加杠家的分数
                        avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, score);
                        //整个房间统计每一局游戏 杠，胡的总次数
                        roomVO.updateEndStatistics(avatar.getUuId()+"", endStatisticstype, 1);
                    }

                    avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("gang", str);
                    //回放记录
                    PlayRecordOperation(avatarIndex,cardPoint,5,playRecordType,null,null);


                    clearArrayAndSetQuest();
                    //摸牌并判断自己摸上来的牌自己是否可以胡/可以杠****
                    for (int i=0;i<playerList.size();i++){
                        if(avatar.getUuId() != playerList.get(i).getUuId()){
                            //杠牌返回给其他人只返回杠的类型和杠牌的玩家位置
                            playerList.get(i).getSession().sendMsg(new OtherGangResponse(1,cardPoint,avatarIndex,type));
                        }
                        else{
                            //杠牌返回给其他人只返回杠的类型和杠牌的玩家位置
                            playerList.get(i).getSession().sendMsg(new GangResponse(1, 1, 1,type));
                        }
                    }
                    pickCardAfterGang(avatar);//2016-8-1
                }
            }
            else{
                for (Avatar ava : gangAvatar) {
                    ava.gangQuest = true;
                }
            }
        }else{
            try {
                playerList.get(avatarIndex).getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000016));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return flag;
    }
    
    public int calculateScore(Avatar avatar , int cardIndex,int dian){
    	int score = 0;
    	int totalScore = 0;
    	String recordType = "";//胡牌的分类
    	
//    	score+=1;//基本胡牌分数
//		if(avatar.getUuId()==bankerAvatar.getUuId())//庄家胡
//			score+=1;

		
    	int pldscore = roomVO.getPldscore();
		Map<String,Integer> huResult = checkHu2(avatar , cardIndex);//算好所有的名堂
		int roomType = roomVO.getRoomType();
		if(roomType==4||roomType==5||roomType==6||roomType==7){//鄂尔多斯，呼和浩特和集宁玩法
			int roomScore = 5;
				if(roomType==7)	
					roomScore = 9;
			if(!huResult.containsKey(Rule.Hu_qxd)&&huResult.containsKey(Rule.Hu_menqing))//门清
				score+=1;
			if(roomVO.isAddFlowerCard()&&huResult.containsKey(Rule.CaiShen))//财神
				score+=huResult.get(Rule.CaiShen);
			//特色选项
			if(roomVO.isQgbkd()&&huResult.containsKey(Rule.Hu_quemen))//缺门
				score+=1;
			if(roomVO.isQgbkd()&&huResult.containsKey(Rule.Hu_qingyise)&&!huResult.containsKey(Rule.Hu_yitiaolong)&&huResult.containsKey(Rule.Hu_gouzhang))//够张
				score+=1;
			if(huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isKan5())//坎五万
				score+=roomScore;
			if(roomVO.isQgbkd()&&!huResult.containsKey(Rule.Hu_qxd)&&huResult.containsKey(Rule.Hu_biankandiao)&&!huResult.containsKey(Rule.Hu_kanwuwan))//边砍钓
				score+=1;
			//处理胡牌方法
			if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==1)//清一色
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_qxd))//七小对
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_yitiaolong))//一条龙
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_pengpeng)&&roomVO.isPengpeng())//碰碰胡
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==2&&roomVO.isHunyise())//混一色
				score+=roomScore;
			if(huResult.containsKey(Rule.Hu_haohuaqxd))//豪华七对
				score+=10;
			if(huResult.containsKey(Rule.Hu_shisanyao))//十三幺
				score+=13;
			
			
				
				boolean dunorla = avatar.avatarVO.isDunorla();
				boolean pao = avatar.avatarVO.isRun();
				for(Avatar player:playerList){//分别处理四个用户的赢输牌的分数
					int calscore = 0;
					if(player.getUuId()!=avatar.getUuId()){//如果不是当前用户
				//处理跑拉蹲分数		
				if(dunorla)
					calscore+=pldscore;
				if(pao)
					calscore+=pldscore;
				boolean curdunorla = avatar.avatarVO.isDunorla();
				boolean curpao = avatar.avatarVO.isRun();
				if(curdunorla)
					calscore+=pldscore;
				if(curpao)
					calscore+=pldscore;
				
				
				if(followBanke){//为连庄
					calscore+=1;
					
				}else{//为拉庄
					if(player.avatarVO.isMain())//如果是被拉庄了，那么多出一分
						calscore+=1;
					
				}
				
				if(dian == -1){//为自摸
					recordType = "1";
					//加分项逻辑
					calscore+=2;//自摸加两分
				}else{//为点炮
					if(playerList.indexOf(player)!=dian){
						continue;
					}
					if(roomType==7)
						calscore = 3;
					else
						calscore+=1;//点炮加一分
					
				}
				
				calscore+=score;
				totalScore+=calscore;
				
				player.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1*(calscore));
					}else{//如果是当前用户
						//增加胡家的分数
						 
					}
				}
				avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, totalScore);
			
		}else if(roomType == 8){//包头
			int roomScore = 5;
			int multiscore = 1;
		if(huResult.containsKey(Rule.Hu_menqing))//门清加倍
			multiscore*=2;
		if(roomVO.isAddFlowerCard()&&huResult.containsKey(Rule.CaiShen))//财神
			score+=huResult.get(Rule.CaiShen);
		//特色选项
		if(huResult.containsKey(Rule.Hu_quemen)&&roomVO.isQgbkd())//缺门
			score+=huResult.get(Rule.Hu_quemen);
		if(huResult.containsKey(Rule.Hu_gouzhang)&&roomVO.isQgbkd())//够张
			score+=1;
		if(huResult.containsKey(Rule.Hu_biankandiao)&&!huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isQgbkd())//边砍钓
			score+=1;
		if(huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isKan5())//坎五万
			score+=roomScore;
		//处理胡牌方法
		if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==1)//清一色
			score+=roomScore*2;
		if(huResult.containsKey(Rule.Hu_qxd))//七小对
			score+=roomScore*2;
		if(huResult.containsKey(Rule.Hu_yitiaolong))//一条龙
			score+=roomScore*2;
		if(huResult.containsKey(Rule.Hu_pengpeng)&&roomVO.isPengpeng())//碰碰胡
			score+=roomScore;
		if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==2&&roomVO.isHunyise())//混一色
			score+=roomScore;
		if(huResult.containsKey(Rule.Hu_haohuaqxd))//豪华七对
			score+=(roomScore*4);
		if(huResult.containsKey(Rule.Hu_shisanyao))//缺门
			score+=13;
		
		
			
			boolean dunorla = avatar.avatarVO.isDunorla();
			boolean pao = avatar.avatarVO.isRun();
			for(Avatar player:playerList){//分别处理四个用户的赢输牌的分数
				int calscore = 0;
				int calmulti = 1;
				if(player.getUuId()!=avatar.getUuId()){//如果不是当前用户
			//处理跑拉蹲分数		
			if(dunorla)
				calscore+=pldscore;
			if(pao)
				calscore+=pldscore;
			boolean curdunorla = avatar.avatarVO.isDunorla();
			boolean curpao = avatar.avatarVO.isRun();
			if(curdunorla)
				calscore+=pldscore;
			if(curpao)
				calscore+=pldscore;
			
			
			if(followBanke){//为连庄
				calscore+=1;
				
			}else{//为拉庄
				if(player.avatarVO.isMain())//如果是被拉庄了，那么多出一分
					calscore+=1;
				
			}
			
			if(dian == -1){//为自摸
				recordType = "1";
				//加分项逻辑
				calmulti*=2;//自摸加倍
			}else{//为点炮
				if(playerList.indexOf(player)!=dian){
					continue;
				}
				if(roomType==7)
					calscore = 3;
				else
					calscore+=1;//点炮加一分
				
			}
			calscore+=score;
			calmulti*=multiscore;
			calscore*=calmulti;
			totalScore+=calscore;
			player.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1*calscore);
				}else{//如果是当前用户
					//增加胡家的分数
					 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, totalScore);
				}
			}
			avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, totalScore);
		}
			 
    	return 0;
    }
    /**
     *胡牌
     * @param avatar
     * @return
     */
    public boolean huPai(Avatar avatar , int cardIndex,String type){
    	boolean flag = false;
    	StringBuffer sb = new StringBuffer();
    	avatar.getPaiArray()[1][cardIndex] = 3;
    	//当胡家手上没有红中，则多抓一个码
    	int playRecordType = 6;
    	if(huAvatar.size() > 0) {	
    		
    		
   		 if(huAvatar.contains(avatar)){
    			//if(playerList.get(pickAvatarIndex).getUuId() != avatar.getUuId()){
    			if(pickAvatarIndex == curAvatarIndex){//点炮，那么点炮人的下家优先胡,摸牌的人刚刚出牌
    				//下面代码判断是否有人胡牌优先级更高
    				int ami = playerList.indexOf(avatar);
    	   			 int distance = ami - curAvatarIndex;
    	   			 boolean needWait = false;
    	   			 if(distance<0)
    	   				distance = 4+distance;
    	   			 for(Avatar canhuAvator:huAvatar){
    	   				 int curindex = playerList.indexOf(canhuAvator);
    	   				int dis = curindex - curAvatarIndex;
    	   				if(dis<0)
    	   					dis = dis+4;
    	   				if(dis<distance)
    	   					needWait = true;
    	   			 }
    	   			 
    				//把胡了的牌索引放入到对应赢家的牌组中
    		    	avatar.putCardInList(cardIndex);
    				//system.out.println("点炮");
    				//当摸牌人的索引等于出牌人的索引时，表示点炮了
    				//点炮    别人点炮的时候查看是否可以胡
    				if(avatar.canHu&&!needWait){
    					//胡牌数组中移除掉胡了的人
    					huAvatar.remove(avatar);
    					gangAvatar.clear();
    	    			penAvatar.clear();
    	    			chiAvatar.clear();;
    					//两个人之间建立关联，游戏结束算账用 
 			
    					//整个房间统计每一局游戏 杠，胡的总次数
    					roomVO.updateEndStatistics(avatar.getUuId()+"", "jiepao", 1);
    					roomVO.updateEndStatistics(playerList.get(curAvatarIndex).getUuId()+"", "dianpao", 1);
    					flag = true;
    				}
    				else{
    					//system.out.println("放过一个人就要等自己摸牌之后才能胡其他人的牌"); 
    					avatar.huQuest = true;
    					return false;
//    					huAvatar.remove(avatar);
    				}
    				calculateScore(avatar , cardIndex,curAvatarIndex);
    			}
    			else{
    				//自摸,一定能胡    				
    				//system.out.println("自摸");
    				//胡牌数组中移除掉胡了的人
    				huAvatar.remove(avatar);
    				gangAvatar.clear();
        			penAvatar.clear();
        			chiAvatar.clear();;
    				//两个人之间建立关联，游戏结束算账用   自摸不会出现抢胡的情况
    				roomVO.updateEndStatistics(avatar.getUuId()+"", "zimo", 1);
    				flag = true;
    				calculateScore(avatar , cardIndex,-1);
    			}
    			//本次游戏已经胡，不进行摸牌
    			hasHu = true;
    			//游戏回放
    			PlayRecordOperation(playerList.indexOf(avatar),cardIndex,playRecordType,-1,null,null);
   		 	}
   	 	}
    		//更新roomlogic的PlayerList信息
     		if(avatar.getUuId()==bankerAvatar.getUuId()){//当前庄家胡了牌，那么算跟庄,不重新分配庄家
     			followBanke = true;
     			followNumber++;
     		}else{//重新分配庄家为庄家的下家
     			followBanke = false;
     			followNumber = 0;
     			int curbank = playerList.indexOf(bankerAvatar);
     			curbank++;
     			if(curbank>=4)
     				curbank = 0;
     			bankerAvatar = playerList.get(curbank);
     			bankerAvatar.avatarVO.setMain(true);
     		}
     		
    		RoomManager.getInstance().getRoom(playerList.get(0).getRoomVO().getRoomId()).setPlayerList(playerList);
    		//一局牌胡了，返回这一局的所有数据吃，碰， 杠，胡等信息
    		settlementData("0");
//    	}
    	return flag;
    }
    
    /**
     * 胡牌/流局/解散房间后返回结算数据信息
     * 不能多次调用，多次调用，总分会多增加出最近一局的分数    第一局结束扣房卡
     */
    public void settlementData(String  type){
    	
    	int totalCount = roomVO.getRoundNumber();
    	int useCount = RoomManager.getInstance().getRoom(roomVO.getRoomId()).getCount();
    	if(totalCount == (useCount +1) && !type.equals("2")){
    		//第一局结束扣房卡
//    		deductRoomCard();//因为测试所以注释这行
    	}
    	JSONArray array = new JSONArray();
    	JSONObject json = new JSONObject();
//    	if(!type.equals("0")){
//    		allMas = null; 
//    	}
    	StandingsDetail standingsDetail = new StandingsDetail();
    	StringBuffer content = new StringBuffer();
    	StringBuffer score = new StringBuffer();
    	for (Avatar avatar : playerList) {
    		HuReturnObjectVO   huReturnObjectVO = avatar.avatarVO.getHuReturnObjectVO();
    		//生成战绩内容
    		content.append(avatar.avatarVO.getAccount().getNickname()+":"+huReturnObjectVO.getTotalScore()+",");
    		
    		//统计本局分数
    		huReturnObjectVO.setNickname(avatar.avatarVO.getAccount().getNickname());
    		huReturnObjectVO.setPaiArray(avatar.avatarVO.getPaiArray()[0]);
    		huReturnObjectVO.setUuid(avatar.getUuId());
    		array.add(huReturnObjectVO);
    		//在整个房间信息中修改总分数(房间次数用完之后的总分数)
    		roomVO.updateEndStatistics(avatar.getUuId()+"", "scores", huReturnObjectVO.getTotalScore());
    		score.append(avatar.getUuId()+":"+ roomVO.getEndStatistics().get(avatar.getUuId()+"").get("scores")+",");
    		//修改存储的分数(断线重连需要)
    		avatar.avatarVO.supdateScores(huReturnObjectVO.getTotalScore());
    		//游戏回放 中码消息
    		if(avatar.avatarVO.isMain()){
    			if(!type.equals("0")){
    				PlayRecordOperation(playerList.indexOf(avatar),-1,8,-1,null,null);
    			}
    			else{
    				PlayRecordOperation(playerList.indexOf(avatar),-1,8,-1,null,HuPaiType.getInstance().getValidMa());
    			}
    		}
		}
    	json.put("avatarList", array);
//    	json.put("allMas", allMas);
    	json.put("type", type);
//    	if(!type.equals("0")){
//    		json.put("validMas", new ArrayList<>());
//    	}
//    	else{
//    		json.put("validMas", validMa);
//    	}
    	json.put("currentScore", score.toString());
    	//生成战绩content
    	standingsDetail.setContent(content.toString());
    	try {
    		standingsDetail.setCreatetime(DateUtil.toChangeDate(new Date(), DateUtil.maskC));
    		int id = StandingsDetailService.getInstance().saveSelective(standingsDetail);
    		if(id >0){
    			 RoomLogic roomLogic = RoomManager.getInstance().getRoom(roomVO.getRoomId());
    			 roomLogic.getStandingsDetailsIds().add(standingsDetail.getId());
    			//更新游戏回放中的玩家分数
    		    PlayRecordInitUpdateScore(standingsDetail.getId());
    		}
    		else{
    			System.out.println("分局战绩录入失败："+new Date());
    		}
		} catch (ParseException e) {
			e.printStackTrace();
		}
    	
    	
    	int count = 10;
    	for (Avatar avatar : playerList) {
    		//发送消息
    		avatar.getSession().sendMsg(new HuPaiResponse(1,json.toString()));
    		
    		
    		avatar.overOff = true;
    		avatar.oneSettlementInfo = json.toString();
    		
    		//清除一些存储数据
    		avatar.getResultRelation().clear();

    		avatar.avatarVO.getChupais().clear();
    		avatar.avatarVO.setCommonCards(0);
    		avatar.avatarVO.setHasMopaiChupai(false);
    		//清除 hu	ReturnObjectVO 信息 
    		avatar.avatarVO.setHuReturnObjectVO(new HuReturnObjectVO());

    		count = RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).getCount();
		}
    	//房间局数用完，返回本局胡牌信息的同时返回整个房间这几局的胡，杠等统计信息
	  if(count <= 0){
		  	//总房间战绩
		  	Standings standings  = new Standings();
		  	StringBuffer sb = new StringBuffer();
		  	//standings.setContent(content);
			Map<String, Map<String, Integer>> endStatistics = roomVO.getEndStatistics();
			Map<String,Integer> map = new HashMap<String, Integer>();
			Set<Entry<String, Map<String, Integer>>> set= endStatistics.entrySet();
			JSONObject js = new JSONObject();
			List<FinalGameEndItemVo> list = new ArrayList<FinalGameEndItemVo>();
			FinalGameEndItemVo obj;
			for (Entry<String, Map<String, Integer>>  param : set) {
				obj = new FinalGameEndItemVo();
				obj.setUuid(Integer.parseInt(param.getKey()));
				sb.append(AccountService.getInstance().selectByUUid(Integer.parseInt(param.getKey())).getNickname());
				map = param.getValue();
				for (Entry<String, Integer> entry : map.entrySet()) {
					switch (entry.getKey()) {
					case "zimo":
						obj.setZimo(entry.getValue());
						break;
					case "jiepao":
						obj.setJiepao(entry.getValue());
						break;
					case "dianpao":
						obj.setDianpao(entry.getValue());
						break;
					case "minggang":
						obj.setMinggang(entry.getValue());
						break;
					case "angang":
						obj.setAngang(entry.getValue());
						break;
					case "scores":
						obj.setScores(entry.getValue());
						sb.append(":"+entry.getValue()+",");
						break;
					default:
						break;
					}
				}
				list.add(obj);
			}
			js.put("totalInfo", list);
		  	js.put("theowner",theOwner);
			//system.out.println("这个房间次数用完：返回数据=="+js.toJSONString());
			//战绩记录存储
			standings.setContent(sb.toString());
			try {
				standings.setCreatetime(DateUtil.toChangeDate(new Date(), DateUtil.maskC));
				standings.setRoomid(roomVO.getId());
				int i = StandingsService.getInstance().saveSelective(standings);
				if(i> 0){
					//存储 房间战绩和每局战绩关联信息
					StandingsRelation standingsRelation;
	    			 List<Integer> standingsDetailsIds =RoomManager.getInstance().getRoom(roomVO.getRoomId()).getStandingsDetailsIds();
					for (Integer standingsDetailsId : standingsDetailsIds) {
						standingsRelation = new StandingsRelation();
						standingsRelation.setStandingsId(standings.getId());
						standingsRelation.setStandingsdetailId(standingsDetailsId);
						StandingsRelationService.getInstance().saveSelective(standingsRelation);
					}
					//存储 房间战绩和每个玩家关联信息
					StandingsAccountRelation standingsAccountRelation;
					for (Avatar avatar : playerList) {
						standingsAccountRelation = new StandingsAccountRelation();
						standingsAccountRelation.setStandingsId(standings.getId());
						standingsAccountRelation.setAccountId(avatar.avatarVO.getAccount().getId());
						StandingsAccountRelationService.getInstance().saveSelective(standingsAccountRelation);
					}
				}
				System.out.println("整个房间战绩"+i);
			} catch (ParseException e) {
				e.printStackTrace();
			}
			//发送消息
			for (Avatar avatar : playerList) {
				avatar.getSession().sendMsg(new HuPaiAllResponse(1,js.toString()));
			}
			//4局完成之后解散房间//销毁
			RoomManager.getInstance().getRoom(roomVO.getRoomId()).destoryRoomLogic();
		}
		//判断该房间还有没有次数。有则清除玩家的准备状态，为下一局开始做准备
		else{
			//清除当前房间牌的数据信息
			for (Avatar avatar : playerList) {
				avatar.avatarVO.setIsReady(false);
			}
		}
    }
    
    /**
     * 出牌返回出牌点数和下一家玩家信息
     * @param
     *
     */
    private void chuPaiCallBack(){
    	//把出牌点数和下面该谁出牌发送会前端  下一家都还没有摸牌就要出牌了??
        if(!hasHu && checkMsgAndSend()){
        	//如果没有吃，碰，杠，胡的情况，则下家自动摸牌
            pickCard();
        }
    }
    /**
     * 發送吃，碰，杠，胡牌信息
     * @return
     */
    private boolean checkMsgAndSend(){
        if(huAvatar.size() > 0){
            return false;
        }
        if(gangAvatar.size() >0){
            return false;
        }
        if(penAvatar.size()>0){
            return false;
        }
        if(chiAvatar.size()>0){
        	return false;
        }
        return true;
    }

    /**
     * 发牌
     */
    private void dealingTheCards() {
    	nextCardindex = 0;
        bankerAvatar = null;
		Avatar av;
		for (int i = 0; i < 13; i++) {
            for (int k = 0; k < playerList.size(); k++) {
				av = playerList.get(k);
                if (bankerAvatar == null) {
                    if (av.avatarVO.isMain()) {
                        bankerAvatar = av;
                    }
                }
                //处理抓到花牌的逻辑
                int curCard = listCard.get(nextCardindex);
                av.putCardInList(curCard);//放到牌组里面
				while(curCard>33){//是花牌则重新发张
					setFlowerCardOwnerInfo(av, curCard);
					nextCardindex++;
					curCard = listCard.get(nextCardindex);
					av.putCardInList(curCard);//放到牌组里面
				}
                
                av.oneSettlementInfo = "";
                av.overOff = false;
                nextCardindex++;
            }
        }
      //处理抓到花牌的逻辑
        int curCard = listCard.get(nextCardindex);
        bankerAvatar.putCardInList(curCard);//放到牌组里面
		while(curCard>33){//是花牌则重新发张
			setFlowerCardOwnerInfo(bankerAvatar, curCard);
			nextCardindex++;
			curCard = listCard.get(nextCardindex);
			bankerAvatar.putCardInList(curCard);//放到牌组里面
		}
        nextCardindex++;
        
        //检测一下庄家有没有天胡
       if(checkHu(bankerAvatar,-1)){
    	   //检查有没有天胡/有则把相关联的信息放入缓存中
    	   huAvatar.add(bankerAvatar);
    	   pickAvatarIndex = 0;//第一个摸牌人就是庄家
    	   //发送消息
    	   bankerAvatar.getSession().sendMsg(new HuPaiResponse(1,"hu,"));
    	   bankerAvatar.huAvatarDetailInfo.add(listCard.get(nextCardindex-1)+":"+0);
       }
       //检测庄家起手有没的杠  长沙麻将叫做大四喜
       if(bankerAvatar.checkSelfGang()){
    	   gangAvatar.add(bankerAvatar);
    	   //发送消息
		   StringBuffer sb = new StringBuffer();
		   sb.append("gang");
		   for (int i : bankerAvatar.gangIndex) {
			   sb.append(":"+i);
		   }
		   sb.append(",");

    	   bankerAvatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
    	  // bankerAvatar.huAvatarDetailInfo.add(bankerAvatar.gangIndex.get(0)+":"+2);
		   //bankerAvatar.gangIndex.clear();
       }
       //游戏回放
       PlayRecordInit();
    }
    
    /**
     * 游戏回放，记录 房间信息和初始牌组，玩家信息
     */
    public void PlayRecordInit(){
    	 playRecordGame = new PlayRecordGameVO();
    	 RoomVO roomVo = roomVO.clone();
    	 roomVo.setEndStatistics(new HashMap<String, Map<String,Integer>>());
    	 roomVo.setPlayerList(new ArrayList<AvatarVO>());
         playRecordGame.roomvo = roomVo;
         PlayRecordItemVO playRecordItemVO;
         Account account;
         StringBuffer sb;
         for (int i = 0; i < playerList.size(); i++) {
  		   playRecordItemVO = new PlayRecordItemVO();
  		   account = playerList.get(i).avatarVO.getAccount();
  		   playRecordItemVO.setAccountIndex(i);
  		   playRecordItemVO.setAccountName(account.getNickname());
  		   sb = new StringBuffer();
  		   int [] str = playerList.get(i).getPaiArray()[0];
  		   for (int j = 0; j < str.length; j++) {
  			   sb.append(str[j]+",");
  		   }
  		   System.out.println(account.getUuid()+"的牌为:"+sb.substring(0,sb.length()-1));
  		   playRecordItemVO.setCardList(sb.substring(0,sb.length()-1));
  		   playRecordItemVO.setHeadIcon(account.getHeadicon());
  		   playRecordItemVO.setSex(account.getSex());
  		   playRecordItemVO.setGameRound(roomVO.getCurrentRound());
  		   playRecordItemVO.setUuid(account.getUuid());
  		   playRecordGame.playerItems.add(playRecordItemVO);
  		}
    }
    
    /**
     * 游戏回放，记录打牌操作信息
     * 
     * @param curAvatarIndex  操作玩家索引
     * @param cardIndex 操作相关牌索引
     * @param type 操作相关步骤  1出牌，2摸牌，3吃，4碰，5杠，6胡(自摸/点炮),7抢胡,8抓码,9:流局.....
     * @param gangType  type不为杠时 传入 -1
     * @param ma  不是抓码操作时 为null
     */
    public void PlayRecordOperation(Integer curAvatarIndex , Integer cardIndex,Integer type,Integer gangType,String ma,List<Integer> valideMa){
    	
    	//System.out.println("记录操作"+type);
    	PlayBehaviedVO behaviedvo = new PlayBehaviedVO();
    	behaviedvo.setAccountindex_id(curAvatarIndex);
    	behaviedvo.setCardIndex(cardIndex+"");
    	behaviedvo.setRecordindex(playRecordGame.behavieList.size());
    	behaviedvo.setType(type);
    	behaviedvo.setGangType(gangType);
    	if(StringUtil.isNotEmpty(ma)){
    		behaviedvo.setMa(ma);
    		behaviedvo.setValideMa(valideMa);
    	}
    	playRecordGame.behavieList.add(behaviedvo);
    	
    }
    
    /**
     * 游戏回放，记录 房间信息和初始牌组，玩家信息 中添加分数
     * @param standingsDetailId 本局游戏的id
     */
    public void PlayRecordInitUpdateScore(int standingsDetailId){
    	
    	if(!playRecordGame.playerItems.isEmpty()){
    		//没有发牌就解散房间
    		for (int i = 0; i < playerList.size(); i++) {
    			playRecordGame.playerItems.get(i).setSocre(playerList.get(i).avatarVO.getScores());
    		}
    		//playRecordGame.standingsDetailId = standingsDetailId;
    		//信息录入数据库表中
    		//String playRecordContent = JsonUtilTool.toJson(playRecordGame);
    		String playRecordContent = JSONObject.toJSONString(playRecordGame);
    		//System.out.println(playRecordContent);
    		PlayRecord playRecord = new PlayRecord();
    		playRecord.setPlayrecord(playRecordContent);
    		playRecord.setStandingsdetailId(standingsDetailId);
    		PlayRecordService.getInstance().saveSelective(playRecord);
    		//录入表之后重置 记录
    		playRecordGame = new PlayRecordGameVO();
    	}
    	
    }
    
    /**
     * 获取下一张牌的点数,如果返回为-1 ，则没有牌了
     * @return
     */
    public int getNextCardPoint(){
    	nextCardindex++;
        if(nextCardindex<listCard.size()){
            return listCard.get(nextCardindex);
        }
        return -1;
    }
    private void checkQiShouFu(){
    	for(int i=0;i<playerList.size();i++){
    		//判断是否有起手胡，有则加入到集合里面
    		if(qiShouFu(playerList.get(i))){
    			qishouHuAvatar.add(playerList.get(i));
    		}
    	}
    }
    /**
     * 是否是起手胡
     * @return
     */
    public boolean qiShouFu(Avatar avatar){
    	/**
		 * 起手胡：
			1 、大四喜：起完牌后，玩家手上已有四张一样的牌，即可胡牌。（四喜计分等同小胡自摸）pai[i] == 4
			2 、板板胡：起完牌后，玩家手上没有一张 2 、 5 、 8 （将牌），即可胡牌。（等同小胡自摸）
			3 、缺一色：起完牌后，玩家手上筒、索、万任缺一门，即可胡牌。（等同小胡自摸）
			4 、六六顺：起完牌后，玩家手上已有 2 个刻子（刻子：三个一样的牌），即可胡牌。（等同小胡自摸）
		 */
    	//1:大四喜
    	boolean flag = false;
    	int[] pai= avatar.avatarVO.getPaiArray()[0];
    	boolean flagWan = true;
    	boolean flagTiao= true;
    	boolean flagTong = true;
    	int threeNum = 0;
    	boolean dasixi = false;
    	boolean banbanhu = false;
    	boolean quyise = false;
    	boolean liuliushun = false;
		for (int i =0 ; i< pai.length ; i++) {
			if(pai[i] == 4){
				//大四喜
				dasixi = true;
				//胡牌信息放入缓存中****
			}
			if(pai[i] == 3){
				//六六顺
				threeNum++;
				if(threeNum == 2){
					liuliushun = true;
				}
			}
			if(i>=0 && i <=8){
				//缺一色
				if(pai[i] > 0){
					//只要存在一条万子
					flagWan = false;
				}
			}
			else if(i>9 && i<=18){
				//缺一色
				if(pai[i] > 0){
					//只要存在一条条子
					flagTiao = false;
				}
			}
			else{
				//缺一色
				if(pai[i] > 0){
					//只要存在一条筒子
					flagTong = false;
				}
			}
		}
		if(pai[1] ==0 && pai[4] ==0 && pai[7] ==0 && 
				pai[10] ==0 && pai[13] ==0 && pai[16] ==0 && 
				pai[19] ==0 && pai[22] ==0 && pai[25] ==0){
			//板板胡
			banbanhu = true;
		}
    	if((flagWan || flagTiao || flagTong)){
    		//缺一色
    		quyise = true;
    	}
		return flag;
    }
    
    private List<AvatarVO> getAvatarVoList(){
        List<AvatarVO> result = new ArrayList<>();
        for (int m = 0; m < playerList.size(); m++) {
            result.add(playerList.get(m).avatarVO);
        }
        return result;
    }
    /**
     * 清理玩家身上的牌数据
     */
    private void cleanPlayListCardData(){
        for(int i=0;i<playerList.size();i++){
            playerList.get(i).cleanPaiData();
        }
    }
    
    private boolean checkHu(Avatar avatar,Integer cardIndex){
		System.out.println(avatar.avatarVO.getAccount().getOpenid() + "checkhu - begin" + cardIndex);

		//根据不同的游戏类型进行不用的判断
		boolean flag = false;
		//处理胡牌的逻辑
		if(cardIndex!=-1&&cardIndex!=100)
			avatar.putCardInList(cardIndex);
		int [][] paiList =  avatar.getPaiArray();
		//可七小队
		flag = flag || checkSevenDouble(paiList.clone()) > 0;
		flag = flag || checkThirteen(paiList.clone());
		flag = flag || normalHuPai.checkHu(paiList.clone());

		if(cardIndex!=-1&&cardIndex!=100)
			avatar.pullCardFormList(cardIndex);
		System.out.println(avatar.avatarVO.getAccount().getOpenid() + "checkhu - end -" + flag);

		return flag;
	}
    
    private Map<String,Integer> checkHu2(Avatar avatar,Integer cardIndex){
        //根据不同的游戏类型进行不用的判断
       Map<String,Integer> result = new HashMap<String,Integer>();
     //处理胡牌的逻辑
       int [][] paiList =  avatar.getPaiArray();
   			//可七小队
   			int isSeven = checkSevenDouble(paiList.clone());
               if(isSeven == 0){
                   //System.out.println("没有七小对");
            	   if(checkThirteen(paiList.clone())){
            		   result.put("Hu", 1);//
            		   result.put(Rule.Hu_shisanyao, 1);
            	   }else{//常规胡法
            		   if(normalHuPai.checkHu(paiList.clone())){
            			   result.put("Hu", 1);
            		   }else{
            			   result.put("Hu", 0);
            		   }
            		   
            	   }
               }else if(isSeven ==1){
            	   result.put("Hu", 1);
            	   result.put(Rule.Hu_qxd, 1);
               }else{
            	   result.put("Hu", 1);
            	   result.put(Rule.Hu_haohuaqxd, 1);
               }
               if(result.get("Hu")==1){//开始检查各种名堂
            	   int i = checkQys(paiList.clone());//检查是否清一色
            	   if(i==1)
            		   result.put(Rule.Hu_qingyise, 1);
            	   if(i==2)
            		   result.put(Rule.Hu_hunyise, 1);
            	   if(checkLong(paiList.clone()))//检查是否一条龙
            		   result.put(Rule.Hu_yitiaolong, 1);
            	   int quemen = checkQuemen(paiList.clone());
            	   if(quemen>0)
            	   //检查是否缺够边坎钓
            		   result.put(Rule.Hu_quemen, quemen);
            	   if(checkGouzhang(paiList.clone()))
            		   result.put(Rule.Hu_gouzhang, 1);
            	   if(cardIndex==4&&checkKan5(paiList.clone()))
            		   result.put(Rule.Hu_kanwuwan, 1);
            	   if(checkBkd(paiList.clone(),cardIndex))
            		   result.put(Rule.Hu_biankandiao, 1);
            	   if(checkMenqing(paiList.clone(),avatar))
            		   result.put(Rule.Hu_menqing, 1);
            	   if(checkPengPeng(paiList.clone(),avatar))
            		   result.put(Rule.Hu_pengpeng, 1);
            	   int flowers = checkFlower(paiList.clone(),avatar);
            	   if(flowers>0)
            		   result.put(Rule.CaiShen, flowers);//查看里面花牌的张数
            	   return result;//胡牌了返回结果
               }
               else
            	   return null;//没有胡牌直接返回空
       
       }
    private int checkFlower(int[][] paiList,Avatar avatar){//检查花牌张数
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	int indexMain = 0;
    	for(Avatar avator:playerList){
    		if(avator.avatarVO.isMain()){
    			indexMain = playerList.indexOf(avator);//庄家序号
    			break;
    		}
    	}
    	int indexCur = playerList.indexOf(avatar);//自家序号
    	int dis = indexCur - indexMain;
    	if(dis<0)
    		dis = dis+4;
    	int flag = 1+dis;
    	int count = 0;
    	for(int i=27;i<pai.length;i++){
    		if(i>33&&pai[i]==flag){
    			count++;
    		}
    	}
    	return count;
    }
    
    
    private boolean checkPengPeng(int[][] paiList,Avatar avatar){
    	boolean result = true;
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	int jiang = 0;
    	for(int i=0;i<pai.length;i++){
    		if(i<34&&pai[i]==2){
    			jiang++;
    			if(jiang>1)
    				return false;
    		}else if(i<34&&(pai[i]>=3||pai[i]==0)){
    			continue;
    		}else{
    			return false;
    		}
    	}
    	return result;
    }
    
    

    
    
    private boolean checkMenqing(int[][] paiList,Avatar avatar){
    	boolean result = true;
    	int[] pai2 = GlobalUtil.CloneIntList(paiList[1]);
    	for(int i=0;i<pai2.length;i++){
    		if((pai2[i]==1||pai2[i]==4)&&i<34){//有吃牌或者碰牌
    			result = false;
    			return result;
    		}
    	}
    		List mingang =avatar.avatarVO.getHuReturnObjectVO().getGangAndHuInfos().get("5");
    		if(mingang!=null&&mingang.size()>0){//有明杠
    			result = false;
    			return result;
    		}
    	return result;
    }
    
    private boolean checkBkd(int[][] paiList,Integer cardIndex){
    	boolean result = false;
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	int flag = cardIndex/9;
    	//先判断边,分为左边和右边
    	if(cardIndex-2>=flag*9&&pai[cardIndex-1]>0&&pai[cardIndex-2]>0){//右边
    		pai[cardIndex]--;
    		pai[cardIndex-1]--;
    		pai[cardIndex-2]--;
    		if(normalHuPai.isHuPai(pai))
    			return true;
    	}else if(cardIndex+2<(flag+1)*9&&pai[cardIndex+1]>0&&pai[cardIndex+2]>0){//左边
    		pai[cardIndex]--;
    		pai[cardIndex+1]--;
    		pai[cardIndex+2]--;
    		if(normalHuPai.isHuPai(pai))
    			return true;
    	}
    	//然后判断坎
    	if(cardIndex-1>=flag*9&&cardIndex+1<(flag+1)*9&&pai[cardIndex+1]>0&&pai[cardIndex-1]>0){
    		pai[cardIndex]--;
    		pai[cardIndex+1]--;
    		pai[cardIndex-1]--;
    		if(normalHuPai.isHuPai(pai))
    			return true;
    	}
    	//最后判断钓
    	if(pai[cardIndex]>=2){
    		pai[cardIndex]-=2;
    		normalHuPai.setJIANG(1);
    		if(normalHuPai.isHuPai(pai))
    			return true;
    	}
    	
    	return result;
    }
    
    
    private boolean checkKan5(int[][] paiList){//判断视否坎五万
    	boolean result = false;
    	int[] pai =GlobalUtil.CloneIntList(paiList[0]);
    	if(pai[3] >= 1 && pai[5] >= 1){
    		pai[3]--;
    		pai[4]--;
    		pai[5]--;
    		if(normalHuPai.isHuPai(pai))
    			return true;
    	}
    	if(pai[4] >= 2){
    		pai[4] -=2 ;
    		normalHuPai.setJIANG(1);
    		if(normalHuPai.isHuPai(pai)){
    			normalHuPai.setJIANG(0);
    			return true;
    		}
    		normalHuPai.setJIANG(0);
    	}
        return result;
    	
    }
    
    private boolean checkGouzhang(int[][] paiList){
    	boolean result = false;
    	int flag=0;
    	for(int i=0;i<27;i++){
    		if(paiList[0][i]>0){
    			flag+=paiList[0][i];
    		}
    		if(i%9==8&&flag>=8){
    			return true;
    		}else{
    			flag = 0;
    			continue;
    		}
    	}
    	return result;
    }
    
    private int checkQuemen(int[][] paiList){//检测是否缺门
//    	boolean result = true;
    	int result = 3;
    	for(int i=0;i<9;i++){
    		if(paiList[0][i]>0){
    			result --;
    			break;
    		}
    	}
//    	if(!result)
    	for(int i=9;i<18;i++){
    		if(paiList[0][i]>0){
    			result --;
    			break;
    		}
    	}
//    	else{
//    		return true;
//    	}
//    	if(!result)
    	for(int i=18;i<27;i++){
    		if(paiList[0][i]>0){
    			result --;
    			break;
    		}
    	}
//    	else{
//    		return true;
//    	}
    	return result;
    }
    
    private boolean checkLong(int[][] paiList){//检查是否一条龙
    	boolean result = true;
    	for(int i=0;i<9;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}
    	if(!result){
    	result = true;
    	for(int i=9;i<18;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}
    	}else{
    		return true;
    	}
    	if(!result){
    	result = true;
    	for(int i=18;i<27;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}
    	}else{
    		return true;
    	}
    	return result;
    }
    
    private int checkQys(int[][] paiList){//清一色为1，混一色为2，没有返回0
    	int flag=-1;
    	int hunflag = -1;
    	int result = 1;//标识清一色
    	for(int i=0;i<paiList[0].length;i++){
    		int curflag = i/9;
    		if(paiList[0][i]>=1&&i<34){
    			if(flag==-1){
    				flag = curflag;
    				continue;
    			}else if(flag!=-1&&flag!=curflag){//不是清一色
    				if(i>26&&hunflag==-1){
    					hunflag = curflag;
    					result = 2;
    					continue;
    				}else if(i>26&&hunflag!=-1&&hunflag==curflag){
    					continue;
    				}else{
    					return 0;
    				}
    			}else{
    				continue;
    			}
    		}
    	}
    	return result;
    	
    }

    
    /**
     * 最后胡牌的检测胡牌的时候在牌组中提出条碰，杠的牌组再进行验证
     * @param paiList
     * @return
     */
    public int[] cleanGangAndPeng(int [] paiList ,Avatar avatar){
    	
    	String str;
    	String strs[];
    	int cardIndex;
    	if((str =avatar.getResultRelation().get(1)) != null){
    		//踢出碰的牌组
    		strs = str.split(",");
    		for (String string : strs) {
				cardIndex = Integer.parseInt(string.split(":")[1]);
				if(paiList[cardIndex] >=3){
					paiList[cardIndex] = paiList[cardIndex] -3;
				}
				else{
					//system.out.println("出现碰了的牌不在手牌中的错误情况!");
				}
			}
    	}
    	
    	if(avatar.getResultRelation().get(2) != null){
    		//踢出杠的牌组
    		//踢出碰的牌组
    		strs = str.split(",");
    		for (String string : strs) {
				cardIndex = Integer.parseInt(string.split(":")[1]);
				if(paiList[cardIndex] ==4){
					paiList[cardIndex] = 0;
				}
				else{
					//system.out.println("出现碰了的牌不在手牌中的错误情况!");
				}
			}
    	}
		return paiList;
    }
    
    
    
    /**
     * 
     * @param paiList
     * @return
     */
    String getString(int[] paiList){
        String result = "int string = ";
        for(int i=0;i<paiList.length;i++){
            result += paiList[i];
        }
        return result;
    }
    /**
     * 检查是否十三幺
     * @param paiList
     * @return
     */
    private final static int[] idxs = {0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33};
	public boolean checkThirteen(int[][] paiList){
		boolean rv = true;
        int i = 0;

        while (rv && i < idxs.length) {
            rv = paiList[0][idxs[i++]] >= 1;
        }
		return rv;
	}

    /**
     * 检查是否七小对胡牌
     * @param paiList
     * @return 0-没有胡牌。1-普通七小对，2-龙七对
     */
    public int checkSevenDouble(int[][] paiList){
        int result = 1;
        
        	for(int i=0;i<paiList[0].length&&i<34;i++){
        		if(paiList[0][i] != 0){
        			if(paiList[0][i] != 2 && paiList[0][i] != 4){
        				return 0;
        			}else{
        				if(paiList[1][i] != 0){
        					return 0;
        				}else {
        					if (paiList[0][i] == 4) {
        						result = 2;
        					}
        				}
        			}
        		}
        	}
        return result;
    }


    /**
     * 前后端握手消息处理，前段接收到消息则会访问整个握手接口，说明接收到信息了
     * 然后后台从list里面移除这个用户对应的uuid，
     * 到最后list里面剩下的就表示前段还没有接收到消息，
     * 则重新发送消息
     * @param avatar
     */
    public void shakeHandsMsg(Avatar  avatar){
    	shakeHandsInfo.remove(avatar.getUuId());
    	
    }
    
    /**
     * 玩家玩游戏时断线重连
     * @param avatar
     */
    public void returnBackAction(Avatar avatar){
    		RoomVO room = roomVO.clone();
    		List<AvatarVO> lists = new ArrayList<AvatarVO>();
    		for (int i = 0; i < playerList.size(); i++) {
    			if(playerList.get(i).getUuId() != avatar.getUuId()){
    				//给其他三个玩家返回重连用户信息
    				playerList.get(i).getSession().sendMsg(new OtherBackLoginResonse(1, avatar.getUuId()+""));
    			}
    			lists.add(playerList.get(i).avatarVO);
    		}
    		//给自己返回整个房间信息
    		AvatarVO avatarVo = null ;
    		List<AvatarVO> playerLists = new ArrayList<AvatarVO>();
    		for (int j = 0; j < lists.size(); j++) {
    			int paiCount = 0;//有多少张普通牌
    			avatarVo = lists.get(j);
    			if(avatarVo.getAccount().getUuid() != avatar.getUuId()){
    				//其他三家的牌组需要处理，不能让重连的玩家知道详细的牌组
    				for (int k = 0; k < avatarVo.getPaiArray()[0].length; k++) {
    					if(avatarVo.getPaiArray()[0][k] != 0 && avatarVo.getPaiArray()[1][k] == 0){
    						paiCount= paiCount +avatarVo.getPaiArray()[0][k];
    						//avatarVo.getPaiArray()[0][k] = 0;
    					}
    				}
    				avatarVo.setCommonCards(paiCount);
    				playerLists.add(avatarVo);
    				
    			}
    			else{
    				//不需要处理自己的牌组
    				playerLists.add(avatarVo);
    			}
    		}
    		if(playerList.size() == 3){
    			playerList.add(avatar);
    		}
    		if(playerLists.size() == 3){
    			playerLists.add(avatar.avatarVO);
    		}
    		/*else{
    		for (int i = 0; i < playerLists.size(); i++) {
				if(playerLists.get(i).getAccount().getUuid() == avatar.getUuId() ){
					playerLists.remove(i);
					playerLists.add(avatar.avatarVO);;
				}
			}
    	}*/
    		room.setPlayerList(playerLists);
    		avatar.getSession().sendMsg(new BackLoginResponse(1, room));
    		//lastAvtar.getSession().sendMsg(responseMsg);
    		
    	
    }
    /**
     * 断线重连返回最后操作信息
     * @param avatar
     */
    public void LoginReturnInfo(Avatar avatar){
    	//断线重连之后，该进行的下一步操作，json存储下一步操作指引
    	JSONObject json = new JSONObject();//
    	StringBuffer sb = new StringBuffer();
    	if(huAvatar.contains(avatar)){
    		//这里需要判断是自摸胡，还是别人点炮胡
    		if(pickAvatarIndex != curAvatarIndex){
    			//自摸
    			json.put("currentCardPoint", currentCardPoint);//当前摸的牌点数
    			json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
        		json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    		}
    		else{
    			//点炮
    			json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
    			json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    		}
//    		if(qianghu){
//    			sb.append("qianghu:"+putOffCardPoint+",");
//    			//system.out.println("抢胡");
//    		}
//    		else{
    			sb.append("hu,");
    			//system.out.println("胡");
//    		}
    	}
    	if(penAvatar.contains(avatar)){
    		sb.append("peng,");
    		json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
    		json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    		//system.out.println("碰");
    	}
    	if(gangAvatar.contains(avatar)){
    		//这里需要判断是别人打牌杠，还是自己摸牌杠
    		StringBuffer gangCardIndex = new StringBuffer();
    		List<Integer> gangIndexs = avatar.gangIndex;
			for (int i = 0; i < gangIndexs.size(); i++) {
				gangCardIndex.append(":"+gangIndexs.get(i));
			}
    		if(avatar.getUuId() == playerList.get(pickAvatarIndex).getUuId()){
    			//自摸杠
    			sb.append("gang"+gangCardIndex.toString()+",");
    			json.put("currentCardPoint", currentCardPoint);//当前摸的牌点数
    			json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
        		json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    			//system.out.println("自杠");
    		}
    		else{
    			json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
        		json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    			sb.append("gang"+gangCardIndex.toString()+",");
    			//system.out.println("点杠");
    		}
    	}
    	if(sb.length()>1){
			////system.out.println(sb);
			//该自己杠/胡/碰
			//游戏轮数
			int roundNum = RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).getCount();
    		json.put("gameRound", roundNum);//游戏轮数
    		//桌面剩余牌数
    		json.put("surplusCards", listCard.size() - nextCardindex);
    		//System.out.println(json.toString());
    		avatar.getSession().sendMsg(new ReturnOnLineResponse(1, json.toString()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			//System.out.println(sb);
			avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
		}
    	else{
    		if(avatar.getUuId() == playerList.get(pickAvatarIndex).getUuId()){
    			//该自己出牌
    			//system.out.println("自己出牌");
    			json.put("currentCardPoint", currentCardPoint);//当前摸的牌点数，当currentCardPoint = -2时  表示是碰了之后出牌
    			json.put("pickAvatarIndex", pickAvatarIndex);//当前摸牌人的索引
    			json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
    			json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    		}
    		else{
        		json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
        		json.put("pickAvatarIndex", pickAvatarIndex);//当前摸牌人的索引
        		json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    		}
    		//游戏局数
    		int roundNum = RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).getCount();
    		json.put("gameRound", roundNum);
    		//桌面剩余牌数
    		json.put("surplusCards", listCard.size() - nextCardindex);
    		//System.out.println(json.toString());
    		avatar.getSession().sendMsg(new ReturnOnLineResponse(1, json.toString()));
    	}
    	
    }
    /*
     * 清空所有数组
     */
    public void clearAvatar(){
    	huAvatar.clear();
        penAvatar.clear(); 
        gangAvatar.clear(); 
        chiAvatar.clear(); 
        qishouHuAvatar.clear(); 
    }
     /**
      * 清空除胡之外的数组
      */
    public void clearAvatarExceptHu(){
        penAvatar.clear(); 
        gangAvatar.clear(); 
        chiAvatar.clear(); 
        qishouHuAvatar.clear(); 
    }
    /**
     * 检测当，缓存数组里全部为空时，放弃操作，则不起作用
     */
    public boolean validateStatus(){
    	if(huAvatar.size() > 0 || penAvatar.size()>0 || gangAvatar.size()>0 || chiAvatar.size()>0 ||qishouHuAvatar.size()>0){
    		return true;
    	}
    	else{
    		return false;
    	}
    }
    /**
     * 第一局结束扣房卡
     */
    public void deductRoomCard(){
    	int currentCard = 0;
    	if(roomVO.getRoundNumber() == ConnectAPI.PLAYERS_NUMBER){
    		currentCard = -1;
    	}
    	else{
    		currentCard = 0 - roomVO.getRoundNumber()/8;
    	}
    	Avatar zhuangAvatar = playerList.get(0);
    	zhuangAvatar.updateRoomCard(currentCard);//开始游戏，减去房主的房卡,同时更新缓存里面对象的房卡(已经在此方法中修改)
    	int roomCard = zhuangAvatar.avatarVO.getAccount().getRoomcard();
    	zhuangAvatar.getSession().sendMsg(new RoomCardChangerResponse(1,roomCard));
    }

    private boolean checkSelfTing(Avatar avator) {
    	System.out.println("开始检查是否自听"+System.currentTimeMillis());
    	boolean rv = false;
    	for(int i = 0; i < 34; ++i) {
	    	if(checkHu(avator,i)){
	    	return true;
	    	}
    	else{
    	continue;
    	}
    	}
    	System.out.println("结束检查是否自听"+System.currentTimeMillis());
    	return rv;
    	}

	private boolean checkOtherTing(Avatar av, Integer cardIndex) {
		System.out.println("开始检查他人是否听"+System.currentTimeMillis());
		boolean rv = false;
		int[][] paiList = av.getPaiArray();
		paiList[0][cardIndex]++;

		for(int i = 0; i < 34; ++i) {
			if (paiList[0][i] == 0 || i == cardIndex) {
				continue;
			}

			paiList[0][i]--;
			rv = rv || checkSelfTing(av);
			paiList[0][i]++;

			if (rv) {
				break;
			}
		}

		paiList[0][cardIndex]--;
		System.out.println("结束检查他人是否听"+System.currentTimeMillis());
		return rv;
	}
	
	//dist＝0 表示庄，接下来1，2，3
	private int getDistToMain(Avatar avatar) {
		int indexMain = 0;
		for(Avatar av:playerList){
			if(av.avatarVO.isMain()){
				indexMain = playerList.indexOf(av);//庄家序号
				break;
			}
		}
		int indexCur = playerList.indexOf(avatar);//自家序号
		int dis = indexCur - indexMain;
		if(dis<0)
			dis = dis+4;
		return dis;
	}

	private void setFlowerCardOwnerInfo(Avatar av, int curCard) {
		for (int j = 0; j < playerList.size(); ++j) {
			playerList.get(j).getPaiArray()[0][curCard] = getDistToMain(av) + 1; //设置花牌是哪家获得
		}
	}

}
