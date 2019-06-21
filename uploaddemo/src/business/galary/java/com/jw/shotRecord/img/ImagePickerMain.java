package com.jw.shotRecord.img;

import android.app.Activity;
import android.content.Intent;
import com.rxxb.imagepicker.ImagePicker;
import com.rxxb.imagepicker.crop.AspectRatio;
import com.rxxb.imagepicker.loader.GlideImageLoader;
import com.rxxb.imagepicker.ui.ImageGridActivity;
import com.rxxb.imagepicker.view.CropImageView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 彭保生 on 2018/2/28.
 */

public class ImagePickerMain  {
    private int outputType = 0;//输出格式，0表示输出路径，1表示base64字符串
    public void execute(String action, JSONArray args, Activity activity) throws JSONException {
        if("getPictures".equals(action)){
            //JSONObject params = args.getJSONObject(0);
            ImagePicker imagePicker = ImagePicker.getInstance();
            ImagePicker.getInstance().setImageLoader(new GlideImageLoader());
            int cutType = 2, maximumImagesCount = 1;
            JSONObject params=null;
            if (params != null) {
                outputType = params.optInt("outputType", 0);
                maximumImagesCount = params.optInt("maximumImagesCount");
                imagePicker.setSelectLimit(maximumImagesCount);
                if (params.has("cutType")) {
                    cutType = params.optInt("cutType", 2);
                }
                imagePicker.setOutPutX(params.optInt("width", 0));
                imagePicker.setOutPutY(params.optInt("height", 0));

                //颜色相关设置
                if (params.has("oKButtonTitleColorNormal")) {
                    imagePicker.getViewColor().setoKButtonTitleColorNormal(params.getString("oKButtonTitleColorNormal"));
                }
                if (params.has("oKButtonTitleColorDisabled")) {
                    imagePicker.getViewColor().setoKButtonTitleColorDisabled(params.getString("oKButtonTitleColorDisabled"));
                }
                if (params.has("naviBgColor")) {
                    imagePicker.getViewColor().setNaviBgColor(params.getString("naviBgColor"));
                }
                if (params.has("naviTitleColor")) {
                    imagePicker.getViewColor().setNaviTitleColor(params.getString("naviTitleColor"));
                }
                if (params.has("barItemTextColor")) {
                    imagePicker.getViewColor().setBarItemTextColor(params.getString("barItemTextColor"));
                }
                if (params.has("toolbarBgColor")) {
                    imagePicker.getViewColor().setToolbarBgColor(params.getString("toolbarBgColor"));
                }
                if (params.has("toolbarTitleColorNormal")) {
                    imagePicker.getViewColor().setToolbarTitleColorNormal(params.getString("toolbarTitleColorNormal"));
                }
                if (params.has("toolbarTitleColorDisabled")) {
                    imagePicker.getViewColor().setToolbarTitleColorDisabled(params.getString("toolbarTitleColorDisabled"));
                }

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
            activity.startActivityForResult(intent, 100);
        }
    }

}
