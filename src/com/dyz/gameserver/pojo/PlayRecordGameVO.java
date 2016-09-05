package com.dyz.gameserver.pojo;

import java.util.ArrayList;
import java.util.List;

/**
 * 游戏回放相关
 * @author luck
 *
 */
public class PlayRecordGameVO {
	public List<PlayBehaviedVO> behavieList = new ArrayList<PlayBehaviedVO>();
	public List<PlayRecordItemVO> playerItems = new ArrayList<PlayRecordItemVO>();
	public RoomVO roomvo;
}
