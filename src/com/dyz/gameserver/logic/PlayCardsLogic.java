package com.dyz.gameserver.logic;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.context.ConnectAPI;
import com.context.ErrorCode;
import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ResponseMsg;
import com.dyz.gameserver.context.GameServerContext;
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

import test.java.Pai;
import test.java.PaiList;


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
     * 下张牌的索引
     */
    private int nextCardindex = 0;
    /**
     * 上一家出的牌的点数
     */
    private int putOffCardPoint = -1;
    /**
     * 当前玩家摸的牌的点数
     */
    private int currentCardPoint = -2;
    /**
     * 4家玩家信息集合
     */
    private List<Avatar> playerList;
    /**
     * 庄家
     */
    public Avatar bankerAvatar = null;
    /**
     * 上一把的庄家
     */
    public  int preBankerAvatar = -1;
	/**
	 * 房主ID
	 */
	private int theOwner;
	/**
     * 房间信息
     */
    private RoomVO roomVO;
    /**
     * 记录本次游戏是否已经胡了，控制摸牌
     */
    private boolean hasHu;

	private NormalHuPai normalHuPai;
	//是否连庄
	boolean followBanke = false;
	//跟庄的次数
	int followNumber = 0;

	 //游戏回放，
    PlayRecordGameVO playRecordGame;
    /**
     * 和前段握手，判断是否丢包的情况，丢包则继续发送信息
     *Integer为用户uuid
     */
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

	public List<Avatar> getPlayerList() {
		return playerList;
	}

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
		roomVO.setHasHu(false);
		paiCount = 34;
	    if(roomVO.isAddFlowerCard()){
			paiCount += 8;
		}

	    for(Avatar curplayer:playerList){
	    	curplayer.canHu = true;
	    	if(!roomVO.getResultScore().containsKey(""+curplayer.getUuId()))
	    	roomVO.getResultScore().put(""+curplayer.getUuId(), 0);
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
			shuffleTheCards(); //XHTEST
		//发牌
		dealingTheCards();
	}

	public void afterInitCard(){
		pickCard();
	}
	/**
	 * 随机洗牌
	 */
	public void shuffleTheCards() {
		Collections.shuffle(listCard);
		Collections.shuffle(listCard);
//		listCard = PaiList.getListCard2(listCard);//为了方便测试
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
        //本次摸得牌点数，下一张牌的点数，及本次摸的牌点数
        int tempPoint = getNextCardPoint();
        if(tempPoint != -1&&tempPoint<34) {//所摸的不是财神牌
        	PlayRecordOperation(pickAvatarIndex,tempPoint,2,-1,null,null);
        	currentCardPoint = tempPoint;
        	Avatar avatar = playerList.get(pickAvatarIndex);
        	avatar.avatarVO.setHasMopaiChupai(true);//修改出牌 摸牌状态
        	if(!avatar.avatarVO.isTing())//没有听的过牌才能摸牌重现可以接炮
        	avatar.canHu = true;
            avatar.putCardInList(tempPoint);
        	try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
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
            StringBuffer sb = new StringBuffer();
            if (avatar.checkSelfGang()&&!avatar.avatarVO.isTing()) {
            	int[][] paiarray = avatar.getPaiArray();
            	paiarray[0][tempPoint]-=4;
            	if((paiarray[1][tempPoint]==1&&checkSelfTing(avatar))||paiarray[1][tempPoint]==0){//明杠或者暗杠
            	gangAvatar.add(avatar);
            	sb.append("gang");
            	for (int i : avatar.gangIndex) {
            		sb.append(":"+i);
				}
            	sb.append(",");
            	}
            	paiarray[0][tempPoint]+=4;
            	
            }
            //判断自己摸上来的牌自己是否可以胡
            if(checkAvatarIsHuPai(avatar,100,"mo")){
            	huAvatar.add(avatar);
            	sb.append("hu,");
            }else if(avatar.avatarVO.isTing()){
            	sb.append("chu:"+tempPoint);
            }
            if(sb.length()>2){
				avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
            }
            
        }else if(tempPoint != -1&&tempPoint>=34) {//所摸的是花牌的处理
        	PlayRecordOperation(pickAvatarIndex,tempPoint,2,-1,null,null);//回放记录
        	Avatar avatar = playerList.get(pickAvatarIndex);
        	setFlowerCardOwnerInfo(avatar, tempPoint);
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
        if(tempPoint != -1&&tempPoint<34) {
        	//int avatarIndex = playerList.indexOf(avatar); // 2016-8-2注释
        	pickAvatarIndex = playerList.indexOf(avatar);
        	// Avatar avatar = playerList.get(pickAvatarIndex);
        	if(!avatar.avatarVO.isTing())//没有听的过牌才能摸牌重现可以接炮
        	avatar.canHu = true;
            //记录摸牌信息
            for(int i=0;i<playerList.size();i++){
                if(i != pickAvatarIndex){
					playerList.get(i).getSession().sendMsg(new OtherPickCardResponse(1,pickAvatarIndex));
                }else {
                	playerList.get(i).gangIndex.clear();//每次出牌就先清除缓存里面的可以杠的牌下标
					playerList.get(i).getSession().sendMsg(new PickCardResponse(1, tempPoint));
					//摸牌之后就重置可否胡别人牌的标签
//					playerList.get(i).canHu = true;
				}
            }
            //记录摸牌信息
            PlayRecordOperation(pickAvatarIndex,currentCardPoint,2,-1,null,null);
            
            //判断自己摸上来的牌自己是否可以胡
            StringBuffer sb = new StringBuffer();
            //摸起来也要判断是否可以杠，胡
            avatar.putCardInList(tempPoint);
            if (avatar.checkSelfGang()) {
            	int[][] paiarray = avatar.getPaiArray();
            	paiarray[0][tempPoint]-=4;
            	if((paiarray[1][tempPoint]==1&&checkSelfTing(avatar))||paiarray[1][tempPoint]==0){//明杠或者暗杠
            	gangAvatar.add(avatar);
            	sb.append("gang");
            	for (int i : avatar.gangIndex) {
            		sb.append(":"+i);
				}
            	sb.append(",");
            	}
            	paiarray[0][tempPoint]+=4;
            }
            if(checkAvatarIsHuPai(avatar,100,"ganghu")){
            	//检测完之后不需要移除
            	huAvatar.add(avatar);
            	sb.append("hu,");
            }
            if(sb.length()>2){
				avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
            }
            
        }else if(tempPoint != -1&&tempPoint>=34){
        	pickAvatarIndex = playerList.indexOf(avatar);
        	PlayRecordOperation(pickAvatarIndex,currentCardPoint,2,-1,null,null);//回放记录
        	setFlowerCardOwnerInfo(avatar, currentCardPoint);
        	for(int i=0;i<playerList.size();i++){//通知所有玩家摸到一张花牌
                if(i != pickAvatarIndex){
                    playerList.get(i).getSession().sendMsg(new PickFlowerCardResponse(1,pickAvatarIndex,tempPoint));//提醒其他玩家抓到花牌
                }else {
                	avatar.getSession().sendMsg(new PickCardResponse(1, tempPoint));//返回摸到的牌，客户端对花牌做额外的处理
				}
            }
//        	pickAvatarIndex = getPreAvatarIndex();//当前摸牌人归位
        	pickCardAfterGang(avatar);//继续摸牌处理
        	return;
        }
        else{
        	//流局
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
        if(nextIndex < 0){
            nextIndex = 3;
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
    		if(pickAvatarIndex == playerList.indexOf(avatar)){
    			//如果是自己摸的过，则 canHu = true；
    			avatar.canHu = true;
    			clearAvatar();
    		}else{
    			if(huAvatar.contains(avatar)){
    				huAvatar.remove(avatar);
    				avatar.canHu = false;
    			}
    			if(gangAvatar.contains(avatar)){
    				gangAvatar.remove(avatar);
    				avatar.gangIndex.clear();
    			}
    			if(penAvatar.contains(avatar)){
    				penAvatar.remove(avatar);
    			}
    			if(chiAvatar.contains(avatar)){
    				chiAvatar.remove(avatar);
    			}

    			if(huAvatar.size() == 0){
    				for(Avatar item : gangAvatar){
    					if (item.gangQuest) {
//    						avatar.qiangHu = false;
    						//进行这个玩家的杠操作，并且把后面的碰，吃数组置为0;
    						gangCard(item,putOffCardPoint,1);
    						clearArrayAndSetQuest();
    						return;
    					}
    				}
    				for(Avatar item : penAvatar) {
    					if (item.pengQuest) {
    						//进行这个玩家的碰操作，并且把后面的吃数组置为0;
    						pengCard(item, putOffCardPoint);
    						clearArrayAndSetQuest();
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
    						return;
    					}
    				}
    			}else if(huAvatar.size() == 1&&huAvatar.get(0).huQuest){
					huPai(huAvatar.get(0),putOffCardPoint,"0");
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
//    	avatar.avatarVO.setHuType(0);//重置划水麻将胡牌格式
    	//出牌信息放入到缓存中，掉线重连的时候，返回房间信息需要
        avatar.avatarVO.updateChupais(cardPoint);
        avatar.avatarVO.setHasMopaiChupai(true);//修改出牌 摸牌状态
    	//已经出牌就清除所有的吃，碰，杠，胡的数组
    	clearAvatar();
    	
        putOffCardPoint = cardPoint;
        curAvatarIndex = playerList.indexOf(avatar);
    	PlayRecordOperation(curAvatarIndex,cardPoint,1,-1,null,null);
        avatar.pullCardFormList(putOffCardPoint);
        for(int i=0;i<playerList.size();i++){
            //不能返回给自己
//        	playerList.get(i).canHu = true;
        	playerList.get(i).gangIndex.clear();//每次出牌就先清除缓存里面的可以杠的牌下标
            if(i != curAvatarIndex) {
                playerList.get(i).getSession().sendMsg(new ChuPaiResponse(1, putOffCardPoint, curAvatarIndex));
            } else {
				if (!roomVO.isYikouxiangCard()
						&& !avatar.avatarVO.isTing()
						&& checkSelfTing(avatar)) {
					avatar.getSession().sendMsg(new ReturnInfoResponse(1, "canting"));
				} else {
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
				if(avatar.getRoomVO().getZiMo()!= 2
						&& ava.canHu
						&& checkAvatarIsHuPai(ava,putOffCardPoint,"chu")){
					//胡牌状态为可胡的状态时才行
					huAvatar.add(ava);
					sb.append("hu,");
				}

				if (!ava.avatarVO.isTing()) {
					if (ava.checkGang(putOffCardPoint)) {
						//有听口，且杠后不影响听口
						int[][] paiarray = ava.getPaiArray();
						paiarray[0][putOffCardPoint] -= 3;
						if(checkSelfTing(ava)){
						gangAvatar.add(ava);
						//同时传会杠的牌的点数
						sb.append("gang:" + putOffCardPoint + ",");
						}
						paiarray[0][putOffCardPoint] += 3;
					}

					if (ava.checkPeng(putOffCardPoint) &&
							(!roomVO.isYikouxiangCard() || checkOtherTing(ava, putOffCardPoint, true))) {
							penAvatar.add(ava);
							sb.append("peng,");
					}

					if (roomVO.isCanchi() && getNextAvatarIndex() == i && ava.checkChi(putOffCardPoint) &&
							(!roomVO.isYikouxiangCard() || checkOtherTing(ava, putOffCardPoint, false))) {
						//只有下一家才能吃
						chiAvatar.add(ava);
						sb.append("chi");
					}
				}

				if (sb.length()>1) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					ava.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
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
    						 //吃了的牌放入到avatar的resultRelation  Map中
    						 playerList.get(i).putResultRelation(4,cardIndex+","+onePoint+","+twoPoint);
    						 playerList.get(i).avatarVO.getPaiArray()[1][cardIndex]+=4;//要留有一个地方放吃牌的其他牌
    						 playerList.get(i).avatarVO.getPaiArray()[1][onePoint]+=4;//如果一个字为4，两个字为8依次累加
    						 playerList.get(i).avatarVO.getPaiArray()[1][twoPoint]+=4;//
    					 }
    					 playerList.get(i).getSession().sendMsg(new ChiResponse(1,cardIndex+","+onePoint+","+twoPoint));
    				 }
//    				 responseMsg = new PengResponse(1,cardIndex,playerList.indexOf(avatar));
//    				 lastAvtar = avatar;
    				 //更新摸牌人信息 2016-8-3
    				 pickAvatarIndex = playerList.indexOf(avatar);
//    				 curAvatarIndex = playerList.indexOf(avatar);
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
    					 }
    					 playerList.get(i).getSession().sendMsg(new PengResponse(1,cardIndex,playerList.indexOf(avatar)));
    				 }
//    				 responseMsg = new PengResponse(1,cardIndex,playerList.indexOf(avatar));
//    				 lastAvtar = avatar;
    				 //更新摸牌人信息 2016-8-3
    				 if(avatar.avatarVO.getPaiArray()[1][cardIndex]!=4)
    				 avatar.avatarVO.getPaiArray()[1][cardIndex]=1;
    				 else
    					 avatar.avatarVO.getPaiArray()[1][cardIndex]=5;
    				 pickAvatarIndex = playerList.indexOf(avatar);
//    				 curAvatarIndex = playerList.indexOf(avatar);
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
                            avatar.avatarVO.getPaiArray()[1][cardPoint] = 2;
                            //长沙麻将
                            str = "0:"+cardPoint+":"+Rule.Gang_ming;
                            type = 0;
                            score = Rule.scoreMap.get(Rule.Gang_ming);
                            recordType ="5";
//                            endStatisticstype = "minggang";
                            avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo(Rule.Gang_ming, cardPoint+"");//记录杠的消息
                        }
                        else{//自杠暗杠
                            playRecordType = 2;
                            //存储杠牌的信息，
                            avatar.putResultRelation(2,cardPoint+"");
                            avatar.avatarVO.getPaiArray()[1][cardPoint] = 6;
//                            str = "0:"+cardPoint+":"+Rule.Gang_an;
                            type = 1;
//                            score = Rule.scoreMap.get(Rule.Gang_an);
//                            recordType ="4";
//                            endStatisticstype = "angang";
                            avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo(Rule.Gang_an, cardPoint+"");//记录杠的消息
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
                        type = 0;

                        avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo(Rule.Gang_ming, cardPoint+"");//记录杠的消息
                    }

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
    	String huType = "";
    	int paoladun = 0;//专门用来计算跑啦蹲的分数，因为包头的跑啦蹲分数要翻番后另算
		
    	int pldscore = roomVO.getPldscore();
		Map<String,Integer> huResult = checkHu2(avatar , cardIndex);//算好所有的名堂
		Map<String,String> huResult2 = new HashMap<String,String>();
		
		score+=1;//基本胡牌分数
		huResult2.put(Rule.Hu_base, "1");
		if(avatar.getUuId()==bankerAvatar.getUuId()){//庄家胡
		score+=1;
		huResult2.put(Rule.Hu_zhuang, "1");
		}
		if(followBanke&&!avatar.avatarVO.isMain()){//拉了别人的庄
			score+=1;
			huResult2.put(Rule.Hu_lazhuang, "1");
		}
		boolean dunorla = avatar.avatarVO.isDunorla();
		boolean pao = avatar.avatarVO.isRun();
		//处理跑拉蹲分数,蹲或拉不是赢全部		
		if(avatar.avatarVO.isMain()&&dunorla){//如果庄蹲了赢全部
//			huResult2.put(Rule.Hu_dun, ""+pldscore);
			paoladun+=pldscore;
		}
		if(pao){
//			huResult2.put(Rule.Hu_pao, ""+pldscore);
			paoladun+=pldscore;
		}
		//同时也要算分数------------------------------------
		int roomType = roomVO.getRoomType();
		if(roomType==4||roomType==5||roomType==6||roomType==7){//鄂尔多斯，呼和浩特和集宁玩法
			int roomScore = 5;
				if(roomType==7)	
					roomScore = 9;
			if(!huResult.containsKey(Rule.Hu_qxd)&&huResult.containsKey(Rule.Hu_menqing)){//门清
				score+=1;
			huResult2.put(Rule.Hu_menqing, "1");
			}
			if(roomVO.isAddFlowerCard()&&huResult.containsKey(Rule.CaiShen)){//财神
				score+=huResult.get(Rule.CaiShen);
				huResult2.put(Rule.CaiShen, "1*"+huResult.get(Rule.CaiShen));
			}
			//特色选项
			if(roomVO.isQgbkd()&&huResult.containsKey(Rule.Hu_quemen)){//缺门
				score+=1;
				huResult2.put(Rule.Hu_quemen, "1");
			}
			if(roomVO.isQgbkd()&&!huResult.containsKey(Rule.Hu_qingyise)&&!huResult.containsKey(Rule.Hu_yitiaolong)&&huResult.containsKey(Rule.Hu_gouzhang)){//够张
				score+=1;
				huResult2.put(Rule.Hu_gouzhang, "1");
			}
			if(huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isKan5()){//坎五万
				score+=5;
				huResult2.put(Rule.Hu_kanwuwan, "5");
			}
			if(roomVO.isQgbkd()&&!huResult.containsKey(Rule.Hu_qxd)&&huResult.containsKey(Rule.Hu_biankandiao)&&!huResult.containsKey(Rule.Hu_kanwuwan)){//边砍钓
				score+=1;
				huResult2.put(Rule.Hu_biankandiao, "1");
			}
			if(huResult.containsKey(Rule.Gang_ming)){
				score+=huResult.get(Rule.Gang_ming);
				huResult2.put(Rule.Gang_ming, "1*"+huResult.get(Rule.Gang_ming));
			}
			if(huResult.containsKey(Rule.Gang_an)){
				score+=2*huResult.get(Rule.Gang_an);
				huResult2.put(Rule.Gang_an, "2*"+huResult.get(Rule.Gang_an));
			}
			//处理胡牌方法
			if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==1){//清一色
				score+=roomScore;
				huResult2.put(Rule.Hu_qingyise, ""+roomScore);
			}
			if(huResult.containsKey(Rule.Hu_qxd)){//七小对
				score+=roomScore;
				huResult2.put(Rule.Hu_qxd, ""+roomScore);
			}
			if(huResult.containsKey(Rule.Hu_yitiaolong)){//一条龙
				score+=roomScore;
				huResult2.put(Rule.Hu_yitiaolong, ""+roomScore);
			}
			if(huResult.containsKey(Rule.Hu_pengpeng)&&roomVO.isPengpeng()){//碰碰胡
				score+=roomScore;
				huResult2.put(Rule.Hu_pengpeng, ""+roomScore);
			}
			if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==2&&roomVO.isHunyise()){//混一色
				score+=roomScore;
				huResult2.put(Rule.Hu_hunyise, ""+roomScore);
			}
			if(huResult.containsKey(Rule.Hu_haohuaqxd)){//豪华七对
				score+=10;
				huResult2.put(Rule.Hu_haohuaqxd, "10");
			}
			if(huResult.containsKey(Rule.Hu_shisanyao)){//十三幺
				score+=13;
				huResult2.put(Rule.Hu_shisanyao, "13");
			}
				for(Avatar player:playerList){//分别处理四个用户的赢输牌的分数
					int calscore = 0;
					if(player.getUuId()!=avatar.getUuId()){//如果不是当前用户
					//开始处理各个不同玩家不同的结算信息包括
					Map<String,String> curMap = new HashMap<String,String>();
					if(dian == -1){//为自摸
						huResult2.put(Rule.Hu_zimo, "1");
						curMap.putAll(huResult2);
						recordType = "1";
						huType = "zimo";
						//加分项逻辑
						calscore+=1;//自摸加两分
						player.avatarVO.getHuReturnObjectVO().updateTotalInfo("bierenzimo", cardIndex+"");
//						curMap.put(Rule.Hu_bierenzimo, "1");
					}else{//为点炮
						if(playerList.indexOf(player)!=dian){
							continue;
						}
						player.avatarVO.getHuReturnObjectVO().updateTotalInfo("dianpao", cardIndex+"");
						curMap.putAll(huResult2);
						huType = "dianpao";
						if(roomType==7){
							huResult2.put(Rule.Hu_jiepao, "2");
							curMap.put(Rule.Hu_dianpao, "2");
							calscore +=2;
						}
					
					}		
						
				
				boolean curdunorla = player.avatarVO.isDunorla();
				boolean curpao = player.avatarVO.isRun();
				if(curdunorla){//输家蹲或者拉了
					if(player.avatarVO.isMain()){//如果输家蹲了，输蹲
						curMap.put(Rule.Hu_dun, ""+pldscore);
						calscore+=pldscore;
						if(dunorla){//如果赢家拉了，还要加上赢家拉的分
							curMap.put(Rule.Hu_la, ""+pldscore);
							calscore+=pldscore;
						}
					}else{//如果输家拉了
						if(avatar.avatarVO.isMain()){//并且赢家是庄家
							calscore+=pldscore;
							curMap.put(Rule.Hu_la, ""+pldscore);//输家拉了且赢家是庄家需要显示拉
						}
					}
					
				}else{//输家没有蹲或者拉，但是，点炮且是庄家且赢家选择啦
					if(player.avatarVO.isMain()&&dunorla){
						curMap.put(Rule.Hu_la, ""+pldscore);//输家没有拉但是输家是庄家且赢家拉了需要显示拉
						calscore+=pldscore;
					}
				}
				if(curpao){
					curMap.put(Rule.Hu_pao, ""+pldscore);
					calscore+=pldscore;
				}

				calscore+=paoladun;
				calscore+=score;
				totalScore+=calscore;
				curMap.put("score", ""+calscore);
				player.avatarVO.getHuReturnObjectVO().updateTotalScore(-1*(calscore));
				player.avatarVO.getHuReturnObjectVO().setHuInfo(curMap);
				roomVO.updateEndStatistics(player.getUuId()+"", huType, -1*calscore);
				}
				}
				//循环结束后才处理胡牌人的逻辑
				if(dunorla){
					if(avatar.avatarVO.isMain())//赢家是庄家且赢家蹲了显示蹲
					huResult2.put(Rule.Hu_dun, ""+pldscore);
//					else
//						huResult2.put(Rule.Hu_la, ""+pldscore);
				}
				if(pao)
					huResult2.put(Rule.Hu_pao, ""+pldscore);
				if(dian==-1)
					huResult2.put(Rule.Hu_zimo, "1");
//				else//拉不是赢家显示
//					huResult2.put(Rule.Hu_jiepao, "1");
				if(huType.equals("dianpao"))
					huType = "jiepao";
				avatar.avatarVO.getHuReturnObjectVO().updateTotalScore(totalScore);
//				avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("score", ""+totalScore);
				huResult2.put("score", ""+totalScore);
				roomVO.updateEndStatistics(avatar.getUuId()+"", huType, totalScore);
			
		}else if(roomType == 8){//包头
			int roomScore = 5;
			int multiscore = 1;
		if(huResult.containsKey(Rule.Hu_menqing)){//门清加倍
			multiscore*=2;
			huResult2.put(Rule.Hu_menqing, "*2");
		}
		if(roomVO.isAddFlowerCard()&&huResult.containsKey(Rule.CaiShen)){//财神
			score+=huResult.get(Rule.CaiShen);
			huResult2.put(Rule.CaiShen, "1*"+huResult.get(Rule.CaiShen));
		}
		//特色选项
		if(huResult.containsKey(Rule.Hu_quemen)&&roomVO.isQgbkd()){//缺门
			score+=huResult.get(Rule.Hu_quemen);
			huResult2.put(Rule.Hu_quemen, "1*"+huResult.get(Rule.Hu_quemen));
		}
		if(huResult.containsKey(Rule.Hu_gouzhang)&&roomVO.isQgbkd()){//够张
			score+=1;
			huResult2.put(Rule.Hu_gouzhang, "1");
		}
		if(huResult.containsKey(Rule.Hu_biankandiao)&&!huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isQgbkd()){//边砍钓
			score+=1;
			huResult2.put(Rule.Hu_biankandiao, "1");
		}
		if(huResult.containsKey(Rule.Hu_kanwuwan)&&roomVO.isKan5()){//坎五万
			score+=roomScore;
			huResult2.put(Rule.Hu_kanwuwan, "5");
		}
		if(huResult.containsKey(Rule.Gang_ming)){
			score+=huResult.get(Rule.Gang_ming);
			huResult2.put(Rule.Gang_ming, "1*"+huResult.get(Rule.Gang_ming));
		}
		if(huResult.containsKey(Rule.Gang_an)){
			score+=2*huResult.get(Rule.Gang_an);
			huResult2.put(Rule.Gang_an, "2*"+huResult.get(Rule.Gang_an));
		}
		//处理胡牌方法
		if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==1){//清一色
			score+=roomScore*2;
			huResult2.put(Rule.Hu_qingyise, "10");
		}
		if(huResult.containsKey(Rule.Hu_qxd)){//七小对
			score+=roomScore*2;
			huResult2.put(Rule.Hu_qxd, "10");
		}
		if(huResult.containsKey(Rule.Hu_yitiaolong)){//一条龙
			score+=roomScore*2;
			huResult2.put(Rule.Hu_yitiaolong, "10");
		}
		if(huResult.containsKey(Rule.Hu_pengpeng)&&roomVO.isPengpeng()){//碰碰胡
			score+=roomScore;
			huResult2.put(Rule.Hu_pengpeng, "5");
		}
		if(huResult.containsKey(Rule.Hu_qingyise)&&huResult.get(Rule.Hu_qingyise)==2&&roomVO.isHunyise()){//混一色
			score+=roomScore;
			huResult2.put(Rule.Hu_hunyise, "5");
		}
		if(huResult.containsKey(Rule.Hu_haohuaqxd)){//豪华七对
			score+=(roomScore*4);
			huResult2.put(Rule.Hu_haohuaqxd, "20");
		}
		if(huResult.containsKey(Rule.Hu_shisanyao)){//十三幺
			score+=13;
			huResult2.put(Rule.Hu_shisanyao, "13");
		}
			for(Avatar player:playerList){//分别处理四个用户的赢输牌的分数
				int calscore = 0;
				int calmulti = 1;
				if(player.getUuId()!=avatar.getUuId()){//如果不是当前用户
					//开始处理各个不同玩家不同的结算信息包括
					Map<String,String> curMap = new HashMap<String,String>();
				if(dian == -1){//为自摸
					huResult2.put(Rule.Hu_zimo, "1");
					curMap.putAll(huResult2);
					recordType = "1";
					huType = "zimo";
					//加分项逻辑
					calmulti*=2;//自摸加倍
//					curMap.put(Rule.Hu_bierenzimo, "1");
					
					player.avatarVO.getHuReturnObjectVO().updateTotalInfo("bierenzimo", cardIndex+"");
				}else{//为点炮
					if(playerList.indexOf(player)!=dian){
						continue;
					}
					player.avatarVO.getHuReturnObjectVO().updateTotalInfo("dianpao", cardIndex+"");
					curMap.putAll(huResult2);
					huType = "dianpao";
//					curMap.put(Rule.Hu_dianpao, "底分");
//					huResult2.put(Rule.Hu_jiepao, "底分");
				}
			
			boolean curdunorla = player.avatarVO.isDunorla();
			boolean curpao = player.avatarVO.isRun();
			if(curdunorla){//输家蹲或者拉了
				if(player.avatarVO.isMain()){//如果输家蹲了，输蹲
					curMap.put(Rule.Hu_dun, ""+pldscore);
					paoladun+=pldscore;
					if(dunorla){//如果赢家拉了，还要加上赢家拉的分
						curMap.put(Rule.Hu_la, ""+pldscore);
						paoladun+=pldscore;
					}
				}else{//如果输家拉了
					if(avatar.avatarVO.isMain()){//并且赢家是庄家
						paoladun+=pldscore;
						curMap.put(Rule.Hu_la, ""+pldscore);//输家拉了且赢家是庄家需要显示拉
					}
				}
				
			}else{//输家没有蹲或者拉，但是，点炮且是庄家且赢家选择啦
				if(player.avatarVO.isMain()&&dunorla){
					curMap.put(Rule.Hu_la, ""+pldscore);//输家没有拉但是输家是庄家且赢家拉了需要显示拉
					paoladun+=pldscore;
				}
			}
			if(curpao){
				curMap.put(Rule.Hu_pao, ""+pldscore);
				paoladun+=pldscore;
			}
	
			calscore+=score;
			calmulti*=multiscore;
			calscore*=calmulti;
			calscore+=paoladun;
			totalScore+=calscore;
			player.avatarVO.getHuReturnObjectVO().updateTotalScore(-1*calscore);
			curMap.put("score", ""+calscore);
			player.avatarVO.getHuReturnObjectVO().setHuInfo(curMap);
			roomVO.updateEndStatistics(avatar.getUuId()+"", huType, -1*calscore);
				}
			}
			if(dunorla){
				if(avatar.avatarVO.isMain())
				huResult2.put(Rule.Hu_dun, ""+pldscore);
//				else//拉不是赢家显示
//					huResult2.put(Rule.Hu_la, ""+pldscore);
			}
			if(pao)
				huResult2.put(Rule.Hu_pao, ""+pldscore);
			if(dian==-1)
				huResult2.put(Rule.Hu_zimo, "1");
			if(huType.equals("dianpao"))
				huType = "jiepao";
			avatar.avatarVO.getHuReturnObjectVO().updateTotalScore(totalScore);
			huResult2.put("score", ""+totalScore);
			roomVO.updateEndStatistics(avatar.getUuId()+"", huType, totalScore);
		}
		avatar.avatarVO.getHuReturnObjectVO().setHuInfo(huResult2); 
    	return 0;
    }
    /**
     *胡牌
     * @param avatar
     * @return
     */
    public boolean huPai(Avatar avatar , int cardIndex,String type){
    	boolean flag = false;
    	//当胡家手上没有红中，则多抓一个码
    	int playRecordType = 6;
    	if(hasHu)
    		return false;
    	if(huAvatar.size() > 0) {	
    		
   		 if(huAvatar.contains(avatar)){
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
    				if(avatar.canHu&&!needWait){
    					clearAvatar();
    					flag = true;
    				}
    				else{
    					avatar.huQuest = true;
    					return false;
    				}
    				avatar.putCardInList(cardIndex);
    				avatar.getPaiArray()[1][cardIndex] = 3;
    				avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("jiepao", cardIndex+"");
    				playerList.get(curAvatarIndex).avatarVO.getHuReturnObjectVO().updateTotalInfo("dianpao", cardIndex+"");
    				calculateScore(avatar , cardIndex,curAvatarIndex);
    			}
    			else{
    				clearAvatar();
    				avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("zimo", ""+cardIndex);
    				flag = true;
    				calculateScore(avatar , cardIndex,-1);
    			}
    			hasHu = true;
    			roomVO.setHasHu(true);
    			PlayRecordOperation(playerList.indexOf(avatar),cardIndex,playRecordType,-1,null,null);
   		 	}
   	 	}else{
   	 		return false;
   	 	}
    		//更新roomlogic的PlayerList信息
     		if(avatar.getUuId()==bankerAvatar.getUuId()){//当前庄家胡了牌，那么算跟庄,不重新分配庄家
     			followBanke = true;
     			preBankerAvatar = playerList.indexOf(bankerAvatar);
     			followNumber++;
     		}else{//重新分配庄家为庄家的下家
     			followBanke = false;
     			followNumber = 0;
     			preBankerAvatar = playerList.indexOf(bankerAvatar);
     			bankerAvatar.avatarVO.setMain(false);
     			int curbank = playerList.indexOf(bankerAvatar);
     			curbank++;
     			if(curbank>=4)
     				curbank = 0;
     			bankerAvatar = playerList.get(curbank);
     			bankerAvatar.avatarVO.setMain(true);
     			
     		}
     		for(Avatar avator:playerList){
 				avator.avatarVO.setTing(false);
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
    		deductRoomCard();//因为测试所以注释这行
    	}
    	if("1".equals(type))
    	for(Avatar avator:playerList){
				avator.avatarVO.setTing(false);
			}
    	JSONArray array = new JSONArray();
    	JSONObject json = new JSONObject();
    	StandingsDetail standingsDetail = new StandingsDetail();
    	StringBuffer content = new StringBuffer();
    	StringBuffer score = new StringBuffer();
    	Map<String,Integer> avatarPosition = new HashMap<String,Integer>();
    	for (Avatar avatar : playerList) {
    		HuReturnObjectVO   huReturnObjectVO = avatar.avatarVO.getHuReturnObjectVO();
    		//生成战绩内容
    		content.append(avatar.avatarVO.getAccount().getNickname()+":"+huReturnObjectVO.getTotalScore()+",");
    		
    		//统计本局分数
    		huReturnObjectVO.setNickname(avatar.avatarVO.getAccount().getNickname());
    		huReturnObjectVO.setPaiArray(avatar.avatarVO.getPaiArray()[0]);
    		huReturnObjectVO.setUuid(avatar.getUuId());
    		avatarPosition.put(""+avatar.getUuId(),(playerList.indexOf(avatar)));
    		array.add(huReturnObjectVO);
    		//在整个房间信息中修改总分数(房间次数用完之后的总分数)
    		roomVO.updateEndStatistics(avatar.getUuId()+"", "scores", huReturnObjectVO.getTotalScore());
    		score.append(avatar.getUuId()+":"+ roomVO.getEndStatistics().get(avatar.getUuId()+"").get("scores")+",");
    		//修改存储的分数(断线重连需要)
    		avatar.avatarVO.supdateScores(huReturnObjectVO.getTotalScore());
		}
    	json.put("avatarList", array);
    	json.put("bankerAvatar", playerList.indexOf(bankerAvatar));//新增庄家位置索引
    	json.put("preBankerAvatar", preBankerAvatar);//新增庄家位置索引
    	json.put("avatarPosition", avatarPosition);
    	json.put("count", roomVO.getCurrentRound());//已经进行的局数
    	json.put("type", type);
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
				int roomscore = roomVO.getResultScore().get(param.getKey());
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
						obj.setScores(roomscore);
						sb.append(":"+roomscore+",");
						break;
					default:
						break;
					}
				}
				list.add(obj);
			}
			js.put("totalInfo", list);
		  	js.put("theowner",theOwner);
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
		
		curAvatarIndex = playerList.indexOf(bankerAvatar);
		curAvatarIndex = getPreAvatarIndex();
		pickAvatarIndex = curAvatarIndex;
		PlayRecordInit();

    }
    
    /**
     * 游戏回放，记录 房间信息和初始牌组，玩家信息
     */
    public void PlayRecordInit(){
    	 playRecordGame = new PlayRecordGameVO();
    	 RoomVO roomVo = roomVO;
//    	 roomVo.setEndStatistics(new HashMap<String, Map<String,Integer>>());
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
    
    private boolean checkHu(Avatar avatar,Integer cardIndex){

		//根据不同的游戏类型进行不用的判断
		boolean flag = false;
		//处理胡牌的逻辑
		if(cardIndex!=-1&&cardIndex!=100)
			avatar.putCardInList(cardIndex);
		int [][] paiList =  avatar.getPaiArray();
		//可七小队
		flag = flag || checkSevenDouble(paiList,cardIndex) > 0;
		flag = flag || checkThirteen(paiList);
		flag = flag || normalHuPai.checkHu(paiList);

		if(cardIndex!=-1&&cardIndex!=100)
			avatar.pullCardFormList(cardIndex);

		return flag;
	}
    
    private Map<String,Integer> checkHu2(Avatar avatar,Integer cardIndex){
        //根据不同的游戏类型进行不用的判断
       Map<String,Integer> result = new HashMap<String,Integer>();
     //处理胡牌的逻辑
       int [][] paiList =  avatar.getPaiArray();
   			//可七小队
   			int isSeven = checkSevenDouble(paiList,cardIndex);
               if(isSeven == 0){
            	   if(checkThirteen(paiList)){
            		   result.put("Hu", 1);//
            		   result.put(Rule.Hu_shisanyao, 1);
            	   }else{//常规胡法
            		   if(normalHuPai.checkHu(paiList)){
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
            	   int i = checkQys(paiList);//检查是否清一色
            	   if(i==1)
            		   result.put(Rule.Hu_qingyise, 1);
            	   if(i==2)
            		   result.put(Rule.Hu_hunyise, 1);
            	   if(checkLong(paiList))//检查是否一条龙
            		   result.put(Rule.Hu_yitiaolong, 1);
            	   int quemen = checkQuemen(paiList);
            	   if(quemen>0)
            	   //检查是否缺够边坎钓
            		   result.put(Rule.Hu_quemen, quemen);
            	   if(checkGouzhang(paiList))
            		   result.put(Rule.Hu_gouzhang, 1);
            	   if(cardIndex==4&&checkKan5(paiList))
            		   result.put(Rule.Hu_kanwuwan, 1);
            	   if(checkBkd(paiList,cardIndex))
            		   result.put(Rule.Hu_biankandiao, 1);
            	   if(checkMenqing(paiList,avatar))
            		   result.put(Rule.Hu_menqing, 1);
            	   if(checkPengPeng(paiList,avatar))
            		   result.put(Rule.Hu_pengpeng, 1);
            	   if(checkMingGang(avatar)>0)//加入算明暗杠的逻辑
            		   result.put(Rule.Gang_ming, checkMingGang(avatar));
            	   if(checkAnGang(avatar)>0)
            		   result.put(Rule.Gang_an, checkAnGang(avatar));
            	   int flowers = checkFlower(paiList,avatar);
            	   if(flowers>0)
            		   result.put(Rule.CaiShen, flowers);//查看里面花牌的张数
            	   return result;//胡牌了返回结果
               }
               else
            	   return null;//没有胡牌直接返回空
       
       }
    
    private int checkMingGang(Avatar avatar){
    	String ming = avatar.avatarVO.getHuReturnObjectVO().getTotalInfo().get(Rule.Gang_ming);
    	if(ming!=null&&!"".equals(ming)){
    	String[] mings = ming.split(",");
    	return mings.length;
    	}
    	return 0;
    }
    
    private int checkAnGang(Avatar avatar){
    	String ming = avatar.avatarVO.getHuReturnObjectVO().getTotalInfo().get(Rule.Gang_an);
    	if(ming!=null&&!"".equals(ming)){
    	String[] mings = ming.split(",");
    	return mings.length;
    	}
    	return 0;
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
    		if((pai2[i]==1||pai2[i]==4||pai2[i]==5||(pai2[i]%4==0&&pai2[i]/4>0))&&i<34){//有吃牌或者碰牌
    			result = false;
    			return result;
    		}
    	}
    		String mingang =avatar.avatarVO.getHuReturnObjectVO().getTotalInfo().get(Rule.Gang_ming);
    		if(mingang!=null&&!"".equals(mingang)){//有明杠
    			result = false;
    			return result;
    		}
    	return result;
    }
    
    private boolean checkBkd(int[][] paiList,Integer cardIndex){
    	boolean result = false;
    	int[] pai2 =GlobalUtil.CloneIntList(paiList[0]);
    	for(int i=0;i<paiList[0].length&&i<34;i++){
            if(paiList[1][i] == 1 && pai2[i] >= 3) {
                pai2[i] -= 3;
            }else if((paiList[1][i] == 2||paiList[1][i] == 6) && pai2[i] == 4){
                pai2[i]  -= 4;
            }else if(paiList[1][i]/4>0&&paiList[1][i]%4==0 && pai2[i] > 0){//吃牌的标识是4，吃几次扣几次
            	int times = paiList[1][i]/4;
            	pai2[i] = pai2[i]-times;
            }else if(paiList[1][i] == 5 && pai2[i] == 4){//碰一次并且吃一次
                pai2[i]  -= 4;
            }
        }
    	int[] pai =GlobalUtil.CloneIntList(pai2);
    	
    	//判断视否唯一听口
		for(int i=0;i<34;i++){
			pai =GlobalUtil.CloneIntList(pai2);
    		pai[cardIndex]--;
			pai[i]++;
    		if(normalHuPai.isHuPai(pai)&&i!=cardIndex){//有多个听口不算坎
    			return false;
    		}
		}
		
    	int flag = cardIndex/9;
    	//先判断边,分为左边和右边
    	if(cardIndex-2>=flag*9&&pai[cardIndex-1]>0&&pai[cardIndex-2]>0&&cardIndex%9==2){//右边,只有为3的时候
    		pai[cardIndex]--;
    		pai[cardIndex-1]--;
    		pai[cardIndex-2]--;
    		if(normalHuPai.isHuPai(pai)){
    			return true;
    		}
    		else{
    			pai[cardIndex]++;
        		pai[cardIndex-1]++;
        		pai[cardIndex-2]++;
    		}
    			
    	}else if(cardIndex+2<(flag+1)*9&&pai[cardIndex+1]>0&&pai[cardIndex+2]>0&&cardIndex%9==6){//左边，只有为7的时候
    		pai[cardIndex]--;
    		pai[cardIndex+1]--;
    		pai[cardIndex+2]--;
    		if(normalHuPai.isHuPai(pai)){
    			return true;
    		}
    		else{
    			pai[cardIndex]++;
        		pai[cardIndex+1]++;
        		pai[cardIndex+2]++;
    		}
    	}
    	//然后判断坎
    	if(cardIndex-1>=flag*9&&cardIndex+1<(flag+1)*9&&pai[cardIndex+1]>0&&pai[cardIndex-1]>0){
    		pai[cardIndex]--;
    		pai[cardIndex+1]--;
    		pai[cardIndex-1]--;
    		if(normalHuPai.isHuPai(pai)){
    			return true;
    			
    		}
    		else{
    			pai[cardIndex]++;
        		pai[cardIndex+1]++;
        		pai[cardIndex-1]++;
    		}
    	}
    	//最后判断钓
    	if(pai[cardIndex]>=2){
    		pai[cardIndex]-=2;
    		normalHuPai.setJIANG(1);
    		if(normalHuPai.isHuPai(pai)){
    			result = true;
    		}
    		normalHuPai.setJIANG(0);
    		return result;
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
    			result = true;
    		}
    		//追加判断是否只有单听口
    		normalHuPai.setJIANG(0);
    		for(int i=0;i<34;i++){
    			pai =GlobalUtil.CloneIntList(paiList[0]);
        		pai[4]--;
    			pai[i]++;
        		if(normalHuPai.isHuPai(pai)&&i!=4){//有多个听口不算单吊
        			return false;
        		}else{
        			pai[i]--;
        		}
    		}
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
    		if(i%9==8){
    			if(flag>=8)
    			return true;
    			else{
    				flag = 0;
    				continue;
    			}
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
    	if(result){//如果是在万里面有一条龙
    		for(int i=0;i<9;i++){
    			paiList[0][i]--;
        	}
    		result = normalHuPai.checkHu(paiList);
    		for(int i=0;i<9;i++){
    			paiList[0][i]++;
        	}
    		return result;
    	}
    	if(!result){
    	result = true;
    	for(int i=9;i<18;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}
    	if(result){//如果是在条里面有一条龙
    		for(int i=9;i<18;i++){
    			paiList[0][i]--;
        	}
    		result = normalHuPai.checkHu(paiList);
    		for(int i=9;i<18;i++){
    			paiList[0][i]++;
        	}
    		return result;
    	}
    	}
    	if(!result){
    	result = true;
    	for(int i=18;i<27;i++){
    		if(paiList[0][i]==0){
    			result = false;
    			break;
    		}
    	}
    	if(result){//如果是在筒里面有一条龙
    		for(int i=18;i<27;i++){
    			paiList[0][i]--;
        	}
    		result = normalHuPai.checkHu(paiList);
    		for(int i=18;i<27;i++){
    			paiList[0][i]++;
        	}
    		return result;
    	}
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
     * 检查是否十三幺
     * @param paiList
     * @return
     */
    private final static int[] idxs = {0, 8, 9, 17, 18, 26, 27, 28, 29, 30, 31, 32, 33};
	public boolean checkThirteen(int[][] paiList){
		boolean rv = true;
        int i = 0;
        int flag = 0;
        while (rv && i < idxs.length) {
            rv = paiList[0][idxs[i++]] >= 1;
            if(paiList[0][idxs[i-1]]>1)
            	flag++;
        }
        if(rv&&flag>0)
		return rv;
        else{
        	return false;
        }
	}

    /**
     * 检查是否七小对胡牌
     * @param paiList
     * @return 0-没有胡牌。1-普通七小对，2-龙七对
     */
    public int checkSevenDouble(int[][] paiList,Integer cardIndex){
        int result = 1;
        
        	for(int i=0;i<paiList[0].length&&i<34;i++){
        		if(paiList[0][i] != 0){
        			if(paiList[0][i] != 2 && paiList[0][i] != 4){
        				return 0;
        			}else{
        				if(paiList[1][i] != 0&&paiList[1][i] != 3){
        					return 0;
        				}else {
        					if (paiList[0][i] == 4&&i==cardIndex) {//胡牌时候要刚刚是那张牌才行
        						result = 2;
        					}
        				}
        			}
        		}
        	}
        return result;
    }
    
    public int checkSevenDouble2(int[][] paiList){
        int result = 1;
        
        	for(int i=0;i<paiList[0].length&&i<34;i++){
        		if(paiList[0][i] != 0){
        			if(paiList[0][i] != 2 && paiList[0][i] != 4){
        				return 0;
        			}else{
        				if(paiList[1][i] != 0&&paiList[1][i] != 3){
        					return 0;
        				}else {
        					if (paiList[0][i] == 4) {//胡牌时候要刚刚是那张牌才行
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
    		RoomVO room = roomVO;
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
    			if(avatarVo.getAccount().getUuid() != avatar.getUuId()&&avatarVo.getPaiArray()!=null){
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
    			sb.append("hu,");

    	}
    	if(penAvatar.contains(avatar)){
    		sb.append("peng,");
    		json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
    		json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    	}
    	if(chiAvatar.contains(avatar)){
    		sb.append("chi,");
    		json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
    		json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
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
    		}
    		else{
    			json.put("curAvatarIndex", curAvatarIndex);//当前出牌人的索引
        		json.put("putOffCardPoint", putOffCardPoint);//当前出的牌的点数
    			sb.append("gang"+gangCardIndex.toString()+",");
    		}
    	}
    	if(sb.length()>1){
			//该自己杠/胡/碰
			//游戏轮数
			int roundNum = RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).getCount();
    		json.put("gameRound", roundNum);//游戏轮数
    		//桌面剩余牌数
    		json.put("surplusCards", listCard.size() - nextCardindex);
    		avatar.getSession().sendMsg(new ReturnOnLineResponse(1, json.toString()));
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
		}
    	else{
    		if(!hasHu){
    		if(avatar.getUuId() == playerList.get(pickAvatarIndex).getUuId()){
    			//该自己出牌
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
    		avatar.getSession().sendMsg(new ReturnOnLineResponse(1, json.toString()));
    	}else{//如果重进，已经胡牌，则显示结算信息，别的玩家的准备信息
    		//游戏局数
    		int roundNum = RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).getCount();
    		avatar.avatarVO.setIsReady(true);//如果胡牌了再重新进入游戏，则重置准备状态
    		json.put("gameRound", roundNum);
    		//玩家的准备信息
    		json.put("hasHu", "true");
    		avatar.getSession().sendMsg(new ReturnOnLineResponse(1, json.toString()));
    	}
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
    }
    /**
     * 检测当，缓存数组里全部为空时，放弃操作，则不起作用
     */
    public boolean validateStatus(){
    	if(huAvatar.size() > 0 || penAvatar.size()>0 || gangAvatar.size()>0 || chiAvatar.size()>0 ){
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

	private boolean checkSelfTing(Avatar av) {
		boolean rv = false;
		int[][] paiList = av.getPaiArray();
		for(int i = 0; i < 34; ++i) {
			if (paiList[0][i] > 3) {
				continue;
			}
			paiList[0][i]++;
			rv = rv || checkSevenDouble2(paiList) > 0;
			rv = rv || checkThirteen(paiList);
			rv = rv || normalHuPai.checkHu(paiList);
			paiList[0][i]--;

			if (rv) {
				break;
			}
		}
		return rv;
	}

	private boolean checkOtherTing(Avatar av, Integer cardIndex, boolean bp) {
		boolean rv = false;
		int[][] paiList = av.getPaiArray();
		paiList[0][cardIndex]++;
		if (bp) {
			paiList[1][cardIndex] += 1;
		}

		for(int i = 0; i < 34; ++i) {
			if (paiList[0][i] == 0 || i == cardIndex) {
//			if (paiList[0][i] == 0 ) {//本来就听了,那当然更加是可以听的
				continue;
			}

			paiList[0][i]--;
			rv = rv || checkSelfTing(av);
			paiList[0][i]++;

			if (rv) {
				break;
			}
		}

		if (bp) {
			paiList[1][cardIndex] -= 1;
		}
		paiList[0][cardIndex]--;

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
		int dis = getDistToMain(av);
		for (int j = 0; j < playerList.size(); ++j) {
			playerList.get(j).getPaiArray()[0][curCard] = dis + 1; //设置花牌是哪家获得
		}
	}
	
    public int getPreBankerAvatar() {
		return preBankerAvatar;
	}
	public void setPreBankerAvatar(int preBankerAvatar) {
		this.preBankerAvatar = preBankerAvatar;
	}

}
