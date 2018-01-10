package com.example.speechrec;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;

@SuppressLint("NewApi")
public class PaintVedio extends SurfaceView implements Callback, Runnable {
	//��Ļ��С����
	private static int screenWidth;//�����Ļ�ߴ籣��������
	private static int screenHeight;
	//�������̱߳���
	private boolean runFlag = false;//������Ϊ��������.��ʱû�жԴ˱�����ʵ������
	private static SurfaceHolder holder;//��ֵʵ�ʵ�surfaceView holder��ַ.ֻ�Ǳ��ڵ���
	private HttpURLConnection conn;//URL HTTP��ַ������,ֻ����Ϊ�����.��������viewʱ�ر�
	private Thread thread;//������Դ����ͼ���̱߳���,��surfaceView״̬�ı�ʱ ���û�ر�����

	public PaintVedio(Context context, AttributeSet attrs) {
		super(context, attrs);//���й���,�����
		screenValue();//��Ļ�ߴ縳ֵ
		holder = this.getHolder();
		holder.addCallback(this);//�����Դ��Ļص�����
	}

	// ========================================
	/**
	 * �����Ļ����ֵ
	 */
	private void screenValue() {
		DisplayMetrics dm = new DisplayMetrics();
		dm = getResources().getDisplayMetrics();
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		runFlag = true;
		thread = new Thread(this);
		thread.start();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
//		change�¼������л�������ʱ����.���г�ʼ������һ��
//		runFlag = true;
//		thread = new Thread(this);
//		thread.start();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		runFlag = false;
		conn.disconnect();
	}

	@Override
	public void run() {
		Canvas c;
		Bitmap bmp;
		InputStream is;
		URL videoURL = null;
//		Paint p = new Paint(); // ��������,��ͼ����Բ���Ҫ
		String imageURL = "http://192.168.8.1:8083/?action=snapshot";//��Ƶ��ַ,ע�������ý���action.
		try {
			videoURL = new URL(imageURL);;	
		} catch (Exception e) {
		}
		//��ͼ��������
		BitmapFactory.Options o = new BitmapFactory.Options();// ����ԭͼ����ֵ
		o.inPreferredConfig = Bitmap.Config.ARGB_8888;// ������
		while (runFlag) {
			c = null;
			try {
				synchronized (holder) {
					c = holder.lockCanvas();// ����������һ����������Ϳ���ͨ���䷵�صĻ�������Canvas���������滭ͼ�Ȳ����ˡ�
					// ===========================================================
				//	���Ӻ������,������ֻ����һ��,��Ҫ�������Ӳ������
				 	conn = (HttpURLConnection)videoURL.openConnection();//�˷�����new HttpURLConnection������connect()
//					conn.connect();//getInputStream���Զ����ô˷���.�˷���һ������new HttpURLConnection֮��.(new��ʱ��û�з�����������)
					is = conn.getInputStream(); //��ȡ��
					bmp = BitmapFactory.decodeStream(is, null, o);
					bmp = Bitmap.createScaledBitmap(bmp, screenWidth,
							screenHeight, true);// ��ͼƬ������Ļ�ߴ��������
					c.drawBitmap(bmp, 0, 0, null);
					
					Thread.sleep(30);// ���ʱ��,�������������Ž�������.����Լ�ֱܷ�42��������ͼ��.					
				}
			} catch (Exception e) {
//				System.out.println(e.getMessage());
			}finally{
				holder.unlockCanvasAndPost(c);// ������ͼ���ύ
				conn.disconnect();
			}
		}

	}
}
