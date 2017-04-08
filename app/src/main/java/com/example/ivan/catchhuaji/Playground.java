package com.example.ivan.catchhuaji;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Toast;

import java.util.Random;

/**
 * Created by Ivan on 2017/4/6.
 */

public class Playground extends SurfaceView implements View.OnTouchListener
{
	private static final int ROW = 9;
	private static final int COL = 9;
	
	private static final int DIR_COUNT = 6;
	private static final int DIR_UNABLE = -1;
	private static final int DIR_LEFT = 0;
	private static final int DIR_LEFT_TOP = 1;
	private static final int DIR_RIGHT_TOP = 2;
	private static final int DIR_RIGHT = 3;
	private static final int DIR_RIGHT_BOT = 4;
	private static final int DIR_LEFT_BOT = 5;
	
	private static int BLOCK_COUNT = 10;
	private static int WIDTH = 100;
	
	private Dot[][] matrix;
	private Dot cat;
	
	public Playground(Context context)
	{
		super(context);
		init();
	}
	
	private void init()
	{
		getHolder().addCallback(callback);
		cat = new Dot(COL / 2, ROW / 2);
		cat.setStatus(Dot.STATUS_IN);
		matrix = new Dot[ROW][COL];
		for (int i = 0; i < ROW; ++i)
		{
			for (int j = 0; j < COL; ++j)
			{
				matrix[i][j] = new Dot(j, i);
			}
		}
		setOnTouchListener(this);
		restart();
	}
	
	private void redraw()
	{
		Canvas canvas = getHolder().lockCanvas();
		canvas.drawColor(Color.LTGRAY);
		Paint paint = new Paint();
		
		for (int i = 0; i < ROW; ++i)
		{
			int offset = i % 2 != 0 ? WIDTH / 2 : 0;
			for (int j = 0; j < COL; ++j)
			{
				Dot thisDot = matrix[i][j];
				switch (thisDot.getStatus())
				{
					case Dot.STATUS_IN:
						paint.setColor(0xFFFF0000);
						break;
					case Dot.STATUS_OFF:
						paint.setColor(0xFFEEEEEE);
						break;
					case Dot.STATUS_ON:
						paint.setColor(0xFFFFAA00);
						break;
				}
				paint.setFlags(Paint.ANTI_ALIAS_FLAG);//抗锯齿
				canvas.drawOval(new RectF(thisDot.getX() * WIDTH + offset, thisDot.getY() * WIDTH, (thisDot.getX() + 1) * WIDTH + offset, (thisDot.getY() + 1) * WIDTH), paint);
			}
		}
		getHolder().unlockCanvasAndPost(canvas);
	}
	
	private void restart()
	{
		for (int i = 0; i < ROW; ++i)
			for (int j = 0; j < COL; ++j)
				matrix[i][j].setStatus(Dot.STATUS_OFF);
		
		getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_IN);
		
		Random random = new Random();
		for (int i = 0; i < BLOCK_COUNT; )
		{
			int x = random.nextInt(ROW);
			int y = random.nextInt(COL);
			if (matrix[x][y].getStatus() == Dot.STATUS_OFF)
			{
				matrix[x][y].setStatus(Dot.STATUS_ON);
				++i;
			}
		}
	}
	
	private Dot getDot(int x, int y)
	{
		return matrix[y][x];
	}
	
	SurfaceHolder.Callback callback = new SurfaceHolder.Callback()
	{
		@Override
		public void surfaceCreated(SurfaceHolder holder)
		{
			redraw();
		}
		
		@Override
		public void surfaceChanged(SurfaceHolder holder, int format, int width, int height)
		{
			WIDTH = width / (COL + 1);
			redraw();
		}
		
		@Override
		public void surfaceDestroyed(SurfaceHolder holder)
		{
			
		}
	};
	
	private Dot getNeighbour(Dot thisDot, int dir)
	{
		switch (dir)
		{
			case DIR_LEFT:
				return getDot(thisDot.getX() - 1, thisDot.getY());
			case DIR_LEFT_TOP:
				if (thisDot.getY() % 2 == 0)
					return getDot(thisDot.getX() - 1, thisDot.getY() - 1);
				return getDot(thisDot.getX(), thisDot.getY() - 1);
			case DIR_RIGHT_TOP:
				if (thisDot.getY() % 2 == 0)
					return getDot(thisDot.getX(), thisDot.getY() - 1);
				return getDot(thisDot.getX() + 1, thisDot.getY() - 1);
			case DIR_RIGHT:
				return getDot(thisDot.getX() + 1, thisDot.getY());
			case DIR_RIGHT_BOT:
				if (thisDot.getY() % 2 == 0)
					return getDot(thisDot.getX(), thisDot.getY() + 1);
				return getDot(thisDot.getX() + 1, thisDot.getY() + 1);
			case DIR_LEFT_BOT:
				if (thisDot.getY() % 2 == 0)
					return getDot(thisDot.getX() - 1, thisDot.getY() + 1);
				return getDot(thisDot.getX(), thisDot.getY() + 1);
		}
		return null;
	}
	
	/**
	 * 获取点 thisDot 到方向 dir 的边界或第一个障碍的距离。
	 *
	 * @param thisDot
	 * @param dir
	 * @return 若此方向无障碍，则返回朝此方向到边界的点数，若有障碍，则返回朝此方向第一个障碍的点数 distance*(-1) - 1。
	 */
	private int getDistance(Dot thisDot, int dir)
	{
		int distance = 0;
		Dot nextDot = getNeighbour(thisDot, dir);
		while (nextDot != null)
		{
			if (!isAtEdge(nextDot) && nextDot.getStatus() == Dot.STATUS_OFF)
			{
				++distance;
			} else if (nextDot.getStatus() == Dot.STATUS_ON)
			{
				distance = distance * (-1) - 1;
				break;
			} else
			{
				++distance;
				break;
			}
			nextDot = getNeighbour(nextDot, dir);
		}
		return distance;
	}
	
	private void catMoveTo(Dot dot)
	{
		getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_OFF);
		cat.setXY(dot.getX(), dot.getY());
		getDot(cat.getX(), cat.getY()).setStatus(Dot.STATUS_IN);
	}
	
	private void move()
	{
		if (isAtEdge(cat))
		{
			lose();
			return;
		}
		
		int bestDir = DIR_UNABLE;
		int[] distances = new int[DIR_COUNT];
		
		//第一次遍历各个方向，判断是否有方向直达边缘且距离边缘最近，有则将 bestDir 设置为此方向。
		for (int i = DIR_LEFT; i < DIR_COUNT; ++i)
		{
			distances[i] = getDistance(cat, i);
			if (bestDir == DIR_UNABLE && distances[i] > 0
					|| bestDir != DIR_UNABLE && distances[i] <= distances[bestDir] && distances[i] > 0)
				bestDir = i;
		}
		//若无直达边缘的方向则进行第二次遍历各个方向，将 bestDir 设置为离障碍最远的方向。
		if (bestDir == DIR_UNABLE)
			for (int i = DIR_LEFT; i < DIR_COUNT; ++i)
				if (bestDir == DIR_UNABLE && distances[i] < -1
						|| bestDir != DIR_UNABLE && distances[i] <= distances[bestDir])
					bestDir = i;
		
		if (bestDir == DIR_UNABLE)
			win();
		else
			catMoveTo(getNeighbour(cat, bestDir));
	}
	
	private void win()
	{
		Toast.makeText(getContext(), "You Win!", Toast.LENGTH_SHORT).show();
	}
	
	private void lose()
	{
		Toast.makeText(getContext(), "You Lose!", Toast.LENGTH_SHORT).show();
	}
	
	private boolean isAtEdge(Dot dot)
	{
		if (dot.getX() * dot.getY() == 0 || dot.getX() == COL - 1 || dot.getY() == ROW - 1)
			return true;
		return false;
	}
	
	@Override
	public boolean onTouch(View v, MotionEvent event)
	{
		if (event.getAction() == MotionEvent.ACTION_UP)
		{
			int touchX = (int) event.getX();
			int touchY = (int) event.getY();
			int x;
			int y = touchY / WIDTH;
			if (y % 2 == 0)
			{
				x = touchX / WIDTH;
			} else
			{
				x = (touchX - WIDTH / 2) / WIDTH;
			}
			if (x < COL && y < ROW)
			{
				Dot thisDot = getDot(x, y);
				if (thisDot.getStatus() == Dot.STATUS_OFF)
				{
					thisDot.setStatus(Dot.STATUS_ON);
					move();
				}
			}
			redraw();
		}
		return true;
	}
}
