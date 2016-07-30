package com.dyz.persist.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.context.Rule;
import com.dyz.gameserver.Avatar;

/**
 * 判断胡牌类型
 * @author luck
 *
 */
public class HuPaiType {

	
	/**
	 * 
	 * 	 //区分转转麻将，划水麻将，长沙麻将
	 * 
	 * 返回String的规格
     * 存储本局 杠，胡关系
     * list里面字符串规则 
     * 杠：uuid(出牌家),介绍(明杠，暗杠)  （123，明杠）
     * 自己摸来杠：介绍(明杠，暗杠)
     * 点炮：uuid(出牌家),介绍(胡的类型) （123，qishouhu）
     * 自摸：介绍(胡的类型)
     * Map：key-->1：表示信息    2:表示次数
     * count 为1表示单胡  2表示多响
     */
	public static void getHuType(Avatar avatarShu , Avatar avatar , int roomType ,int cardIndex,List<Avatar> playerList,List<Integer> mas,int count){
		 //区分转转麻将，划水麻将，长沙麻将
		 if(roomType == 1){
			 //转转麻将没有大小胡之分
			 zhuanZhuan(avatarShu , avatar , cardIndex,playerList,mas,count);
		 }
		 else if(roomType == 2){
			 //划水麻将
			  huaShui(avatarShu , avatar, cardIndex,playerList,count);
		 }
		 else{
			 //长沙麻将
			 changSha(avatarShu,  avatar ,cardIndex);
		 }
	}
	/**
	 * 划水麻将
	 * @param uuid
	 * @param avatar
	 * @param str
	 * huCount 是否是一炮多响
	 * @return
	 */
	private static void huaShui(Avatar avatarShu , Avatar avatar,  int cardIndex , List<Avatar> playerList , int huCount){
		String str;
		int [] paiList = avatar.getSinglePaiArray();
		 if(avatarShu.getUuId() == avatar.getUuId() ){
			 //自摸类型
			 /*str ="0:"+cardIndex+":"+Rule.Hu_zi_common;  
			 for (int i = 0; i < playerList.size(); i++) {
				 if(playerList.get(i).getUuId() == avatar.getUuId()){
					// avatar.avatarVO.updateScoreRecord(1, 2*3);//记录分数
					//:游戏自摸1，接炮2，点炮3，暗杠4，明杠5 ，胡6记录(key),
					//修改自己的分数
					 playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("1", 2*3);
					 for (int j = 0; j < selfCount; j++) {
						 //抓码 抓到自己，再加分
						 playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 2*3);
					 }
				}
				else{
					//修改其他三家分数
					//ava.avatarVO.updateScoreRecord(1, -1*2);//记录分数（负分表示别人自摸扣的分）
					playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("1", -1*2);
					for (int j = 0; j < selfCount; j++) {
						//胡家抓码抓到自己，所有这里还要再减分
						playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", -1*2);
					}
				}
			}*/
		 }
		 else{
			//点炮   单响  
			 if(huCount == 1){
				 str =avatarShu.getUuId()+":"+cardIndex+":"+Rule.Hu_d_self;  
				 //修改胡家自己的分数
				 
				 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("2",1);
				 
				 //修改点炮玩家的分数
				avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("3",-1);
				 //存储hu的关系信息 胡玩家uuid：胡牌id：胡牌类型
				 String string = avatar.getUuId()+":"+cardIndex+":"+Rule.Hu_d_other; 
				 //点炮信息放入放炮玩家信息中
				 avatarShu.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", string);
			 }
			 else{
				 //点炮  多响   (抓码人为点炮玩家)
				 //点炮玩家被抓到码的次数   selfCount
				//胡牌玩家被抓到码的次数
				 str =avatarShu.getUuId()+":"+cardIndex+":"+Rule.Hu_d_self;  
				 //修改胡家自己的分数
				 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("2",1);
				
				 
				 //修改点炮玩家的分数
				 avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("3",-1);
					
				 //存储hu的关系信息 胡玩家uuid：胡牌id：胡牌类型
				 String string = avatar.getUuId()+":"+cardIndex+":"+Rule.Hu_d_other; 
				 //点炮信息放入放炮玩家信息中
				 avatarShu.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", string);
					
			 }
		 }
	}
	/**
	 * 转转麻将
	 * @param uuid
	 * @param avatar
	 * @param str
	 * @return
	 */
	private static void zhuanZhuan(Avatar  avatarShu , Avatar avatar , int cardIndex, List<Avatar> playerList,List<Integer> mas , int count){
		String str; 
		int selfCount = 0;
		List<Integer> maPoint = new ArrayList<Integer>();;
		if(mas != null){
			for (Integer cardPoint : mas) {
				if(cardPoint >= 9){
					maPoint .add(cardPoint%4);
				}
			}
		}
			//抓的码里面有多少个指向对应的各个玩家
			selfCount  = Collections.frequency(maPoint, 0);//自己 
			int downCount  = Collections.frequency(maPoint, 1);//下家
			int towardCount  = Collections.frequency(maPoint, 2);//对家
			int upCount  = Collections.frequency(maPoint, 3);//上家
			
			
			int selfIndex = 0;//胡家在数组中的位置 （0-3）
			
			for (int i = 0; i < playerList.size(); i++) {
				if(playerList.get(i).getUuId() == avatar.getUuId()){
					selfIndex = i;
				}
			}
			//其他三家在playerList中的下标，同上面的selfCount，downCount对应
			int downIndex = otherIndex(selfIndex,1);
			int towardIndex = otherIndex(selfIndex,2);
			int upIndex = otherIndex(selfIndex,3);
		    if(avatarShu.getUuId() == avatar.getUuId() ){
		    	//自摸
				 str ="0:"+cardIndex+":"+Rule.Hu_zi_common;  
				 for (int i = 0; i < playerList.size(); i++) {
					 if(playerList.get(i).getUuId() == avatar.getUuId()){
						// avatar.avatarVO.updateScoreRecord(1, 2*3);//记录分数
						//:游戏自摸1，接炮2，点炮3，暗杠4，明杠5 ，胡6记录(key),
						//修改自己的分数
						 playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("1", 2*3);
						 for (int j = 0; j < selfCount; j++) {
							 //抓码 抓到自己，再加分
							 playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 2*3);
						 }
					}
					else{
						//修改其他三家分数
						//ava.avatarVO.updateScoreRecord(1, -1*2);//记录分数（负分表示别人自摸扣的分）
						playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("1", -1*2);
						for (int j = 0; j < selfCount; j++) {
							//胡家抓码抓到自己，所有这里还要再减分
							playerList.get(i).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", -1*2);
						}
					}
				}
			    //抓码加减分
				for (int j = 0; j < downCount; j++) {
					//抓码 抓到下家，胡家加分，下家减分
					playerList.get(selfIndex).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 2);
					playerList.get(downIndex).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", -1*2);
				}
				for (int j = 0; j < towardCount; j++) {
					//抓码 抓到对家，胡家加分，对家减分
					playerList.get(selfIndex).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 2);
					playerList.get(towardIndex).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", -1*2);
				}
				for (int j = 0; j < upCount; j++) {
					//抓码 抓到上家，胡家加分，上家减分
					playerList.get(selfIndex).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 2);
					playerList.get(upIndex).avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7",-1*2);
				}
		 }
		 else{
			 //点炮   单响  
			 if(count == 1){
				 int dPaoIndex = 0;//点炮人在数组中的下标
				 for (Avatar ava : playerList) {
					 if(ava.getUuId() == avatarShu.getUuId()){
						 //得到点炮玩家的下标
						 dPaoIndex = playerList.indexOf(ava);
					 }
				 }
				 //点炮玩家被抓到码的次数
				 int dPaoCount = 0;
				 if(dPaoIndex == downIndex){
					 dPaoCount = downCount;
				 }
				 else if(dPaoIndex == towardIndex){
					 dPaoCount = towardCount;
				 }
				 else if(dPaoIndex == upIndex){
					 dPaoCount = upCount;
				 }
				 
				 str =avatarShu.getUuId()+":"+cardIndex+":"+Rule.Hu_d_self;  
				 //修改胡家自己的分数
				 
				 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("2",1);
				 for (int j = 0; j < selfCount; j++) {
					 //抓码 抓到自己，自己加分
					 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 1);
				 }
				 for (int j = 0; j < dPaoCount; j++) {
					 //抓码 抓到点炮玩家，自己加分
					 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 1);
				 }
				 
				 //修改点炮玩家的分数
				avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("3",-1);
				 for (int j = 0; j < selfCount; j++) {
					 //抓码 抓到胡家，自己再减分
					 avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", -1);
				 }
				 for (int j = 0; j < dPaoCount; j++) {
					 //抓码 抓到自己，自己再减分
					 avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", -1);
				 }
				 //存储hu的关系信息 胡玩家uuid：胡牌id：胡牌类型
				 String string = avatar.getUuId()+":"+cardIndex+":"+Rule.Hu_d_other; 
				 //点炮信息放入放炮玩家信息中
				 avatarShu.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", string);
			 }
			 else{
				 //点炮  多响   (抓码人为点炮玩家)
				 //点炮玩家被抓到码的次数   selfCount
				//胡牌玩家被抓到码的次数
				 int huCount = playerList.indexOf(avatar);
				 if(huCount == downIndex){
					 huCount = downCount;
				 }
				 else if(huCount == towardIndex){
					 huCount = towardCount;
				 }
				 else if(huCount == upIndex){
					 huCount = upCount;
				 }
				 
				 str =avatarShu.getUuId()+":"+cardIndex+":"+Rule.Hu_d_self;  
				 //修改胡家自己的分数
				 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("2",1);
				 for (int j = 0; j < selfCount; j++) {
					 //抓码 抓到自己，自己加分
					 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 1);
				 }
				 for (int j = 0; j < huCount; j++) {
					 //抓码 抓到点炮玩家，自己加分
					 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", 1);
				 }
				 
				 //修改点炮玩家的分数
				 avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("3",-1);
					for (int j = 0; j < selfCount; j++) {
					 //抓码 抓到胡家，自己再减分
					 avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", -1);
				 }
				 for (int j = 0; j < huCount; j++) {
					 //抓码 抓到自己，自己再减分
					 avatarShu.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("7", -1);
				 }
				 //存储hu的关系信息 胡玩家uuid：胡牌id：胡牌类型
				 String string = avatar.getUuId()+":"+cardIndex+":"+Rule.Hu_d_other; 
				 //点炮信息放入放炮玩家信息中
				 avatarShu.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", string);
					
			 }
		 }
		//存储hu的关系信息，胡牌信息放入胡牌玩家存储信息中
		 avatar.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", str);
	}
	/**
	 * 长沙麻将
	 * @param uuid
	 * @param avatar
	 * @param str
	 * @return
	 */
	private static void changSha(Avatar  avatarShu , Avatar avatar , int cardIndex){
		String str = null;
		int uuid  = avatarShu.getUuId();
		int [] paiList = avatar.getSinglePaiArray();
		 //长沙麻将
		 if(avatarShu.getUuId() == avatar.getUuId() ){
				//自摸类型
				if(checkQingyise(paiList)){
					//清一色
					str = "0:"+Rule.Hu_zi_qingyise;
				}
				if(avatar.getRoomVO().getSevenDouble() && checkQiDui(paiList)){
					if(str != null){
						//七小队对
						str = str +"-"+0+Rule.Hu_self_qixiaodui;
					}
					else{
						str = Rule.Hu_self_qixiaodui;
					}
				}
				if(str == null){
					//str = "0:"+Rule.Hu_zi_common;
				}
			}
			else{
				//点炮类型
				if(checkQingyise(paiList)){
					//清一色
					str = uuid+":"+Rule.Hu_d_qingyise;
				}
				if(avatar.getRoomVO().getSevenDouble() && checkQiDui(paiList)){
					if(str != null){
						//七小队对
						str = str +"-"+uuid+":"+Rule.Hu_other_qixiaodui;
					}
					else{
						str = uuid+":"+Rule.Hu_other_qixiaodui;
					}
				}
				if(str == null){
					//str =uuid+":"+Rule.Hu_zi_common;
				}
			}
		 
	}
	
	
	
	/**
	 * 判断是否是清一色
	 * @param paiList
	 * @return
	 */
	private static  boolean checkQingyise(int [] paiList){
		boolean str= false;
		//是否是清一色
		int qys = 0;
		for (int i = 0; i < paiList.length; i++) {
			if(i <= 8){
				if(paiList[i]>=1){
					qys = qys+1;
				}
			}
			else if( i >= 9 && i<= 17){
				if(paiList[i]>=1){
					qys = qys+1;
				}
			}
			else{
				if(paiList[i]>=1){
					qys = qys+1;
				}
			}
		}
		
		if(qys ==1){
			str = true;
		}
		return str;
	}
	/**
	 * 判断是否是七对
	 * @param paiList
	 * @return
	 */
	private static  boolean checkQiDui(int [] paiList){
		boolean str= false;
		int qys = 0;
		for (int i = 0; i < paiList.length; i++) {
			if(paiList[i] == 0 || paiList[i] == 2){
				qys++;
			}
		}
		if(qys ==7){
			str = true;
		}
		return str;
	}
	
	private static int otherIndex(int selfindex,int count){
		if((selfindex + count) >= 4){
			selfindex = selfindex + count -4;
		}
		else{
			selfindex = selfindex + count;
		}
		return selfindex;
	}
	
}
