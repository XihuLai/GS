package com.dyz.gameserver.logic;

import com.context.Rule;
import com.dyz.gameserver.Avatar;
import com.dyz.gameserver.manager.RoomManager;
import com.dyz.gameserver.msg.response.chupai.ChuPaiResponse;
import com.dyz.gameserver.msg.response.gang.GangResponse;
import com.dyz.gameserver.msg.response.gang.OtherGangResponse;
import com.dyz.gameserver.msg.response.pickcard.OtherPickCardResponse;
import com.dyz.gameserver.msg.response.pickcard.PickCardResponse;
import com.dyz.gameserver.pojo.AvatarVO;
import com.dyz.persist.util.HuPaiType;
import com.dyz.persist.util.Naizi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    private int roomType;
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

    private  Avatar ava ;//最近的摸牌人或出牌人
    
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

    public Avatar getAva() {
    	return ava;
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
    public void initCard(int roomType,boolean isHong) {
        this.roomType = roomType;
        if(roomType == 1){
        	//转转麻将
            paiCount = 27;
            if(isHong){
                paiCount = 28;
            }
        }else if(roomType == 2){
            paiCount = 34;

        }else if(roomType == 3){
            paiCount = 27;
        }

        listCard = new ArrayList<Integer>();
        for (int i = 0; i < paiCount; i++) {
            for (int k = 0; k < 4; k++) {
                listCard.add(i);
            }
        }
        //洗牌
        shuffleTheCards();
       //TODO kevinTest
       /* RoomVO roomVO = new RoomVO();
        roomVO.setRoomId(12311);
        roomVO.setRoomType(1);
        playerList = new ArrayList<Avatar>();
        for(int ad = 0;ad<4;ad++){
            AvatarVO avatarVO = new AvatarVO();
            avatarVO.setAccount(new Account());
            Avatar avatar = new Avatar();
            avatar.avatarVO = avatarVO;
            avatar.roomVO = roomVO;
            if(ad == 2){
                avatarVO.setMain(true);
            }
            playerList.add(avatar);
        }

        for (int kk = 0; kk < playerList.size(); kk++) {
            playerList.get(kk).CreatePaiArray();
        }*/

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
        int tempPoint = getNextCardPoint();
        if(tempPoint != -1) {
            playerList.get(nextIndex).putCardInList(tempPoint);
            playerList.get(nextIndex).getSession().sendMsg(new PickCardResponse(1, tempPoint));
            for(int i=0;i<playerList.size();i++)
            {
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
        int nextIndex = curAvatarIndex;
        if(nextIndex < 3){
            nextIndex++;
        }else{
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
                huAvatar.remove(avatar);
            }else if(passType == 2){
                gangAvatar.remove(avatar);
            }else if(passType == 3){
                penAvatar.remove(avatar);
            }else if(passType == 4) {
                chiAvatar.remove(avatar);
            }

            if(huAvatar.size() == 0) {
                if (gangAvatar.size() > 0) {
                    for(Avatar item : gangAvatar){
                        if (item.gangQuest) {
                            //进行这个玩家的杠操作，并且把后面的碰，吃数组置为0;
                            pengCard(item,putOffCardPoint);
                            clearArrayAndSetQuest();
                            return;
                        }
                    }
                }
                if (penAvatar.size() > 0) {
                    for(Avatar item : penAvatar){
                        if (item.pengQuest) {
                            //进行这个玩家的碰操作，并且把后面的吃数组置为0;
                            pengCard(item,putOffCardPoint);
                            clearArrayAndSetQuest();
                            return;
                        }
                    }
                }
                if (chiAvatar.size() > 0) {
                    for(Avatar item : chiAvatar){
                        if (item.chiQuest) {
                            //进行这个玩家的吃操作
                            chiCard(item,putOffCardPoint);
                            clearArrayAndSetQuest();
                            return;
                        }
                    }
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
     * 摸牌
     * @param avatar
     *
     */
    public int pickCard(Avatar avatar){
//        int result = -1;
//        //****这里需不需要判断起手胡数组为不为空
//        if(huAvatar.size() == 0 && penAvatar.size() == 0 && gangAvatar.size() == 0 && chiAvatar.size() == 0 && qishouHuAvatar.size() == 0) {
//            ava = avatar;
//        	if (cardindex < listCard.size()-1) {
//                cardindex++;
//               if(avatar.putCardInList(listCard.get(cardindex))){
//            	   //摸牌加入手牌之后检测是否可以胡牌
//            	   result = listCard.get(cardindex);
//            	   if(checkHu(avatar)){
//            		   //胡牌消息放到数组中
//            		   huAvatar.add(avatar);
//            	   }
//            	   if(avatar.checkGang()){
//            		   //可以杠
//            		   gangAvatar.add(avatar);
//            	   }
//               }
//            }else if(cardindex ==listCard.size()-1 ){
//                //海底撈情况
//                cardindex++;
//                if(roomType == 3){
//                	//长沙麻将有捞与不捞的关系
//                	result = 100;
//                }
//                else{
//                	if(avatar.putCardInList(listCard.get(cardindex))){
//                		result = listCard.get(cardindex);
//                		//摸牌加入手牌之后检测是否可以胡牌
//                		if(checkHu(avatar)){
//                			//胡牌消息放到数组中
//                			huAvatar.add(avatar);
//                		}
//                		if(avatar.checkGang()){
//                			//可以杠
//                			gangAvatar.add(avatar);
//                		}
//                	}
//                }
//            }
//        }
//        return result;result
        return 1;
    }

    /**
     * 出牌
     * @param avatar
     * @param cardIndex
     */
    public void putOffCard(Avatar avatar,int cardIndex){
        putOffCardPoint = cardIndex;
        avatar.pullCardFormList(cardIndex);
        curAvatarIndex = playerList.indexOf(avatar);
        int nextIndex = curAvatarIndex;
        if(nextIndex < 3){
            nextIndex++;
        }else{
            nextIndex = 0;
        }
        for(int i=0;i<playerList.size();i++){
        	//判断那些玩家有吃，碰，杠,胡的情况
            if(playerList.get(i).getUuId() != avatar.getUuId()) {
                if (playerList.get(i).checkPeng(cardIndex)) {
                    penAvatar.add(playerList.get(i));
                }
                if (playerList.get(i).checkGang(cardIndex)) {
                    gangAvatar.add(playerList.get(i));
                }
                if (nextIndex == i && playerList.get(i).checkChi(cardIndex) &&  roomType == 3){
                	//(长沙麻将)只有下一家才能吃
                    chiAvatar.add(playerList.get(i));
                }
                if(checkAvatarIsHuPai(playerList.get(i),cardIndex)){
                    huAvatar.add(playerList.get(i));
                }
            }
        }
        ava  = avatar;//把每次最后出牌的玩家信息放入缓存中

        chuPaiCallBack(cardIndex);
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
    	 if(huAvatar.size() == 0 && penAvatar.size() == 0 && gangAvatar.size() == 0 && chiAvatar.size() !=0  && qishouHuAvatar.size() == 0) {
    		 if(chiAvatar.contains(avatar)){
    			 flag = true;
    			 //更新牌组
                 avatar.putCardInList(cardIndex);

    		 }
    	 }else{
             if(chiAvatar.size() > 0){
                 avatar.chiQuest = true;
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
    	 if(huAvatar.size() == 0 && penAvatar.size() > 0 && qishouHuAvatar.size() == 0 && gangAvatar.size() == 0) {
    		 if(penAvatar.contains(avatar)){
    			 //更新牌组
    			 flag = avatar.putCardInList(cardIndex);
    			 //把各个玩家碰的牌记录到缓存中去****
    			 avatar.putResultRelation(1,cardIndex+"");
    			 clearArrayAndSetQuest();
    		 }
    	 }else{
             if(penAvatar.size() > 0) {
                 avatar.pengQuest = true;
             }
         }
		return flag;
    }
    /**
     *杠牌
     * @param avatar
     * @return
     */
    public boolean gangCard(Avatar avatar , int cardPoint){
    	boolean flag = false;
    	 if(huAvatar.size() == 0 && gangAvatar.size() > 0 && qishouHuAvatar.size() == 0 && penAvatar.size() == 0 ) {
    		 if(gangAvatar.contains(avatar)){
    			 flag = true;
    			 //更新牌组
                 avatar.putCardInList(cardPoint);
    			 //判断杠的类型，自杠，还是点杠
    			 String str;
    			 if(avatar.getUuId() == ava.getUuId()){
    				 //自杠(明杠或暗杠)
    				 String strs = avatar.getResultRelation().get(1);
    				 if(strs != null && strs.contains(cardPoint+"")){
    					 //明杠
    					 str = "0:"+Rule.Gang_ming; 
    				 }
    				 else{
    					 //暗杠
    					 str = "0:"+Rule.Gang_an;
    				 }
    			 }
    			 else{
    				 //点杠
    				 str = ava.getUuId()+":"+Rule.Gang_dian;
    			 }
    			 //****两个人之间建立关联，游戏结束算账用
                 avatar.putResultRelation(2,str);
    			 clearArrayAndSetQuest();
                 //杠了以后要摸一张牌
                 if(true) {
                     //可以换牌的情况只补一张牌
                     int tempPoint = getNextCardPoint();
                     if (tempPoint != -1) {
                         avatar.putCardInList(tempPoint);
                         avatar.getSession().sendMsg(new GangResponse(1, tempPoint,0));
                     }
                 }else{
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
                            playerList.get(i).getSession().sendMsg(new OtherGangResponse(1,cardPoint,avatar.getUuId()));
                     }
                 }
    		 }
    	 }else{
             if(gangAvatar.size() > 0) {
                 avatar.gangQuest = true;
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
    	 if(huAvatar.size() > 0 && qishouHuAvatar.size() == 0 ) {
    		 if(huAvatar.contains(avatar)){
    			 flag = true;
    			 if(checkAvatarIsHuPai(avatar,cardIndex)){
    				 //胡牌数组中移除掉胡了的人
    				 huAvatar.remove(avatar);
    				 huCount++;
    				//****两个人之间建立关联，游戏结束算账用 
    				 String str = HuPaiType.getHuType(ava.getUuId(), avatar,roomType );
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
        		 //重新分配庄家，下一局点家坐庄
    			 for (Avatar itemAva : playerList) {
    				 if(ava.getUuId() == itemAva.getUuId() ){
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
    		 RoomManager.getInstance().getRoom(playerList.get(0).roomVO.getRoomId()).setPlayerList(playerList);
    		 //返回这一句的所有数据 杠，胡等，胡牌返回什么的数据
    		 
    	 }
    	 
		return flag;
    }
    /**
     * 出牌返回出牌点数和下一家玩家信息
     * @param cardIndex
     *
     */
    private void chuPaiCallBack(int cardIndex){
    	//把出牌点数和下面该谁出牌发送会前端  下一家都还没有摸牌就要出牌了??

    	for(int i=0;i<playerList.size();i++){
            //不能返回给自己
            if(i != curAvatarIndex) {
                playerList.get(i).getSession().sendMsg(new ChuPaiResponse(1, cardIndex, curAvatarIndex));
            }
    	}
        if(checkMsgAndSend()){
            pickCard();
        }
    }

    /**
     * 發送吃，碰，杠，胡牌信息
     * @return
     */
    private boolean checkMsgAndSend(){
        if(huAvatar.size() > 0){
            for(int i = 0;i<huAvatar.size();i++){
               // huAvatar.get(i).getSession().sendMsg();
            }
            return false;
        }
        if(gangAvatar.size() >0){
            for(int i = 0;i<gangAvatar.size();i++){
               // gangAvatar.get(i).getSession().sendMsg();
            }
            return false;
        }
        if(penAvatar.size()>0){
            for(int i = 0;i<penAvatar.size();i++){
               // penAvatar.get(i).getSession().sendMsg();
            }
            return false;
        }
        if(chiAvatar.size()>0){
            for(int i=0;i<chiAvatar.size();i++){

            }
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
        
        // TODO kevinTest
//        for (int m = 0; m < playerList.size(); m++) {
//            playerList.get(m).printPaiArray();
//            //发完牌后，把牌的数据信息发送给前台
//           // playerList.get(m).getSession().sendMsg(new StartGameResponse(1,getAvatarVoList()));
//        }
       //检测一下庄家有没有天胡
       if(checkHu(bankerAvatar)){
    	   //检查有没有天胡/有则把相关联的信息放入缓存中
    	   huAvatar.add(bankerAvatar);
       }
        if(roomType == 3) {
            //判读有没有起手胡
            checkQiShouFu();
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
				//胡牌信息放入缓存中
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
        int [] paiList =  avatar.getPaiArray();
        //int isServer = checkSevenDouble(paiList);
        if(roomType == 1 && avatar.roomVO.getHong()){
        	//转转麻将，可以选择红中
            //红中当癞子
             return  Naizi.testHuiPai(paiList);
        }
        else{
        	 return isHuPai(paiList);
        }
        /*if(isServer == 0){
            System.out.println("没有七小对");
            if(isHuPai(paiList)){
                System.out.print("胡牌");
                //cleanPlayListCardData();
            }else{
                System.out.println("checkHu 没有胡牌");
            }
        }else{

            if(isServer == 1){
                System.out.println("七对");
            }else{
                System.out.println("龙七对");
            }
            //cleanPlayListCardData();
            result = true;
        }*/
    }

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
        playCardsLogic.initCard(1,false);
    }
}
