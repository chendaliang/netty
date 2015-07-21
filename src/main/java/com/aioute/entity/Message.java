package com.aioute.entity;

import io.netty.buffer.ByteBuf;

/**
 * @author AOTC 和硬件间传递的信息的封装
 *
 */
public class Message
{

	byte start;
	byte num;
	byte[] address = new byte[4];
	byte command;
	byte lenth;
	byte[] data;
	byte checksum;

	// ----------工具方法---------------------------------
	// message转成硬件信息
	public static ByteBuf message2Bytebuf(Message message, ByteBuf buf)
	{
		buf.writeByte(message.getStart());
		buf.writeByte(message.getNum());
		for (int i = 0; i < message.getAddress().length; i++)
		{
			buf.writeByte(message.getAddress()[message.getAddress().length - 1]);
		}
		buf.writeByte(message.getCommand());
		buf.writeByte(message.getLenth());
		for (int i = 0; i < message.getData().length; i++)
		{
			buf.writeByte(message.getData()[message.getData().length - 1]);
		}

		buf.writeByte(message.getChecksum());
		return buf;
	}

	// 检查硬件给的message的正确性
	public static boolean isRight(Message message)
	{
		boolean ret = true;

		// 检查 0XAA
		if ((message.getStart() & 0xff) != 0xaa)
		{
			ret = false;
			System.out.println("检查硬件给的信息:byte不是0XAA开始！！");
		}

		// 检查 Checksum
		int sum = message.getCommand() + message.getLenth() + message.getNum();
		for (int i = 0; i < message.getAddress().length; i++)
		{
			sum = sum + message.getAddress()[i];
		}
		for (int i = 0; i < message.getData().length; i++)
		{
			sum = sum + message.getData()[i];
		}
		if (sum != message.getChecksum())
		{
			ret = false;
			System.out.println("检查硬件给的信息:数据校验和Checksum错误！！");
		}

		// 检查 Lenth
		if (message.getData().length != message.getLenth() - 1)
		{
			System.out.println("检查 检查硬件给的信息:数据长度Lenth错误");
			ret = false;
		}
		return ret;
	}

	// 硬件信息转成message
	public static Message byteBuf2Message(ByteBuf buf)
	{
		Message message = new Message();
		message.setStart(buf.readByte());
		message.setNum((buf.readByte()));

		byte[] ls = new byte[4];
		for (int i = 0; i < ls.length; i++)
		{
			ls[i] = (buf.readByte());
		}
		message.setAddress(ls);
		message.setCommand((buf.readByte()));
		message.setLenth((buf.readByte()));

		ls = new byte[buf.readableBytes() - 1];
		for (int i = 0; i < ls.length; i++)
		{
			ls[i] = (buf.readByte());
		}
		message.setData(ls);
		message.setChecksum((buf.readByte()));
		return message;
	}

	// 计算 Checksum
	public byte makeChecksum()
	{
		int ls = 0;
		ls = num + command + lenth;
		for (int i = 0; i < address.length; i++)
		{
			ls = ls + address[i];
		}
		for (int i = 0; i < data.length; i++)
		{
			ls = ls + data[i];
		}
		return (byte) ls;
	}

	// ------get----set-------------------------
	public byte getStart()
	{
		return start;
	}

	public void setStart(byte start)
	{
		this.start = start;
	}

	public byte getNum()
	{
		return num;
	}

	public void setNum(byte num)
	{
		this.num = num;
	}

	public byte[] getAddress()
	{
		return address;
	}

	public void setAddress(byte[] address)
	{
		this.address = address;
	}

	public byte getCommand()
	{
		return command;
	}

	public void setCommand(byte command)
	{
		this.command = command;
	}

	public byte getLenth()
	{
		return lenth;
	}

	public void setLenth(byte lenth)
	{
		this.lenth = lenth;
	}

	public byte[] getData()
	{
		return data;
	}

	public void setData(byte[] data)
	{
		this.data = data;
	}

	public byte getChecksum()
	{
		return checksum;
	}

	public void setChecksum(byte checksum)
	{
		this.checksum = checksum;
	}

	@Override
	public String toString()
	{
		String ls = "Message [start=" + unSign16Str(start) + ", num=" + unSign16Str(num) + ", address=";
		for (int i = 0; i < address.length; i++)
		{
			if (i == 0)
			{
				ls = ls + unSign16Str(address[i]);
			} else
			{
				ls = ls + "," + unSign16Str(address[i]);
			}
		}
		ls = ls + ", command=" + unSign16Str(command) + ", lenth=" + unSign16Str(lenth) + ", data=";
		for (int i = 0; i < data.length; i++)
		{
			if (i == 0)
			{
				ls = ls + unSign16Str(data[i]);
			} else
			{
				ls = ls + "," + unSign16Str(data[i]);
			}
		}
		ls = ls + ", checksum=" + unSign16Str(checksum) + "]";
		return ls.toUpperCase();
	}

	public static String unSign16Str(byte b)
	{
		String ls = Integer.toHexString(b & 0xff);
		if (ls.length() == 1)
		{
			ls = "0" + ls;
		}
		return ls;
	}
}
