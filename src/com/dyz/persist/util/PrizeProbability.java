package com.dyz.persist.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.dyz.myBatis.model.Prize;
import com.dyz.myBatis.services.PrizeService;

/**
 * 奖品概率  
 * @author luck
 *
 */
public  abstract class PrizeProbability {

	public static List<Integer> prizeList = new ArrayList<Integer>();
	
	/**
	 * 初始化奖品概率
	 */
	public  static void initPrizesProbability(){
		List<Prize> prizes = PrizeService.getInstance().selectAllPrizes();
		for (Prize prize : prizes) {
			 for (int i = 0; i < prize.getProbability(); i++) {
				 prizeList.add(prize.getId());
			}
		}
		Collections.shuffle(prizeList);
	}
	/**
	 * 修改奖品概率之后更新
	 */
	public  synchronized  static void updatePrizesProbability(){
		prizeList.clear();
		initPrizesProbability();
	} 
}
