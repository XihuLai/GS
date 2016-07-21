package com.dyz.persist.util;

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
     */
	public static void getHuType(int uuid , Avatar avatar , int roomType ,int cardIndex,List<Avatar> playerList){
		 //区分转转麻将，划水麻将，长沙麻将
		 if(roomType == 1){
			 //转转麻将没有大小胡之分
			 zhuanZhuan(uuid , avatar , cardIndex,playerList);
		 }
		 else if(roomType == 2){
			 //划水麻将
			  huaShui(uuid , avatar, cardIndex);
		 }
		 else{
			 //长沙麻将
			 changSha(uuid,  avatar ,cardIndex);
		 }
	}
	/**
	 * 划水麻将
	 * @param uuid
	 * @param avatar
	 * @param str
	 * @return
	 */
	private static void huaShui(int uuid , Avatar avatar,  int cardIndex){
		String str;
		int [] paiList = avatar.getPaiArray();
		 if(uuid == avatar.getUuId() ){
			 //自摸类型---然后判断牌组是什么类型(清一色？七对？龙对？等等)
		 }
		 else{
			 
		 }
	}
	/**
	 * 转转麻将
	 * @param uuid
	 * @param avatar
	 * @param str
	 * @return
	 */
	private static void zhuanZhuan(int uuid , Avatar avatar , int cardIndex, List<Avatar> playerList){
		String str; 
		if(uuid == avatar.getUuId() ){
			 //自摸
			 str ="0:"+cardIndex+":"+Rule.Hu_zi_common;  
			 for (Avatar ava :  playerList) {
				if(ava.getUuId() == avatar.getUuId()){
					// avatar.avatarVO.updateScoreRecord(1, 2*3);//记录分数
					//:游戏自摸1，接炮2，点炮3，暗杠4，明杠5 ，胡6记录(key),
					//修改自己的分数
					avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("1", 2*3);
				}
				else{
					//修改其他三家分数
					//ava.avatarVO.updateScoreRecord(1, -1*2);//记录分数（负分表示别人自摸扣的分）
					ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("1", -1*2);
				}
			}
		 }
		 else{
			 //点炮  
			 str =uuid+":"+cardIndex+":"+Rule.Hu_d_self;  
			 //修改胡家自己的分数
			 //avatar.avatarVO.updateScoreRecord(2, 1);//记录分数
			 avatar.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("2",1);
			 for (Avatar ava : playerList) {
				if(ava.getUuId() == uuid){
					//修改点炮玩家的分数
					//ava.avatarVO.updateScoreRecord(3, -1);//记录分数
					 ava.avatarVO.getHuReturnObjectVO().updateGangAndHuInfos("3",-1);
					//存储hu的关系信息 胡玩家uuid：胡牌id：胡牌类型
					 String string = avatar.getUuId()+":"+cardIndex+":"+Rule.Hu_d_other; 
					 //点炮信息放入放炮玩家信息中
					 ava.avatarVO.getHuReturnObjectVO().updateTotalInfo("hu", string);
				}
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
	private static void changSha(int uuid , Avatar avatar , int cardIndex){
		String str = null;
		int [] paiList = avatar.getPaiArray();
		 //长沙麻将
		 if(uuid == avatar.getUuId() ){
				//自摸类型
				if(checkQingyise(paiList)){
					//清一色
					str = "0:"+Rule.Hu_zi_qingyise;
				}
				if(avatar.getRoomVO().getSevenDouble() && checkQiDui(paiList)){
					if(str != null){
						//七小队对
						str = str +"-"+0+Rule.Hu_zi_qixiaodui;
					}
					else{
						str = Rule.Hu_zi_qixiaodui;
					}
				}
				if(str == null){
					str = "0:"+Rule.Hu_zi_common;
				}
			}
			else{
				//点炮类型
				//自摸类型
				if(checkQingyise(paiList)){
					//清一色
					str = uuid+":"+Rule.Hu_zi_qingyise;
				}
				if(avatar.getRoomVO().getSevenDouble() && checkQiDui(paiList)){
					if(str != null){
						//七小队对
						str = str +"-"+uuid+":"+Rule.Hu_zi_qixiaodui;
					}
					else{
						str = uuid+":"+Rule.Hu_zi_qixiaodui;
					}
				}
				if(str == null){
					str =uuid+":"+Rule.Hu_zi_common;
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
	
	
	
}
