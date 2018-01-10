package com.example.speechrec;

import java.io.InputStream;
import android.content.Context;
import android.util.Log;

/**
 * �����Ժ�����չ��
 * 
 * @author kongqw
 * 
 */
public class FucUtil {

    // Log��ǩ
    private static final String TAG = "FucUtil";

    /**
     * ��ȡassetĿ¼���ļ�����
     * 
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            e.printStackTrace();
        }
        return result;
    }
}