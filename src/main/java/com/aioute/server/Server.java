package com.aioute.server;

import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.aioute.pool.Pool;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.stream.ChunkedWriteHandler;

public class Server
{

	public static void main(String[] args) throws Exception
	{

		// 开启tcp端口 19090
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					new Server().bindTcp(19090);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();

		// 开启http端口 18080
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					new Server().bindHttp(18080);
				} catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		}).start();

		Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Runnable()
		{
			public void run()
			{
				Pool.checkTimeOut();
			}
		}, 1, 5, TimeUnit.SECONDS);
	}

	// tcp服务设置
	public void bindTcp(int port)
	{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try
		{
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>()
					{
						@Override
						protected void initChannel(SocketChannel sc) throws Exception
						{
							// 封装，解封 硬件byte数组
							sc.pipeline().addLast(new TcpCoderAndEncoder());
							// 处理业务逻辑
							sc.pipeline().addLast(new TcpDeal());
						}
					});
			ChannelFuture cf = server.bind(port).sync();

			cf.channel().closeFuture().sync();

		} catch (Exception e)
		{

		} finally
		{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	// http服务设置
	public void bindHttp(int port)
	{
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try
		{
			ServerBootstrap server = new ServerBootstrap();
			server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 100).handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new ChannelInitializer<SocketChannel>()
					{
						@Override
						protected void initChannel(SocketChannel sc) throws Exception
						{
							sc.pipeline().addLast("http-decoder", new HttpRequestDecoder());
							sc.pipeline().addLast("http-encoder", new HttpResponseEncoder());
							sc.pipeline().addLast("http-aggregator", new HttpObjectAggregator(1048576));
							sc.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
							sc.pipeline().addLast(new HttpHandler());
						}
					});
			ChannelFuture cf = server.bind("127.0.0.1", port).sync();
			cf.channel().closeFuture().sync();
		} catch (Exception e)
		{
		} finally
		{
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}
}
