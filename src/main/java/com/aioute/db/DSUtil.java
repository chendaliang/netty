package com.aioute.db;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import com.mchange.v2.c3p0.DataSources;

public class DSUtil
{

	private static String url;
	private static String username;
	private static String pwd;
	private static DataSource ds_pooled;

	/**
	 * 加载数据库连接的配置文件和驱动
	 */
	static
	{
		try
		{
			Class.forName("com.mysql.jdbc.Driver");
			url = "jdbc:mysql://127.0.0.1:3306/ceshi?useUnicode=true&characterEncoding=utf8";
			username = "root";
			pwd = "shishi";

			// 设置连接数据库的配置信息
			DataSource ds_unpooled = DataSources.unpooledDataSource(url, username, pwd);

			Map<String, Object> pool_conf = new HashMap<String, Object>();
			// 设置最大连接数
			pool_conf.put("maxPoolSize", 10);
			ds_pooled = DataSources.pooledDataSource(ds_unpooled, pool_conf);
		} catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		} catch (SQLException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * 获取连接对象
	 */
	public static Connection getConnection() throws SQLException
	{
		return ds_pooled.getConnection();
	}

	/**
	 * 释放连接池资源
	 */
	public static void clearup()
	{
		if (ds_pooled != null)
		{
			try
			{
				DataSources.destroy(ds_pooled);
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * 资源关闭
	 * 
	 * @param rs
	 * @param stmt
	 * @param conn
	 */
	public static void close(ResultSet rs, Statement stmt, Connection conn)
	{
		if (rs != null)
		{
			try
			{
				rs.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		if (stmt != null)
		{
			try
			{
				stmt.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}

		if (conn != null)
		{
			try
			{
				conn.close();
			} catch (SQLException e)
			{
				e.printStackTrace();
			}
		}
	}
}