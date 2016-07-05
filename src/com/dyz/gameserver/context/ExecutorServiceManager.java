package com.dyz.gameserver.context;

import com.dyz.gameserver.commons.tool.ServerThreadFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
/**
 * 整个服的线程池工具类
 *
 * @author  kevin
 * @date 2015年1月30日 下午5:37:16
 * @version V1.0
 */
public class ExecutorServiceManager {

	private static ExecutorServiceManager serviceManager = new ExecutorServiceManager();
	/**数据库操作使用的线程池*/
	private ScheduledThreadPoolExecutor executorServiceForDB;

	private ExecutorServiceManager(){}
	public static ExecutorServiceManager getInstance(){
		return serviceManager;
	}

	/**
	 * 初始化线程池
	 */
	public void initExecutorService(){
		executorServiceForDB = (ScheduledThreadPoolExecutor) Executors.newScheduledThreadPool(10,new ServerThreadFactory("ExecutorServiceForDB"));
	}

	/**
	 * 得到线程池对象
	 * @return
     */
	public ScheduledThreadPoolExecutor getExecutorServiceForDB(){
		return executorServiceForDB;
	}

	/**
	 * 关闭线程池
	 * @throws InterruptedException
     */
	public void stop() throws InterruptedException{
		executorServiceForDB.shutdown();
		executorServiceForDB.awaitTermination(1, TimeUnit.SECONDS);
	}
	
}
