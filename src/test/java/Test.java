package test.java;

import com.dyz.gameserver.pojo.GangBackVO;
import com.dyz.persist.util.JsonUtilTool;

import java.util.ArrayList;
import java.util.List;

public class Test {

	public static void main(String[] args) {
		GangBackVO g = new GangBackVO();
		List<Integer> cardVOs = new ArrayList<>();
		cardVOs.add(4);
		cardVOs.add(21);
		g.setCardList(cardVOs);
		System.out.println(JsonUtilTool.toJson(g));
	}
}
