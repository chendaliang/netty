package com.aioute.server;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.util.CharsetUtil;

public class HttpHandler extends ChannelHandlerAdapter
{
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception
	{

		FullHttpRequest request = (FullHttpRequest) msg;
		String url = request.uri();
		String cont = request.content().toString(CharsetUtil.UTF_8);
		// --------------------测试--------------------------
		System.out.println("请求的url：" + url);
		System.out.println("请求带的数据：" + cont);
		// --------------------------------------------------
		// TODO 下面处理http请求

		// 模拟返回
		String res = "I am OK";
		FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK,
				Unpooled.wrappedBuffer(res.getBytes("UTF-8")));
		response.headers().set("Content-Type", "text/plain; charset=utf-8");
		response.headers().set("Content-Length", String.valueOf(response.content().readableBytes()));
		ctx.write(response);
		ctx.flush();

	}
}
