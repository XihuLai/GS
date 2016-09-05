package com.dyz.myBatis.services;

import com.dyz.persist.util.TaskTimer;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import java.io.IOException;
import java.io.Reader;

/**
 * Created by kevin on 2016/6/17.
 */
public class InitServers {



    public void initServersFun() throws IOException {
        Reader reader = Resources.getResourceAsReader("myBatisConfig.xml");
        SqlSessionFactory sessionFactory = new SqlSessionFactoryBuilder().build(reader);

        //===============================================================
        AccountService.getInstance().initSetSession(sessionFactory);
        PrizeService.getInstance().initSetSession(sessionFactory);
        NoticeTableService.getInstance().initSetSession(sessionFactory);
        PrizeRuleService.getInstance().initSetSession(sessionFactory);
        ContactWayService.getInstance().initSetSession(sessionFactory);
        WinnersInfoService.getInstance().initSetSession(sessionFactory);
        TechargerecordService.getInstance().initSetSession(sessionFactory);
        StandingsService.getInstance().initSetSession(sessionFactory);
        StandingsDetailService.getInstance().initSetSession(sessionFactory);
        RoomInfoService.getInstance().initSetSession(sessionFactory);
        StandingsRelationService.getInstance().initSetSession(sessionFactory);
        StandingsAccountRelationService.getInstance().initSetSession(sessionFactory);
        PlayRecordService.getInstance().initSetSession(sessionFactory);

        
        TaskTimer.showTimer();
        TaskTimer.headBag();
    }

    private static InitServers initServers = new InitServers();

    public static InitServers getInstance(){
        return initServers;
    }
}
