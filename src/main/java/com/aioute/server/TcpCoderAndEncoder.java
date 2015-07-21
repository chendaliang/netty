package com.aioute.server;

import java.util.List;

import com.aioute.entity.Message;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageCodec;

/**
 * @author AOTC 封装硬件的byte数组成message类
 *
 */
public class TcpCoderAndEncoder extends ByteToMessageCodec<Message> {

	// 到硬件的编码
	@Override
	protected void encode(ChannelHandlerContext ctx, Message msg, ByteBuf out) throws Exception {
		System.out.println("返回硬件：-----" + msg);
		Message.message2Bytebuf(msg, out);
	}

	// 从硬件来的解码
	@Override
	protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
		// byte转成message
		Message me = Message.byteBuf2Message(in);
		System.out.println("收到硬件信息:-----" + me);
		if (Message.isRight(me)) {
			out.add(me);
		} else {
			throw new Exception("接受错误硬件信息！！！");
		}
	}
}
