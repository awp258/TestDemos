package com.jw.shotRecord.listener;

import android.graphics.Bitmap;

/**
 * =====================================
 * 作    者: 陈嘉桐
 * 版    本：1.1.4
 * 创建日期：2017/4/26
 * 描    述：
 * =====================================
 */
public interface JCameraListener {

    void captureSuccess(Bitmap bitmap);

    void recordSuccess(String url, Bitmap firstFrame);

    void captureEdiit(Bitmap bitmap);

    void recordEdiit(String url, Bitmap firstFrame);
}
