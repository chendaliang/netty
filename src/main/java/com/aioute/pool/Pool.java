package com.aioute.pool;

import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.aioute.entity.Message;

import io.netty.channel.ChannelHandlerContext;

public class Pool
{
	public static Map<String, ChannelHandlerContext> hardwarePool = Collections
			.synchronizedMap(new HashMap<String, ChannelHandlerContext>());
	public static Map<String, Integer> hardwarePoolReadedTime = Collections
			.synchronizedMap(new HashMap<String, Integer>());

	// 硬件登陆 添加
	public static void addHardwarePool(Message message, ChannelHandlerContext channelHandlerContext)
	{
		String key = new String(message.getAddress());
		if (hardwarePool.get(key) == null)
		{
			hardwarePool.put(key, channelHandlerContext);
			hardwarePoolReadedTime.put(key, 0);
			System.out.println("当前hardwarePool大小：" + hardwarePool.size());
		}
	}

	// 硬件心跳更新时间
	public static void updateTime(Message message)
	{
		String key = new String(message.getAddress());
		if (hardwarePool.get(key) != null)
		{
			hardwarePoolReadedTime.put(key, 0);
			System.out.println("更新心跳！！");
		}
	}

	// 定时清理pool
	public static void checkTimeOut()
	{
		for (Iterator<String> it = hardwarePoolReadedTime.keySet().iterator(); it.hasNext();)
		{
			String key = (String) it.next();
			// 超时清理
			if (hardwarePoolReadedTime.get(key) >= 6)
			{
				hardwarePoolReadedTime.remove(key);
				hardwarePool.remove(key);
				System.out.println("清理链接！！！");
			} else
			{
				hardwarePoolReadedTime.put(key, hardwarePoolReadedTime.get(key) + 1);
			}
		}
	}
}
