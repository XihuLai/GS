package com.dyz.gameserver.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.response.chupai.ChuPaiResponse;
import com.dyz.gameserver.msg.response.gang.GangResponse;
import com.dyz.gameserver.msg.response.gang.OtherGangResponse;
import com.dyz.gameserver.msg.response.hu.HuPaiResponse;
import com.dyz.gameserver.msg.response.peng.PengResponse;
import com.dyz.gameserver.msg.response.pickcard.OtherPickCardResponse;
import com.dyz.gameserver.msg.response.pickcard.PickCardResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.gameserver.pojo.RoomVO;
import com.dyz.persist.util.HuPaiType;
import com.dyz.persist.util.Naizi;

import net.sf.json.JSONArray;

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
    
    private int cardindex = 0;
    //上一家出的牌的点数
    private int putOffCardPoint;
    
    private List<Avatar> playerList;
    private int JIANG = 0; //   将牌标志，即牌型“三三三三二”中的“二”

    /**
     * 判断是否可以同时几个人胡牌
     */
    private int huCount=0;
    /**
     * 庄家
     */
    public Avatar bankerAvatar = null;
    /**
     * 是否添加字牌
     */
    //private boolean addWordCard = false;
    /**
     * 当前圈数
     */
    private int currentCircle =0;

    
    
    public int getCurrentCircle() {
		return currentCircle;
	}
    /**
     * 获取下一圈数
     * @param currentCircle
     */
	public void addCircle(int currentCircle) {
		this.currentCircle = currentCircle +1;
	}
    public List<Avatar> getPlayerList() {
        return playerList;
    }


	public void setPlayerList(List<Avatar> playerList) {
        this.playerList = playerList;
    }

    private RoomVO roomVO;
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
    public boolean checkAvatarIsHuPai(Avatar avatar,int cardIndex){
        avatar.putCardInList(cardIndex);
        if(checkHu(avatar)){
            System.out.println("确实胡牌了");
            avatar.pullCardFormList(cardIndex);
            return true;
        }else{
            avatar.pullCardFormList(cardIndex);
            System.out.println("没有胡牌");
            return false;
        }
    }
    /**
     * 摸牌
     *
     *
     */
    public void pickCard(){
        //下一个人摸牌
    	
        int nextIndex = getNextAvatarIndex();
        int tempPoint = getNextCardPoint();//下一张牌的点数，及本次摸的牌点数
        if(tempPoint != -1) {
            playerList.get(nextIndex).putCardInList(tempPoint);
            playerList.get(nextIndex).getSession().sendMsg(new PickCardResponse(1, tempPoint));
            for(int i=0;i<playerList.size();i++){
                if(i != nextIndex){
                    playerList.get(i).getSession().sendMsg(new OtherPickCardResponse(1,nextIndex));
                }
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
    public void gaveUpAction(Avatar avatar,int passType){
        if(passType == 1){
        	//放弃胡，则检测有没人杠
        	huAvatar.remove(avatar);
            if(gangAvatar.contains(avatar)){
                gangAvatar.remove(avatar);
            }
            if(penAvatar.contains(avatar)){
                penAvatar.remove(avatar);
            }
            if(chiAvatar.contains(avatar)){
                chiAvatar.remove(avatar);
            }
        }else if(passType == 2){
        	//放弃杠。则检测有没人碰
            gangAvatar.remove(avatar);
            if(penAvatar.contains(avatar)){
                penAvatar.remove(avatar);
            }
            if(chiAvatar.contains(avatar)){
                chiAvatar.remove(avatar);
            }
        }else if(passType == 3){
        	//放弃碰，则检测有么人吃
            penAvatar.remove(avatar);
           if(chiAvatar.contains(avatar)){
                chiAvatar.remove(avatar);
            }
        }else if(passType == 4) {
        	//放弃吃
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
                    chiCard(item,putOffCardPoint);
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
        avatar.pullCardFormList(putOffCardPoint);
        curAvatarIndex = playerList.indexOf(avatar);
        int nextIndex = getNextAvatarIndex();
        for(int i=0;i<playerList.size();i++){
        	//判断那些玩家有吃，碰，杠,胡的情况
            if(playerList.get(i).getUuId() != avatar.getUuId()) {
                if (playerList.get(i).checkPeng(putOffCardPoint)) {
                    penAvatar.add(playerList.get(i));
                }
                if (playerList.get(i).checkGang(putOffCardPoint)) {
                    gangAvatar.add(playerList.get(i));
                }
                if (nextIndex == i && playerList.get(i).checkChi(putOffCardPoint) &&  roomVO.getRoomType() == 3){
                	//(长沙麻将)只有下一家才能吃
                    chiAvatar.add(playerList.get(i));
                }
                if(checkAvatarIsHuPai(playerList.get(i),putOffCardPoint)){
                    huAvatar.add(playerList.get(i));
                }
            }
        }
        //如果没有吃，碰，杠，胡的情况，则下家自动摸牌
        chuPaiCallBack(putOffCardPoint);
    }
    
    /**
     * 吃牌
     * @param avatar
     * @param cardIndex  牌
     * @return
     */
    public boolean chiCard(Avatar avatar , int cardIndex){
    	//碰，杠都比吃优先
    	boolean flag = false;
    	 if(huAvatar.size() == 0 && penAvatar.size() == 0 && gangAvatar.size() == 0 && chiAvatar.size() > 0) {
    		 if(chiAvatar.contains(avatar)){
    			 //更新牌组
                 avatar.putCardInList(cardIndex);
                 clearArrayAndSetQuest();
                 flag = true;
    		 }
    	 }else{
             if(chiAvatar.size() > 0){
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
    	 if(huAvatar.size() == 0 && penAvatar.size() > 0) {
    		 if(penAvatar.contains(avatar)){
    			 //更新牌组
    			 flag = avatar.putCardInList(cardIndex);
    			 //把各个玩家碰的牌记录到缓存中去,出牌人uuid+所出牌的index
    			 String  str = playerList.get(curAvatarIndex).getUuId()+":"+cardIndex;
    			 avatar.putResultRelation(1,str);
    			 clearArrayAndSetQuest();
                 for (int i=0;i<playerList.size();i++){
                     if(avatar.getUuId() != playerList.get(i).getUuId()){
                         playerList.get(i).getSession().sendMsg(new PengResponse(1,cardIndex,playerList.indexOf(avatar)));
                     }
                 }
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
    	 if(huAvatar.size() == 0 && gangAvatar.size() > 0) {
    		 if(gangAvatar.contains(avatar)){
    			 //更新牌组
    			 flag = avatar.putCardInList(cardPoint);
    			 //判断杠的类型，自杠，还是点杠
    			 String str;
    			 if(avatar.getUuId() == playerList.get(curAvatarIndex).getUuId()){
    				 //自杠(明杠或暗杠)
    				 String strs = avatar.getResultRelation().get(1);
    				 if(strs != null && strs.contains(cardPoint+"")){
    					 //明杠
    					 str = "0:"+cardPoint+":"+Rule.Gang_ming; 
    				 }
    				 else{
    					 //暗杠
    					 str = "0:"+cardPoint+":"+Rule.Gang_an;
    				 }
    			 }
    			 else{
    				 //点杠
    				 str = playerList.get(curAvatarIndex).getUuId()+":"+cardPoint+":"+Rule.Gang_dian;
    			 }
    			 //两个人之间建立关联，游戏结束算账用
                 avatar.putResultRelation(2,str);
    			 clearArrayAndSetQuest();
                 //杠了以后要摸一张牌
                 if(gangType == 0) {
                     //可以换牌的情况只补一张牌
                     int tempPoint = getNextCardPoint();
                     if (tempPoint != -1) {
                         avatar.putCardInList(tempPoint);
                         avatar.getSession().sendMsg(new GangResponse(1, tempPoint,0));
                     }
                 }else if(gangType == 1){
                     //摸两张
                     int tempPoint = getNextCardPoint();
                     int nextPoint = getNextCardPoint();
                     if (tempPoint != -1) {
                         avatar.putCardInList(tempPoint);
                         avatar.getSession().sendMsg(new GangResponse(1, tempPoint,nextPoint));
                     }
                 }

                 for (int i=0;i<playerList.size();i++){
                     if(avatar.getUuId() != playerList.get(i).getUuId()){
                            playerList.get(i).getSession().sendMsg(new OtherGangResponse(1,cardPoint,i));
                     }
                 }
    		 }
    	 }else{
             if(gangAvatar.size() > 0) {
            	 for (Avatar ava : gangAvatar) {
            		 ava.gangQuest = true;
				}
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
    	 while(huAvatar.size() > 0) {
    		 if(huAvatar.contains(avatar)){
    			 flag = true;
    			 if(checkAvatarIsHuPai(avatar,cardIndex)){
    				 //胡牌数组中移除掉胡了的人
    				 huAvatar.remove(avatar);
    				 huCount++;
    				//两个人之间建立关联，游戏结束算账用 
    				 String str = HuPaiType.getHuType(playerList.get(curAvatarIndex).getUuId(), avatar,roomVO.getRoomType(),cardIndex );
    				 avatar.putResultRelation(3,str);
    			 }
    			 else{
    				 System.out.println("胡不了牌");
    			 }
    		 }
    	 }
    	 if(huAvatar.size()==0){
    		 //所有人胡完
    		 if(huCount >= 2){
        		 //重新分配庄家，下一局点两家的玩家坐庄
    			 for (Avatar itemAva : playerList) {
    				 if(playerList.get(curAvatarIndex).getUuId() == itemAva.getUuId() ){
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
    		 //返回这一句的所有数据 杠，胡等，胡牌返回什么的数据
    		 settlementData();
    	 }
    	 
		return flag;
    }
    
    /**
     * 胡牌后返回结算数据信息
     */
    public void settlementData(){
    	//只需要传入缓存中杠2和胡3的信息
    	JSONArray array = new JSONArray();
    	for (Avatar avatar : playerList) {
    		array.add(avatar.getResultRelation().get(2));
    		array.add(avatar.getResultRelation().get(3));
		}
    	for (Avatar avatar : playerList) {
    		avatar.getSession().sendMsg(new HuPaiResponse(1,array.toString()));
		}
    	
    }
    
    /**
     * 出牌返回出牌点数和下一家玩家信息
     * @param cardPoint
     *
     */
    private void chuPaiCallBack(int cardPoint){
    	//把出牌点数和下面该谁出牌发送会前端  下一家都还没有摸牌就要出牌了??

    	for(int i=0;i<playerList.size();i++){
            //不能返回给自己
            if(i != curAvatarIndex) {
                playerList.get(i).getSession().sendMsg(new ChuPaiResponse(1, cardPoint, curAvatarIndex));
            }
    	}
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
                playerList.get(k).putCardInList(listCard.get(cardindex));
                cardindex++;
            }
        }
        bankerAvatar.putCardInList(listCard.get(cardindex));
        cardindex++;
        //检测一下庄家有没有天胡
       if(checkHu(bankerAvatar)){
    	   //检查有没有天胡/有则把相关联的信息放入缓存中
    	   huAvatar.add(bankerAvatar);
       }
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
        	return checkHuHuaS(avatar);
        }
        else if(roomVO.getRoomType() == 2){
        	return checkHuZhuanZ(avatar);
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
     * 判断划水麻将是否胡牌
     * @param avatar
     * @return
      3，是否红中赖子
      4，是否可胡七小对  
              不同情况不同判断
     */
    public boolean checkHuHuaS(Avatar avatar){
    	int [] paiList =  avatar.getPaiArray();
    	//移除掉碰，杠了的牌组
    	paiList  = cleanGangAndPeng(paiList,avatar);
    	boolean flag =  false;
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
    	}
		return flag;
    }
    /**
     * 判断转转麻将是否胡牌
     * @param avatar
     * @return
     */
    public boolean checkHuZhuanZ(Avatar avatar){
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
    
    public static void main(String[] args) {
        PlayCardsLogic playCardsLogic = new PlayCardsLogic();
       // playCardsLogic.initCard(1,false);
    }
}
