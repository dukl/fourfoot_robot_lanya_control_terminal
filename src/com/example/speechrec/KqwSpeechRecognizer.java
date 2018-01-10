package com.example.speechrec;


import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * �����ʶ��
 * 
 * @author kongqw
 * 
 */
public abstract class KqwSpeechRecognizer {
    /**
     * ��ʼ���Ļص�
     * 
     * @param flag
     *            true ��ʼ���ɹ� false ��ʼ��ʧ��
     */
    public abstract void initListener(boolean flag);

    public abstract void resultData(String data);

    public abstract void speechLog(String log);

    // Log��ǩ
    private static final String TAG = "KqwLocalCommandRecognizer";

    public static final String GRAMMAR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test";
    // ������
    private Context mContext;
    // ����ʶ�����
    private SpeechRecognizer mAsr;

    public KqwSpeechRecognizer(Context context) {
        mContext = context;

        // ��ʼ��ʶ�����
        mAsr = SpeechRecognizer.createRecognizer(context, new InitListener() {

            @Override
            public void onInit(int code) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    initListener(false);
                    Toast.makeText(mContext, "��ʼ��ʧ��,�����룺" + code, Toast.LENGTH_SHORT).show();
                } else {
                    initListener(true);
                }
            }
        });

    }

    /**
     * ��������
     */
    public void setParam() {

        // ��ղ���
        mAsr.setParameter(SpeechConstant.PARAMS, null);
        // ����ʶ������ ��������
        mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
        // mAsr.setParameter(SpeechConstant.ENGINE_TYPE,
        // SpeechConstant.TYPE_MIX);
        // mAsr.setParameter(SpeechConstant.ENGINE_TYPE, "mixed");
        // // ���ñ���ʶ����Դ
        mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
        // �����﷨����·��
        mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, GRAMMAR_PATH);
        // ���÷��ؽ����ʽ
        mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // ���ñ���ʶ��ʹ���﷨id
        mAsr.setParameter(SpeechConstant.LOCAL_GRAMMAR, "kqw");
        // ����ʶ�������ֵ
        mAsr.setParameter(SpeechConstant.MIXED_THRESHOLD, "10");
        // ʹ��8k��Ƶ��ʱ����⿪ע��
        // mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
        mAsr.setParameter(SpeechConstant.DOMAIN, "iat");
        mAsr.setParameter(SpeechConstant.NLP_VERSION, "2.0");
        mAsr.setParameter("asr_sch", "1");
        // mAsr.setParameter(SpeechConstant.RESULT_TYPE, "json");
     //   mAsr.setParameter(SpeechConstant.DOMAIN, "iat");//������Ϊ������д
		mAsr.setParameter(SpeechConstant.LANGUAGE, "zh_cn");//����
		mAsr.setParameter(SpeechConstant.ACCENT, "mandarin ");//��ͨ��
    }

    // ��ȡʶ����Դ·��
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        // ʶ��ͨ����Դ
        tempBuffer.append(ResourceUtil.generateResourcePath(mContext, RESOURCE_TYPE.assets, "asr/common.jet"));
        // ʶ��8k��Դ-ʹ��8k��ʱ����⿪ע��
        // tempBuffer.append(";");
        // tempBuffer.append(ResourceUtil.generateResourcePath(this,
        // RESOURCE_TYPE.assets, "asr/common_8k.jet"));
        return tempBuffer.toString();
    }

    int ret = 0;// �������÷���ֵ

    /**
     * ��ʼʶ��
     */
    public void startListening() {
        // ���ò���
        setParam();

        ret = mAsr.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            Log.i(TAG, "ʶ��ʧ��,������: " + ret);
        }
    }

    /**
     * ʶ���������
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        StringBuffer stringBuffer = new StringBuffer();

        public void onVolumeChanged(int volume) {
            Log.i(TAG, "��ǰ����˵����������С��" + volume);
            speechLog("��ǰ����˵����������С��" + volume);
        }

        @Override
        public void onResult(final RecognizerResult result, boolean isLast) {
            /*
             * TODO ƴ�ӷ��ص�����
             * 
             * ���ﷵ�ص���Json���ݣ����巵�ص�������������ʷ��ص�Json�������巵�ص�Json����Ҫ���ж��Ժ��ڶ��������ݽ���ƴ��
             */
            stringBuffer.append(result.getResultString()).append("\n\n");
            
            // isLastΪtrue��ʱ�򣬱�ʾһ�仰˵�꣬��ƴ�Ӻ��������һ�仰����
            if (isLast) {
                // ���ݻص�
            	String text = JsonParser.parseIatResult(stringBuffer.toString());
            //    resultData(stringBuffer.toString());
            	resultData(text);
            }
        }

        @Override
        public void onEndOfSpeech() {
            Log.i(TAG, "����˵��");
            speechLog("����˵��");
        }

        @Override
        public void onBeginOfSpeech() {
            stringBuffer.delete(0, stringBuffer.length());
            Log.i(TAG, "��ʼ˵��");
            speechLog("��ʼ˵��");
        }

        @Override
        public void onError(SpeechError error) {
            Log.i(TAG, "error = " + error.getErrorCode());
            if (error.getErrorCode() == 20005) {
                // ���������û��ʶ��Ҳû����������
          //      resultData("û�й������﷨");
                speechLog("û�й������﷨");
                /*
                 * TODO
                 * ������������������ǲ���ص�20005�Ĵ���ֻ�е����������ʶ��ƥ�䣬��������Ҳʧ�ܵ�����£��᷵��20005
                 * ��������Լ�������������ظ���û�����塱�Ȼظ�
                 */
            } else {
                /*
                 * TODO
                 * ���������кܶ࣬��Ҫ���������������������ڳ���û�д��������£�ֻ��ص�һ��û�м�⵽˵���Ĵ���û�Ǵ�Ļ���������10118
                 */
            }
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            Log.i(TAG, "eventType = " + eventType);
        }

		@Override
		public void onVolumeChanged(int arg0, byte[] arg1) {
			// TODO Auto-generated method stub
			speechLog("��ǰ����˵����������С��" + arg0);
		}

    };

}