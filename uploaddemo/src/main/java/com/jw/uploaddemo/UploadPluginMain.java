package com.jw.uploaddemo;

import android.app.Activity;
import android.content.Intent;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.crop.AspectRatio;
import com.jw.galary.img.loader.GlideImageLoader;
import com.jw.galary.img.ui.ImageGridActivity;
import com.jw.galary.img.view.CropImageView;
import com.jw.galary.video.VideoGridActivity;
import com.jw.galary.video.VideoPicker;
import com.jw.shotRecord.ShotRecordMainActivity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 彭保生 on 2018/2/28.
 */

public class UploadPluginMain {
    private int outputType = 0;//输出格式，0表示输出路径，1表示base64字符串
    public void execute(String action, JSONArray args, Activity activity) throws JSONException {
        JSONObject params = args.getJSONObject(0);
        if (params != null) {
            //颜色相关设置
            if (params.has("oKButtonTitleColorNormal")) {
                ColorCofig.INSTANCE.setEditOKButtonTitleColorNormal(params.getString("oKButtonTitleColorNormal"));
            }
            if (params.has("oKButtonTitleColorDisabled")) {
                ColorCofig.INSTANCE.setOKButtonTitleColorDisabled(params.getString("oKButtonTitleColorDisabled"));
            }
            if (params.has("naviBgColor")) {
                ColorCofig.INSTANCE.setNaviBgColor(params.getString("naviBgColor"));
            }
            if (params.has("naviTitleColor")) {
                ColorCofig.INSTANCE.setNaviTitleColor(params.getString("naviTitleColor"));
            }
            if (params.has("barItemTextColor")) {
                ColorCofig.INSTANCE.setBarItemTextColor(params.getString("barItemTextColor"));
            }
            if (params.has("toolbarBgColor")) {
                ColorCofig.INSTANCE.setToolbarBgColor(params.getString("toolbarBgColor"));
            }
            if (params.has("toolbarTitleColorNormal")) {
                ColorCofig.INSTANCE.setToolbarTitleColorNormal(params.getString("toolbarTitleColorNormal"));
            }
            if (params.has("toolbarTitleColorDisabled")) {
                ColorCofig.INSTANCE.setToolbarTitleColorDisabled(params.getString("toolbarTitleColorDisabled"));
            }
        }
        if("getPictures".equals(action)){
            ImagePicker imagePicker = ImagePicker.getInstance();
            ImagePicker.getInstance().setImageLoader(new GlideImageLoader());
            int cutType = 2, maximumImagesCount = 1;
            if (params != null) {
                outputType = params.optInt("outputType", 0);
                maximumImagesCount = params.optInt("maximumImagesCount");
                imagePicker.setSelectLimit(maximumImagesCount);
                if (params.has("cutType")) {
                    cutType = params.optInt("cutType", 2);
                }
                imagePicker.setOutPutX(params.optInt("width", 0));
                imagePicker.setOutPutY(params.optInt("height", 0));
                if (cutType == 0) {
                    //圆形单选
                    imagePicker.setCutType(0);
                    imagePicker.setStyle(CropImageView.Style.CIRCLE);
                    imagePicker.setAspectRatio(new AspectRatio(1, 1));
                    imagePicker.setMultiMode(false);
                    imagePicker.setDynamicCrop(false);
                } else if (cutType == 1) {
                    //矩形
                    imagePicker.setCutType(1);
                    imagePicker.setStyle(CropImageView.Style.RECTANGLE);
                    imagePicker.setAspectRatio(new AspectRatio(1, 1));
                    imagePicker.setDynamicCrop(false);
                    if (maximumImagesCount == 1) {
                        imagePicker.setMultiMode(false);
                    }
                } else {
                    imagePicker.setCutType(2);
                    imagePicker.setStyle(CropImageView.Style.RECTANGLE);
                    int cutWidth = params.optInt("cutWidth", 1);
                    int cutHeight = params.optInt("cutHeight", 1);
                    if (cutWidth > 0 && cutHeight > 0) {
                        imagePicker.setAspectRatio(new AspectRatio(cutWidth, cutHeight));
                    }
                    imagePicker.setDynamicCrop(true);
                    imagePicker.setMultiMode(true);
                }
            }
            Intent intent = new Intent(activity, ImageGridActivity.class);
            activity.startActivityForResult(intent, 400);
        }else if("getVideos".equals(action)){
            VideoPicker videoPicker = VideoPicker.getInstance();
            VideoPicker.getInstance().setVideoLoader(new GlideImageLoader());
            int maximumImagesCount = 1;
            maximumImagesCount = params.optInt("maximumImagesCount");
            videoPicker.setSelectLimit(maximumImagesCount);
            Intent intent = new Intent(activity, VideoGridActivity.class);
            activity.startActivityForResult(intent, 0);
        }else if("voiceRecord".equals(action)){
            //new VoiceRecordDialog().show(activity.getFragmentManager(), "costumeBuyDialog")
        }else if("shot".equals(action)){
            Intent intent = new Intent(activity, ShotRecordMainActivity.class);
            activity.startActivityForResult(intent, 0);
        }
    }

}
