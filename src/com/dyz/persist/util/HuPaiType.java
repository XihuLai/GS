package com.dyz.persist.util;

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
	public static String getHuType(int uuid , Avatar avatar , int roomType){
		String str = null;
		 //区分转转麻将，划水麻将，长沙麻将
		 if(roomType == 1){
			 //转转麻将没有大小胡之分
			 str = zhuanZhuan(uuid , avatar , str);
		 }
		 else if(roomType == 2){
			 //划水麻将
			 str = huaShui(uuid , avatar , str);
		 }
		 else{
			 //长沙麻将
			 str = changSha(uuid,  avatar, str);
		 }
		
		return str;
	}
	/**
	 * 划水麻将
	 * @param uuid
	 * @param avatar
	 * @param str
	 * @return
	 */
	private static String huaShui(int uuid , Avatar avatar , String str){
		int [] paiList = avatar.getPaiArray();
		 if(uuid == avatar.getUuId() ){
			 //自摸类型
		 }
		 else{
		 }
		 return str;
	}
	/**
	 * 转转麻将
	 * @param uuid
	 * @param avatar
	 * @param str
	 * @return
	 */
	private static String zhuanZhuan(int uuid , Avatar avatar , String str){
		int [] paiList = avatar.getPaiArray();
		 if(uuid == avatar.getUuId() ){
			 //自摸
			 str ="0:"+Rule.Hu_zi_common;  
		 }
		 else{
			 str =uuid+":"+Rule.Hu_zi_common;  
		 }
		 return str;
	}
	/**
	 * 长沙麻将
	 * @param uuid
	 * @param avatar
	 * @param str
	 * @return
	 */
	private static String changSha(int uuid , Avatar avatar , String str){
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
		 
	 
		 return str;
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
