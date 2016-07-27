package com.dyz.gameserver.logic;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.context.ErrorCode;
import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.commons.message.ResponseMsg;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.response.ErrorResponse;
import com.dyz.gameserver.msg.response.chupai.ChuPaiResponse;
import com.dyz.gameserver.msg.response.common.ReturnInfoResponse;
import com.dyz.gameserver.msg.response.gang.GangResponse;
import com.dyz.gameserver.msg.response.gang.OtherGangResponse;
import com.dyz.gameserver.msg.response.hu.HuPaiAllResponse;
import com.dyz.gameserver.msg.response.hu.HuPaiResponse;
import com.dyz.gameserver.msg.response.peng.PengResponse;
import com.dyz.gameserver.msg.response.pickcard.OtherPickCardResponse;
import com.dyz.gameserver.msg.response.pickcard.PickCardResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.CardVO;
import com.dyz.gameserver.pojo.HuReturnObjectVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.HuPaiType;
import com.dyz.persist.util.Naizi;


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
     * 当前摸牌人的索引
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
     * 下张牌的点数
     */
    private int cardindex = 0;
    /**
     * 上一家出的牌的点数
     */
    private int putOffCardPoint;
   /* *//**
     * 剩余牌的数量，转转麻将抓码的时候需要
     *//*
    private int surplusCardCount;*/
    /**
     * 4家玩家信息集合
     */
    private List<Avatar> playerList;
    /**
     * //   将牌标志，即牌型“三三三三二”中的“二”
     */
    private int JIANG = 0; 
    /**
     * 判断是否可以同时几个人胡牌
     */
    private int huCount=0;
    /**
     * 庄家
     */
    public Avatar bankerAvatar = null;
    /**
     * 房间信息
     */
    private RoomVO roomVO;
    /**
     * String有胡家uuid:码牌1:码牌2  组成
     */
    private String allMas;
    /**
     * 和前段握手，判断是否丢包的情况，丢包则继续发送信息
     *Integer为用户uuid
     */
    //private List<Integer> shakeHandsInfo = new  ArrayList<Integer>();
    private Map<Integer , ResponseMsg>  shakeHandsInfo= new  HashMap<Integer,ResponseMsg>();
    
    
	public Map<Integer , ResponseMsg> getShakeHandsInfo() {
		return shakeHandsInfo;
	}
	public void updateShakeHandsInfo(Integer uuid ,  ResponseMsg msg) {
		shakeHandsInfo.put(uuid, msg);
	}
	
	public String getAllMas() {
		return allMas;
	}
	public List<Avatar> getPlayerList() {
        return playerList;
    }

	public void setPlayerList(List<Avatar> playerList) {
        this.playerList = playerList;
    }
    /**
     * 初始化牌
     */
    public void initCard(RoomVO value) {
        roomVO = value;
        if(roomVO.getRoomType() == 1){
        	//转转麻将
            paiCount = 27;
            if(roomVO.getHong()){
                paiCount = 28;
            }
        }else if(roomVO.getRoomType() == 2){
        	//划水麻将
            if(roomVO.isAddWordCard()) {
                paiCount = 34;
            }else{
                paiCount = 27;
            }
        }else if(roomVO.getRoomType() == 3){
        	//长沙麻将
            paiCount = 27;
        }
        listCard = new ArrayList<Integer>();
        for (int i = 0; i < paiCount; i++) {
            for (int k = 0; k < 4; k++) {
                listCard.add(i);
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
        System.out.print("listCard --> "+listCard.toString());
    }
    /**
     * 检测玩家是否胡牌了
     * @param avatar
     * @param cardIndex
     */
    public boolean checkAvatarIsHuPai(Avatar avatar,int cardIndex,String type){
    	if(cardIndex != 100){
    		//传入的参数牌索引为100时表示天胡/或是摸牌，不需要再在添加到牌组中
    		avatar.putCardInList(cardIndex,4);
    	}
        if(checkHu(avatar)){
            System.out.println("确实胡牌了");
            if(type.equals("chu")){
            	System.out.println("检测后数量---------"+cardIndex+"--------------"+avatar.getPaiArray()[cardIndex]);
            	avatar.pullCardFormList(cardIndex);
            	System.out.println("检测后数量---------"+cardIndex+"--------------"+avatar.getPaiArray()[cardIndex]);
            }
            return true;
        }else{
            System.out.println("没有胡牌");
            if(type.equals("chu")){
            	System.out.println("检测后数量---------"+cardIndex+"--------------"+avatar.getPaiArray()[cardIndex]);
            	avatar.pullCardFormList(cardIndex);
            	System.out.println("检测后数量---------"+cardIndex+"--------------"+avatar.getPaiArray()[cardIndex]);
            }
            return false;
        }
    }
    /**
     * 摸牌
     *
     *
     */
    public void pickCard(){
    	
    	//判断握手信息是否为空，为空则发送，不为空则说明还有人回应我的消息，可能出现了丢包情况
    	/*while(shakeHandsInfo.size() > 0){
    		System.out.println("握手信息提醒：前段有消息未返回!");
    	}*/
    	
        //下一个人摸牌
        int nextIndex = getNextAvatarIndex();
        pickAvatarIndex = nextIndex;
        //本次摸得牌点数，下一张牌的点数，及本次摸的牌点数
        int tempPoint = getNextCardPoint();
    	System.out.println("摸牌!--"+tempPoint);
        if(tempPoint != -1) {
        	 Avatar avatar = playerList.get(nextIndex);
        	 avatar.getSession().sendMsg(new PickCardResponse(1, tempPoint));
            System.out.println("摸牌玩家------index"+nextIndex+"名字"+avatar.avatarVO.getAccount().getNickname());
            //记录摸牌信息
            
            
            //摸牌之后就重置可否胡别人牌的标签
            avatar.canHu = true;
            for(int i=0;i<playerList.size();i++){
                if(i != nextIndex){
                    playerList.get(i).getSession().sendMsg(new OtherPickCardResponse(1,nextIndex));
                }
            }
            
            //判断自己摸上来的牌自己是否可以胡
            StringBuffer sb = new StringBuffer();
            //摸起来也要判断是否可以杠，胡
            avatar.putCardInList(tempPoint,0);
            if (avatar.checkSelfGang(tempPoint)) {
            	gangAvatar.add(avatar);
            	sb.append("gang,");
            }
            if(checkAvatarIsHuPai(avatar,100,"mo")){
            	//检测完之后不需要移除
            	huAvatar.add(avatar);
            	sb.append("hu,");
            }
            if(sb.length()>2){
            	avatar.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
            }
            
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
     * 玩家选择放弃操作
     * @param avatar
     * @param passType
     * 1-胡，2-杠，3-碰，4-吃
     */
    public void gaveUpAction(Avatar avatar){
    
    	if(pickAvatarIndex == playerList.indexOf(avatar)){
    		//如果是自己摸的不胡，则 canHu = true；
    		avatar.canHu = true;
    		if(huAvatar.contains(avatar)){
    			huAvatar.remove(avatar);
    		}
    		if(gangAvatar.contains(avatar)){
    			gangAvatar.remove(avatar);
    		}
    		if(penAvatar.contains(avatar)){
    			penAvatar.remove(avatar);
    		}
    		if(chiAvatar.contains(avatar)){
    			chiAvatar.remove(avatar);
    		}
    	}
    	else{
    		//如果别人打的牌不胡，则 huQuest = false
    		avatar.canHu = false;
    		//放弃胡，则检测有没人杠
    		if(huAvatar.contains(avatar)){
    			huAvatar.remove(avatar);
    		}
    		if(gangAvatar.contains(avatar)){
    			gangAvatar.remove(avatar);
    		}
    		if(penAvatar.contains(avatar)){
    			penAvatar.remove(avatar);
    		}
    		if(chiAvatar.contains(avatar)){
    			chiAvatar.remove(avatar);
    		}
    		if(huAvatar.size() == 0) {
    			for(Avatar item : gangAvatar){
    				if (item.gangQuest) {
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
    		}
    		//如果都没有人胡，没有人杠，没有人碰，没有人吃的情况下。则下一玩家摸牌
    		if(huAvatar.size() == 0 && gangAvatar.size() == 0 && penAvatar.size() == 0 && chiAvatar.size() == 0){
    			pickCard();
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
        putOffCardPoint = cardPoint;
        System.out.println("出牌点数"+putOffCardPoint);

        curAvatarIndex = playerList.indexOf(avatar);
        playerList.get(curAvatarIndex).pullCardFormList(putOffCardPoint);
        int nextIndex = getNextAvatarIndex();
        for(int i=0;i<playerList.size();i++){
            //不能返回给自己
            if(i != curAvatarIndex) {
                playerList.get(i).getSession().sendMsg(new ChuPaiResponse(1, putOffCardPoint, curAvatarIndex));
                System.out.println("发送打牌消息----"+playerList.get(i).avatarVO.getAccount().getNickname());
            }
    	}
        Avatar ava;
        if(avatar.getRoomVO().getZiMo() != 1){
        	//出牌时，房间为可抢杠胡时才检测其他玩家有没胡的情况
        	for(int i=0;i<playerList.size();i++){
        		ava = playerList.get(i);
        		if(ava.getUuId() != avatar.getUuId()) {
        			StringBuffer sb = new StringBuffer();
        			//判断吃，碰， 胡 杠的时候需要把以前吃，碰，杠胡的牌踢出再计算
        			System.out.println("检测前牌数量-------"+putOffCardPoint+"------"+ava.getPaiArray()[putOffCardPoint]);
        			System.out.println("当前玩家状态---"+ava.canHu);
        			System.out.println(ava.getPaiArray());
        			if(ava.canHu  && checkAvatarIsHuPai(ava,putOffCardPoint,"chu")){
        				//胡牌状态为可胡的状态时才行
        				huAvatar.add(ava);
        				sb.append("hu,");
        			}
        			if (ava.checkGang(putOffCardPoint)) {
        				gangAvatar.add(ava);
        				sb.append("gang,");
        			}
        			if (ava.checkPeng(putOffCardPoint)) {
        				penAvatar.add(ava);
        				sb.append("peng,");
        			}
        			if ( roomVO.getRoomType() == 3 && nextIndex == i && ava.checkChi(putOffCardPoint)){
        				//(长沙麻将)只有下一家才能吃
        				chiAvatar.add(ava);
        				sb.append("chi");
        			}
        			if(sb.length()>2){
        				System.out.println(sb);
        				ava.getSession().sendMsg(new ReturnInfoResponse(1, sb.toString()));
        			}
        		}
        	}
        }
        //如果没有吃，碰，杠，胡的情况，则下家自动摸牌
        chuPaiCallBack();
    }
    
    /**
     * 吃牌
     * @param avatar
     * @param cardIndex  牌
     * @return
     */
    public boolean chiCard(Avatar avatar , CardVO cardVo){
    	//碰，杠都比吃优先
    	boolean flag = false;
    	int avatarIndex = playerList.indexOf(avatar);
    	if(roomVO.getRoomType() == 3){
    		if(huAvatar.size() == 0 && penAvatar.size() == 0 && gangAvatar.size() == 0 && chiAvatar.size() > 0) {
    			if(chiAvatar.contains(avatar)){
    				//更新牌组
    				avatar.putCardInList(cardVo.getCardPoint(),4);
    				clearArrayAndSetQuest();
    				flag = true;
    				for (int i = 0; i < playerList.size(); i++) {
    					if(avatar.getUuId() == playerList.get(i).getUuId()){
    						//*****吃牌后面弄，需要修改传入的参数 CardVO
//    						String str = "";.getClass()
//    						playerList.get(i).avatarVO.getHuReturnObjectVO().updateTotalInfo("chi", str);
    						//标记吃了的牌的下标(吃:1   碰2  杠3  胡4)
//    						playerList.get(i).avatarVO.getPaiArray()[1][cardVo.getCardPoint()] = 1 ;
//    						playerList.get(i).avatarVO.getPaiArray()[1][cardVo.getOnePoint()] = 1;
//    						playerList.get(i).avatarVO.getPaiArray()[1][cardVo.getOnePoint()] = 1;
    					}
					}
    				 curAvatarIndex = avatarIndex;
    				//更新用户的正常牌组(不算上碰，杠，胡，吃)吃牌这里还需要修改****
    				//playerList.get(avatarIndex).avatarVO.updateCurrentCardList(cardVo.getCardPoint());
    			}
    		}else{
    			if(chiAvatar.size() > 0){
    				for (Avatar ava : chiAvatar) {
    					ava.chiQuest = true;
    					ava.cardVO = cardVo;//存储前段发送过来的吃对象
    				}
    			}
    		}
    	}
    	else{
    		System.out.println("只有长沙麻将可以吃!");
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
    	int avatarIndex = playerList.indexOf(avatar);
    	 if(huAvatar.size() == 0 && penAvatar.size() > 0) {
    		 if(penAvatar.contains(avatar)){
    			 //更新牌组
    			 flag = avatar.putCardInList(cardIndex,1);
    			 //把各个玩家碰的牌记录到缓存中去,牌的index
    			 avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("peng", cardIndex+"");
    			 
    			 clearArrayAndSetQuest();
                 for (int i=0;i<playerList.size();i++){
                     playerList.get(i).getSession().sendMsg(new PengResponse(1,cardIndex,playerList.indexOf(avatar)));
                 }
                 //更新用户的正常牌组(不算上碰，杠，胡，吃)
                // playerList.get(avatarIndex).avatarVO.updateCurrentCardList(cardIndex,cardIndex,cardIndex);
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
    	if(huAvatar.size() == 0 && gangAvatar.size() > 0) {
    		 if(gangAvatar.contains(avatar)){
    			 //更新牌组
    			 flag = avatar.putCardInList(cardPoint,2);//杠牌标记2
    			 //判断杠的类型，自杠，还是点杠
    			 String str;
    			 int type;
    			 int score; //杠牌分数
    			 String recordType;//暗杠 4 ， 明杠 5(用于统计不同type下的次数和得分)
    			 String endStatisticstype;
    			 if(avatar.getUuId() == playerList.get(curAvatarIndex).getUuId()){
    				 //自杠(明杠或暗杠)，，这里的明杠时需要判断本房间是否是抢杠胡的情况，
    				 //如果是抢杠胡，则其他玩家有胡牌的情况下，可以胡
    				 String strs = avatar.getResultRelation().get(1);
    				 if(strs != null && strs.contains(cardPoint+"")){
    					 //明杠
    					 if((avatar.getRoomVO().getZiMo() != 1) 
    							 && checkQiangHu(avatar,cardPoint)){
    						 //这里的明杠时需要判断本房间是否是抢杠胡的情况，
            				 //如果是抢杠胡，则其他玩家有胡牌的情况下，可以胡
    						 
    						 return true;
    					 }
    					 else{
    						 str = "0:"+cardPoint+":"+Rule.Gang_ming; 
    						 type = 0;
    						 score = 1;
    						 recordType ="5";
    						 endStatisticstype = "minggang";
    					 }
    				 }
    				 else{
    					 //暗杠
    					 score = 2; 
    					 str = "0:"+cardPoint+":"+Rule.Gang_an;
    					 type = 1;
    					 recordType ="4";
    					 endStatisticstype = "angang";
    				 }
    				 for (Avatar ava : playerList) {
						if(ava.getUuId() == avatar.getUuId()){
							//修改玩家整个游戏总分和杠的总分
							 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType,score*2);
							 //整个房间统计每一局游戏 杠，胡的总次数
							 roomVO.updateEndStatistics(ava.getUuId(), endStatisticstype, 1);
						}
						else{
							//修改其他三家的分数
							ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType,-1*score);
						}
					}
    			 }
    			 else{
    				 //点杠(分在明杠里面)
    				 score = 3;
    				 recordType = "4";
    				 str = playerList.get(curAvatarIndex).getUuId()+":"+cardPoint+":"+Rule.Gang_dian;
    				 type = 0;
    				 endStatisticstype = "minggang";
    				 //减点杠玩家的分数
    				 playerList.get(curAvatarIndex).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, -1*score);
    				 //增加杠家的分数
    				 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos(recordType, score);
    				//整个房间统计每一局游戏 杠，胡的总次数
					 roomVO.updateEndStatistics(avatar.getUuId(), endStatisticstype, 1);
    			 }
    			 //存储杠牌的信息，
                 //avatar.putResultRelation(2,str);
    			 System.out.println(str);
    			 avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("gang", str);
    			 
    			 clearArrayAndSetQuest();
                 //杠了以后要摸一张牌
    			 int tempPoint = 100;
    			 int nextPoint = 100;
                 if(gangType == 0) {
                     //可以换牌的情况只补一张牌
                     tempPoint = getNextCardPoint();
                     if (tempPoint != -1) {
                         avatar.putCardInList(tempPoint,2);
                         playerList.get(avatarIndex).getSession().sendMsg(new GangResponse(1, tempPoint,100,type));
                     }
                 }else if(gangType == 1){
                     //摸两张
                     tempPoint = getNextCardPoint();
                     nextPoint = getNextCardPoint();
                     if (tempPoint != -1) {
                         avatar.putCardInList(tempPoint,2);
                         playerList.get(avatarIndex).getSession().sendMsg(new GangResponse(1, tempPoint, nextPoint,type));
                     }
                 }
                 for (int i=0;i<playerList.size();i++){
                     if(avatar.getUuId() != playerList.get(i).getUuId()){
                    	 //杠牌返回给其他人只返回杠的类型和杠牌的玩家位置
                            playerList.get(i).getSession().sendMsg(new OtherGangResponse(1,cardPoint,avatarIndex,type));
                     }
                 }
                 //更新用户的正常牌组(不算上碰，杠，胡，吃)
                // playerList.get(avatarIndex).avatarVO.updateCurrentCardList(cardPoint,cardPoint,cardPoint,cardPoint);
    		 }
    	 }else{
             if(gangAvatar.size() > 0) {
            	 for (Avatar ava : gangAvatar) {
            		 ava.gangQuest = true;
				}
             }
             try {
            	 playerList.get(avatarIndex).getSession().sendMsg(new ErrorResponse(ErrorCode.Error_000016));
			} catch (IOException e) {
				e.printStackTrace();
			}
         }
    	 
		return flag;
    }
    /**
     *胡牌
     * @param avatar
     * @return
     */
    public boolean huPai(Avatar avatar , int cardIndex){
    	boolean flag = false;
    	int huCount = huAvatar.size();
    	StringBuffer sb = new StringBuffer();
    	List<Integer> mas = new ArrayList<Integer>();
    	if(huCount ==1 && avatar.getRoomVO().getMa() >= 1){
    		sb.append(avatar.getUuId());
    		//单响    胡家抓码
    		for (int i = 0; i < avatar.getRoomVO().getMa(); i++) {
    			if(getNextCardPoint() != -1){
    				mas.add(getNextCardPoint());
    				sb.append(":"+getNextCardPoint());
    			}
    		}
    		allMas = sb.toString();
    	}
    	else if(huCount >= 1 && avatar.getRoomVO().getMa() >= 1){
    		//多响   点炮玩家抓码
    		sb.append(playerList.get(pickAvatarIndex).getUuId());
    		for (int i = 0; i < avatar.getRoomVO().getMa(); i++) {
    			if(getNextCardPoint() != -1){
    				mas.add(getNextCardPoint());
    				sb.append(":"+getNextCardPoint());
    			}
    		}
    		allMas = sb.toString();
    	}
    	 while(huAvatar.size() > 0) {
    		 if(huAvatar.contains(avatar)){
    			 if(playerList.get(pickAvatarIndex).getUuId() != avatar.getUuId()){
    				 //点炮    别人点炮的时候查看是否可以胡
    				 if(avatar.canHu){
    					System.out.println(avatar.getPaiArray()[cardIndex]);
        				 //胡牌数组中移除掉胡了的人
        				huAvatar.remove(avatar);
        				huCount++;
        				//两个人之间建立关联，游戏结束算账用 
        				HuPaiType.getHuType(playerList.get(curAvatarIndex), avatar,roomVO.getRoomType(),cardIndex ,playerList,mas,huCount);
        			    //整个房间统计每一局游戏 杠，胡的总次数
        				roomVO.updateEndStatistics(avatar.getUuId(), "jiepao", 1);
        				roomVO.updateEndStatistics(playerList.get(curAvatarIndex).getUuId(), "dianpao", 1);
        			    flag = true;
    				 }
    				 else{
    					System.out.println("放过一个人就要等自己摸牌之后才能胡其他人的牌"); 
    					 huAvatar.remove(avatar);
    				 }
    			 }
    			 else{
    				 //自摸
    				System.out.println(avatar.getPaiArray()[cardIndex]);
    				//胡牌数组中移除掉胡了的人
    				huAvatar.remove(avatar);
    				huCount++;
    				 //两个人之间建立关联，游戏结束算账用 
	    			 HuPaiType.getHuType(playerList.get(pickAvatarIndex), avatar,roomVO.getRoomType(),cardIndex,playerList,mas,huCount);
	    			 roomVO.updateEndStatistics(avatar.getUuId(), "zimo", 1);
	    			 flag = true;
    			 }
    		 }
    	 }
    	 if(huAvatar.size()==0){
    		 //所有人胡完
    		 if(huCount >= 2){
        		 //重新分配庄家，下一局点两家的玩家坐庄
    			 for (Avatar itemAva : playerList) {
    				 if(playerList.get(pickAvatarIndex).getUuId() == itemAva.getUuId() ){
                         itemAva.avatarVO.setMain(true);
    				 }
    				 else{
                         itemAva.avatarVO.setMain(false);
    				 }
    			}
        	 }
        	 else{
        		//重新分配庄家，下一局胡家坐庄
    			 for (Avatar itemAva : playerList) {
    				 if(avatar.getUuId() == itemAva.getUuId() ){
                         itemAva.avatarVO.setMain(true);
    				 }
    				 else{
                         itemAva.avatarVO.setMain(false);
    				 }
    			}
        	 }
    		 //更新roomlogic的PlayerList信息
    		 RoomManager.getInstance().getRoom(playerList.get(0).getRoomVO().getRoomId()).setPlayerList(playerList);
    		 //一局牌胡了，返回这一局的所有数据吃，碰， 杠，胡等信息
    		 settlementData();
    	 }
    	 
		return flag;
    }
    
    /**
     * 胡牌后返回结算数据信息
     */
    public void settlementData(){
    	JSONArray array = new JSONArray();
    	JSONObject json = new JSONObject();
    	for (Avatar avatar : playerList) {
    		HuReturnObjectVO   huReturnObjectVO = avatar.avatarVO.getHuReturnObjectVO();
    		huReturnObjectVO.setNickname(avatar.avatarVO.getAccount().getNickname());
    		huReturnObjectVO.setPaiArray(avatar.avatarVO.getPaiArray()[0]);
    		huReturnObjectVO.setUuid(avatar.getUuId());
    		array.add(huReturnObjectVO);
    		//在整个房间信息中修改总分数(房间次数用完之后的总分数)
    		roomVO.updateEndStatistics(avatar.getUuId(), "scores", huReturnObjectVO.getTotalScore());
		}
    	json.put("avatarList", array);
    	json.put("allMas", allMas);
    	int count = 10;
    	for (Avatar avatar : playerList) {
    		avatar.getSession().sendMsg(new HuPaiResponse(1,json.toString()));
    		count = RoomManager.getInstance().getRoom(avatar.getRoomVO().getRoomId()).getCount();
		}
    	//房间局数用完，返回本局胡牌信息的同时返回整个房间这几局的胡，杠等统计信息
		if(count == 0){
			
			Map<Integer, Map<String, Integer>> endStatistics = new HashMap<Integer,Map<String,Integer>>();
			for (Avatar avatar : playerList) {
				if(endStatistics == null){
					endStatistics = avatar.getRoomVO().getEndStatistics();
				}
			}
			int uuid;
			Map<String,Integer> map;
			Set<Entry<Integer, Map<String, Integer>>> set= endStatistics.entrySet();
			JSONObject js = new JSONObject();
			JSONArray arr = new JSONArray();
			while(set.iterator().hasNext()){
				map = new HashMap<String , Integer>();
				map = set.iterator().next().getValue();
				uuid = set.iterator().next().getKey();
				map.put("uuid", uuid);
				arr.add(map);
			}
			js.put("totalInfo", arr);
			for (Avatar avatar : playerList) {
				avatar.getSession().sendMsg(new HuPaiAllResponse(1,js.toString()));
			}
		}
		//判断该房间还有没有次数。有则清除玩家的准备状态，为下一局开始做准备
		else{
			//清楚当前房间牌的数据信息
			for (Avatar avatar : playerList) {
				avatar.avatarVO.setIsReady(false);
			}
			
		}
    }
    
    /**
     * 出牌返回出牌点数和下一家玩家信息
     * @param cardPoint
     *
     */
    private void chuPaiCallBack(){
    	//把出牌点数和下面该谁出牌发送会前端  下一家都还没有摸牌就要出牌了??

        if(checkMsgAndSend()){
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
        cardindex = 0;
        bankerAvatar = null;
        for (int i = 0; i < 13; i++) {
            for (int k = 0; k < playerList.size(); k++) {
                if (bankerAvatar == null) {
                    if (playerList.get(k).avatarVO.isMain()) {
                        bankerAvatar = playerList.get(k);
                    }
                }
                playerList.get(k).putCardInList(listCard.get(cardindex),0);
                cardindex++;
            }
        }
        bankerAvatar.putCardInList(listCard.get(cardindex),0);
        cardindex++;
        //检测一下庄家有没有天胡
       if(checkHu(bankerAvatar)){
    	   //检查有没有天胡/有则把相关联的信息放入缓存中
    	   huAvatar.add(bankerAvatar);
       }
       //初始化碰，胡杠，吃检测的时候需要的牌组
//       for (int i = 0; i < playerList.size(); i++) {
//    	   playerList.get(i).avatarVO.currentCardList = playerList.get(i).avatarVO.getPaiArray();
//       }
    }
    /**
     * 获取下一张牌的点数,如果返回为-1 ，则没有牌了
     * @return
     */
    public int getNextCardPoint(){
        cardindex++;
        if(cardindex<listCard.size()){
            return listCard.get(cardindex);
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
    /**
     * 检测胡牌算法，其中包含七小对，普通胡牌
     * @param avatar
     * @return
     */
    private boolean checkHu(Avatar avatar){
        JIANG = 0;
        //根据不同的游戏类型进行不用的判断
        if(roomVO.getRoomType() == 1){
        	return checkHuZZhuan(avatar);
        }
        else if(roomVO.getRoomType() == 2){
        	return checkHuHShui(avatar);
        }
        else{
        	return checkHuChangsha(avatar);
        }
        
        
        
       /* if(roomVO.getSevenDouble() && !roomVO.getHong()) {
        	//有癞子时，直接进行癞子的胡牌判断，不需要进行单独的判断
            int isSeven = checkSevenDouble(paiList);
            if(isSeven == 0){
                System.out.println("没有七小对");
                if(isHuPai(paiList)){
                  System.out.print("胡牌");
                //cleanPlayListCardData();
                }else{
                    System.out.println("checkHu 没有胡牌");
                }
            }else{

                if(isSeven == 1){
                    System.out.println("七对");
                }else{
                    System.out.println("龙七对");
                }
                //cleanPlayListCardData();
                return true;
            }
        }
        if(roomVO.getRoomType() == 1 && roomVO.getHong()){
        	//转转麻将，可以选择红中
            //红中当癞子
             return  Naizi.testHuiPai(paiList);
        }
        else{
        	 return isHuPai(paiList);
        }*/
    }

    /**
     * 判断转转麻将是否胡牌
     * @param avatar
     * @return
     * 1:是否自摸胡  /1- 自摸胡，2- 可以抢杠胡
		2:是否抢杠胡  /1- 自摸胡，2- 可以抢杠胡
		3:是否红中赖子
		4:是否抓码
       	5:是否可胡七小对  
     */
    public boolean checkHuZZhuan(Avatar avatar){
    	int [] paiList =  avatar.getPaiArray();
    	//不需要移除掉碰，杠了的牌组，在判断是否胡的时候就应判断了
    	//paiList  = cleanGangAndPeng(paiList,avatar);
    	boolean flag =  false;
    	//if(roomVO.getZiMo() == 2 || roomVO.getZiMo() == 0){
    		//可以抢杠胡（只有可抢杠胡的时候才判断其他人有没有胡牌）
    		if(roomVO.getSevenDouble() && !flag){
    			//可七小队
    			int isSeven = checkSevenDouble(paiList.clone());
                if(isSeven == 0){
                    System.out.println("没有七小对");
                }else{
                    if(isSeven == 1){
                        System.out.println("七对");
                    }else{
                        System.out.println("龙七对");
                    }
                    flag = true;
                }
    		}
    		if(!flag){
    			if(roomVO.getHong()){
    				//有癞子
    				flag =   Naizi.testHuiPai(paiList.clone());
    			}
    			else{
    				flag = isHuPai(paiList.clone());
    			}
    		}
    	//}
    	/*
    	
    	if(roomVO.getSevenDouble()){
    		if(roomVO.getHong()){
    			flag =   Naizi.testHuiPai(paiList);
    		}
    		else{
       		 	int isSeven = checkSevenDouble(paiList);
                if(isSeven == 0){
                    System.out.println("没有七小对");
                    if(isHuPai(paiList)){
                      System.out.print("胡牌");
                    //cleanPlayListCardData();
                      flag = true;
                      
                    }else{
                        System.out.println("checkHu 没有胡牌");
                        flag = false;
                    }
                    
                }else{
                    if(isSeven == 1){
                        System.out.println("七对");
                    }else{
                        System.out.println("龙七对");
                    }
                    //cleanPlayListCardData();
                    flag = true;
                }
    		}
    	}
    	else{
    		if(roomVO.getHong()){
    			flag =   Naizi.testHuiPai(paiList);
    		}
    		else{
    			flag = isHuPai(paiList);
    		}
    	}*/
		return flag;
    }
    /**
     * 判断划水麻将是否胡牌
     * @param avatar
     * @return
     */
    public boolean checkHuHShui(Avatar avatar){
    	int [] paiList =  avatar.getPaiArray();
    	boolean flag =  false;
    	if(roomVO.getZiMo() == 2 || roomVO.getZiMo() == 0){
    		//可以抢杠胡（只有可抢杠胡的时候才判断其他人有没有胡牌）
    		if(roomVO.getSevenDouble() && !flag){
    			//可七小队
    			int isSeven = checkSevenDouble(paiList.clone());
                if(isSeven == 0){
                    System.out.println("没有七小对");
                }else{
                    if(isSeven == 1){
                        System.out.println("七对");
                    }else{
                        System.out.println("龙七对");
                    }
                    flag = true;
                }
    		}
    		if(!flag){
    			if(roomVO.getHong()){
    				//有癞子
    				flag =   Naizi.testHuiPai(paiList.clone());
    			}
    			else{
    				flag = isHuPai(paiList.clone());
    			}
    		}
    	}
    	
    	
		return false;
    	
    }
    /**
     * 判断长沙麻将是否胡牌
     * @param avatar
     * @return
     */
    public boolean checkHuChangsha(Avatar avatar){
    	if(roomVO.getRoomType() == 3) {
            //判读有没有起手胡
            checkQiShouFu();
        }
		return false;
    	
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
					System.out.println("出现碰了的牌不在手牌中的错误情况!");
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
					System.out.println("出现碰了的牌不在手牌中的错误情况!");
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
     * 普通胡牌算法
     * @param paiList
     * @return
     */
    public boolean isHuPai(int[] paiList) {

        if (Remain(paiList) == 0) {
            return true;           //   递归退出条件：如果没有剩牌，则胡牌返回。
        }
        for (int i = 0;  i < paiList.length; i++) {//   找到有牌的地方，i就是当前牌,   PAI[i]是个数
            //   跟踪信息
            //   4张组合(杠子)
            if(paiList[i] != 0){
                if (paiList[i] == 4)                               //   如果当前牌数等于4张
                {
                    paiList[i] = 0;                                     //   除开全部4张牌
                    if (isHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，和牌
                    }
                    paiList[i] = 4;                                     //   否则，取消4张组合
                }
                //   3张组合(大对)
                if (paiList[i] >= 3)                               //   如果当前牌不少于3张
                {
                    paiList[i] -= 3;                                   //   减去3张牌
                    if (isHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    paiList[i] += 3;                                   //   取消3张组合
                }
                //   2张组合(将牌)
                if (JIANG ==0 && paiList[i] >= 2)           //   如果之前没有将牌，且当前牌不少于2张
                {
                    JIANG = 1;                                       //   设置将牌标志
                    paiList[i] -= 2;                                   //   减去2张牌
                    if (isHuPai(paiList)) return true;             //   如果剩余的牌组合成功，胡牌
                    paiList[i] += 2;                                   //   取消2张组合
                    JIANG = 0;                                       //   清除将牌标志
                }
                if   ( i> 27){
                    return   false;               //   “东南西北中发白”没有顺牌组合，不胡
                }
                //   顺牌组合，注意是从前往后组合！
                //   排除数值为8和9的牌
                if (i %9!=7 && i%9 != 8 && paiList[i+1]!=0 && paiList[i+2]!=0)             //   如果后面有连续两张牌
                {
                    paiList[i]--;
                    paiList[i + 1]--;
                    paiList[i + 2]--;                                     //   各牌数减1
                    if (isHuPai(paiList)) {
                        return true;             //   如果剩余的牌组合成功，胡牌
                    }
                    paiList[i]++;
                    paiList[i + 1]++;
                    paiList[i + 2]++;                                     //   恢复各牌数
                }
            }

        }
        //   无法全部组合，不胡！
        return false;
    }

    /**
     * 检查是否七小对胡牌
     * @param paiList
     * @return 0-没有胡牌。1-普通七小对，2-龙七对
     */
    public int checkSevenDouble(int[] paiList){
        int result = 1;
        for(int i=0;i<paiList.length;i++){
            if(paiList[i] != 0){
                if(paiList[i] != 2 && paiList[i] != 4){
                    return 0;
                }else{
                    if(paiList[i] == 4){
                        result = 2;
                    }
                }
            }
        }
        return result;
    }

    //   检查剩余牌数
    int Remain(int[] paiList) {
        int sum = 0;
        for (int i = 0; i < paiList.length; i++) {
            sum += paiList[i];
        }
        return sum;
    }
    /**
     * 前后端握手消息处理，前段接收到消息则会访问整个握手接口，说明接收到信息了
     * 然后后台从list里面移除这个用户对应的uuid，
     * 到最后list里面剩下的就表示前段还没有接收到消息，
     * 则重新发送消息
     * @param vaatar
     */
    public void shakeHandsMsg(Avatar  avatar){
    	shakeHandsInfo.remove(avatar.getUuId());
    	
    }
    /**
     * 在可以抢杠胡的情况下，判断其他人有没胡的情况
     * @return boolean
     */
    public boolean checkQiangHu(Avatar avatar ,int cardPoint){
    	boolean flag = false;
    	for (Avatar ava : playerList) {
			if(ava.getUuId() != avatar.getUuId()){
				//判断其他三家有没抢胡的情况
				ava.putCardInList(cardPoint, 0);
				if(checkHuZZhuan(ava)){
					flag = true;
					huAvatar.add(ava);
					//向玩家发送消息
					ava.getSession().sendMsg(new ReturnInfoResponse(1, "hu,"));
				}
				ava.pullCardFormList(cardPoint);
			}
		}
		return flag;
    }
    
    
    
    public static void main(String[] args) {
        PlayCardsLogic playCardsLogic = new PlayCardsLogic();
       // playCardsLogic.initCard(1,false);
    }
}
