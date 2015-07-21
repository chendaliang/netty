package com.aioute.server;

import com.aioute.entity.Message;
import com.aioute.pool.Pool;

import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

public class TcpDeal extends ChannelHandlerAdapter
{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{
		super.channelRead(ctx, msg);
		Message message = (Message) msg;

		// 处理转换完硬件编码后的业务逻辑
		switch (message.getCommand() & 0xff)
		{
		// 13、 链路接口检测命令
		case 0x8d:
			lianlujiekoujiance(ctx, message);
			break;

		default:
			throw new Exception("Message 命令错误！！！");
		}
	}

	private void lianlujiekoujiance(ChannelHandlerContext ctx, Message message)
	{
		switch (message.getData()[0] & 0xff)
		{
		// 硬件登陆
		case 1:
			// 加到pool中
			Pool.addHardwarePool(message, ctx);
			// 返回登陆确认信息
			Message resp = new Message();
			resp.setStart(message.getStart());
			resp.setNum(message.getNum());
			resp.setAddress(message.getAddress());
			resp.setCommand((byte) 0x0d);
			resp.setLenth((byte) 39);
			byte[] data = new byte[39];
			for (int i = 0; i < data.length; i++)
			{
				data[i] = 1;
			}
			data[5] = 0;
			data[0] = 2;
			resp.setData(data);
			resp.setChecksum(resp.makeChecksum());
			ctx.writeAndFlush(resp);
			break;

		// 硬件心跳
		case 2:
			// 更新pool的时间
			Pool.updateTime(message);
			// TODO 返回心跳回应
			break;

		default:
			break;
		}
	}
}
