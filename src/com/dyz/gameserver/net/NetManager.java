package com.dyz.gameserver.net;

import com.dyz.gameserver.net.codec.GameProtocolcodecFactory;
import org.apache.mina.core.filterchain.DefaultIoFilterChainBuilder;
import org.apache.mina.core.filterchain.IoFilter;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IdleStatus;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.executor.ExecutorFilter;
import org.apache.mina.filter.executor.OrderedThreadPoolExecutor;
import org.apache.mina.transport.socket.SocketSessionConfig;
import org.apache.mina.transport.socket.nio.NioSocketAcceptor;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;

public class NetManager {

	private NioSocketAcceptor acceptor;
	private OrderedThreadPoolExecutor threadpool;

	/**
	 * 开始监听端口 前段
	 * @param iohandler
	 * @param listenPort
	 * @throws Exception
     */
	public  void startListner(IoHandler iohandler,int listenPort) throws Exception{
		acceptor = new NioSocketAcceptor();
		acceptor.setBacklog(50);
		acceptor.setReuseAddress(true);
		acceptor.setHandler(iohandler);

        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        IoFilter protocol = new ProtocolCodecFilter(new GameProtocolcodecFactory());
        chain.addLast("codec", protocol);
		//threadpool = new OrderedThreadPoolExecutor(1000);
		//threadpool.setThreadFactory(new ServerThreadFactory("OrderedThreadPool"));
		//chain.addLast("threadPool", new ExecutorFilter(threadpool));

		chain.addLast("ThreadPool",new ExecutorFilter(Executors.newCachedThreadPool()));
		
		int recsize = 1024*1024*2;
		int sendsize = 1024*1024*2;
		int timeout = 2;
		//
		SocketSessionConfig sc = acceptor.getSessionConfig();
		sc.setReuseAddress(true);// 设置每一个非主监听连接的端口可以重用
		sc.setReceiveBufferSize(recsize);// 设置输入缓冲区的大小
		sc.setSendBufferSize(sendsize);// 设置输出缓冲区的大小
		sc.setReadBufferSize(recsize);
		sc.setTcpNoDelay(true);// flush函数的调用 设置为非延迟发送，为true则不组装成大包发送，收到东西马上发出
		sc.setSoLinger(0);
		//设置超时
		sc.setIdleTime(IdleStatus.BOTH_IDLE, timeout);
		acceptor.bind(new InetSocketAddress(listenPort));
	}
	
	
	/**
	 * 开始监听端口 后台
	 * @param iohandler
	 * @param listenPort
	 * @throws Exception
     */
	public  void startHostListner(IoHandler iohandler,int listenPort) throws Exception{
		acceptor = new NioSocketAcceptor();
		acceptor.setBacklog(10);
		acceptor.setReuseAddress(true);
		acceptor.setHandler(iohandler);

        DefaultIoFilterChainBuilder chain = acceptor.getFilterChain();
        IoFilter protocol = new ProtocolCodecFilter(new GameProtocolcodecFactory());
        chain.addLast("codec", protocol);
		chain.addLast("ThreadPool",new ExecutorFilter(Executors.newCachedThreadPool()));
		
		int recsize = 1024*1024*2;
		int sendsize = 1024*1024*2;
		int timeout = 2;
		SocketSessionConfig sc = acceptor.getSessionConfig();
		sc.setReuseAddress(true);// 设置每一个非主监听连接的端口可以重用
		sc.setReceiveBufferSize(recsize);// 设置输入缓冲区的大小
		sc.setSendBufferSize(sendsize);// 设置输出缓冲区的大小
		sc.setReadBufferSize(recsize);
		sc.setTcpNoDelay(true);// flush函数的调用 设置为非延迟发送，为true则不组装成大包发送，收到东西马上发出
		sc.setSoLinger(0);
		//设置超时
		sc.setIdleTime(IdleStatus.BOTH_IDLE, timeout);
		acceptor.bind(new InetSocketAddress(listenPort));
	}

	/**
	 *
	 * @throws InterruptedException
     */
	public void stop() throws InterruptedException{
		acceptor.unbind();
		//threadpool.shutdown();
		//threadpool.awaitTermination(5, TimeUnit.SECONDS);
		acceptor.dispose(true);
	}
	
}
