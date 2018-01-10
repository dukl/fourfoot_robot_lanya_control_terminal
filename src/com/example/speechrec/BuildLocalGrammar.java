package com.example.speechrec;


import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.util.ResourceUtil;
import com.iflytek.cloud.util.ResourceUtil.RESOURCE_TYPE;

import android.content.Context;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * ��������������﷨
 * 
 * @author kongqw
 * 
 */
public abstract class BuildLocalGrammar {

    /**
     * �����﷨�Ļص�
     * 
     * @param errMsg
     *            null ����ɹ�
     */
    public abstract void result(String errMsg, String grammarId);

    // Log��ǩ
    private static final String TAG = "BuildLocalGrammar";

    public static final String GRAMMAR_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/msc/test";
    // ������
    private Context mContext;
    // ����ʶ�����
    private SpeechRecognizer mAsr;

    public BuildLocalGrammar(Context context) {
        mContext = context;

        // ��ʼ��ʶ�����
        mAsr = SpeechRecognizer.createRecognizer(context, new InitListener() {

            @Override
            public void onInit(int code) {
                Log.d(TAG, "SpeechRecognizer init() code = " + code);
                if (code != ErrorCode.SUCCESS) {
                    result(code + "", null);
                    Log.d(TAG, "��ʼ��ʧ��,�����룺" + code);
                    Toast.makeText(mContext, "��ʼ��ʧ��,�����룺" + code, Toast.LENGTH_SHORT).show();
                }
            }
        });
    };

    /**
     * �����﷨
     * 
     * @return
     */
    public void buildLocalGrammar() {
        try {
            /*
             * TODO �����Ҫ�ڳ�����ά��bnf�ļ������������������ά����һЩ�߼�
             * ��������鷳��Ҫһֱ��bnf�ļ�������Ĵ�����Բ��ö��������Ҹ��˲�����һֱ�ֶ��޸�bnf�ļ�
             * �����ݶ����Ժ�����׳���������Bug������ÿ�θ�֮ǰ�ȱ��ݡ� �����ó���ά��bnf�ļ���
             */

            /*
             * �����﷨
             */
            String mContent;// �﷨���ʵ���ʱ����
            String mLocalGrammar = FucUtil.readFile(mContext, "kqw.bnf", "utf-8");
            mContent = new String(mLocalGrammar);
            mAsr.setParameter(SpeechConstant.PARAMS, null);
            // �����ı������ʽ
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            // ������������
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            // �����﷨����·��
            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, GRAMMAR_PATH);
            // ʹ��8k��Ƶ��ʱ����⿪ע��
            // mAsr.setParameter(SpeechConstant.SAMPLE_RATE, "8000");
            // ������Դ·��
            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
            // �����﷨
            int ret = mAsr.buildGrammar("bnf", mContent, new GrammarListener() {

                @Override
                public void onBuildFinish(String grammarId, SpeechError error) {
                    if (error == null) {
                        Log.d(TAG, "�﷨�����ɹ�");
                        result(null, grammarId);
                    } else {
                        Log.d(TAG, "�﷨����ʧ��,������:" + error.getErrorCode());
                        result(error.getErrorCode() + "", grammarId);
                    }
                }
            });
            if (ret != ErrorCode.SUCCESS) {
                Log.d(TAG, "�﷨����ʧ��,������:" + ret);
                result(ret + "", null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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

}