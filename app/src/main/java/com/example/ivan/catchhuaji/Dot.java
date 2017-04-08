package com.example.ivan.catchhuaji;

/**
 * Created by Ivan on 2017/4/6.
 */

public class Dot
{
	public static final int STATUS_ON = 0;
	public static final int STATUS_OFF = 1;
	public static final int STATUS_IN = 2;
	
	private int status;
	private int x, y;
	
	public Dot(int x, int y)
	{
		this.x = x;
		this.y = y;
		status = STATUS_OFF;
	}
	
	public void setXY(int x, int y)
	{
		this.x = x;
		this.y = y;
	}
	
	public int getX()
	{
		return x;
	}
	
	public int getY()
	{
		return y;
	}
	
	public int getStatus()
	{
		return status;
	}
	
	public void setStatus(int status)
	{
		this.status = status;
	}
}
