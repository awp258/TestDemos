/*
package com.jw.uploaddemo;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.crop.AspectRatio;
import com.jw.galary.img.loader.GlideImageLoader;
import com.jw.galary.img.ui.ImageGridActivity;
import com.jw.galary.img.view.CropImageView;
import com.jw.galary.video.VideoGridActivity;
import com.jw.galary.video.VideoPicker;
import com.jw.shotRecord.ShotRecordMainActivity;
import com.jw.uploaddemo.base.utils.ThemeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

*/
/**
 * Created by 彭保生 on 2018/2/28.
 *//*


public class UploadPluginMain extends CordovaPlugin {
    private CallbackContext mCallbackContext;
    private int outputType = 0;//输出格式，0表示输出路径，1表示base64字符串
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.mCallbackContext = callbackContext;
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
            startActivityForResult(new Intent(cordova.getActivity(), ImageGridActivity.class), 400);
        }else if("getVideos".equals(action)){
            VideoPicker videoPicker = VideoPicker.getInstance();
            VideoPicker.getInstance().setVideoLoader(new GlideImageLoader());
            int maximumImagesCount = 1;
            maximumImagesCount = params.optInt("maximumImagesCount");
            videoPicker.setSelectLimit(maximumImagesCount);
            startActivityForResult(new Intent(cordova.getActivity(), VideoGridActivity.class), 0);
        }else if("voiceRecord".equals(action)){
            int hasPermission = ThemeUtils.checkPermission(
                    cordova.getActivity(), Manifest.permission.RECORD_AUDIO
            );
            //如果开启
            if (hasPermission == PackageManager.PERMISSION_GRANTED) {
                VoiceRecordDialog().show(cordova.getActivity().getSupportFragmentManager(), "costumeBuyDialog")
            } else {
                //弹出请求框请求用户开启
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ThemeUtils.requestPermission(cordova.getActivity(),
                            Manifest.permission.RECORD_AUDIO
                            , 200
                    );
                }
            }
        }else if("shot".equals(action)){
            int hasPermission = ThemeUtils.checkPermission(
                    cordova.getActivity(), Manifest.permission.CAMERA
            );
            int hasPermission2 = ThemeUtils.checkPermission(
                    cordova.getActivity(), Manifest.permission.RECORD_AUDIO
            );
            if (hasPermission != PackageManager.PERMISSION_GRANTED || hasPermission2 != PackageManager.PERMISSION_GRANTED) {
                ArrayList stringArrays =new  ArrayList<String>();
                stringArrays.add(Manifest.permission.CAMERA);
                stringArrays.add(Manifest.permission.RECORD_AUDIO);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    ThemeUtils.requestPermissions(
                            cordova.getActivity(), stringArrays, 300
                    );
                }
            } else
                startActivityForResult(new Intent(cordova.getActivity(), ShotRecordMainActivity.class), 0);
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private void checkPermission() {
        //检查需要系统同意的请求是否开启
        int hasPermission = ThemeUtils.checkPermission(
                cordova.getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE
        );
        int hasPermission2 = ThemeUtils.checkPermission(
                cordova.getActivity(), Manifest.permission.READ_PHONE_STATE
        );
        if (hasPermission != PackageManager.PERMISSION_GRANTED || hasPermission2 != PackageManager.PERMISSION_GRANTED) {
            ArrayList stringArrays = new ArrayList<String>();
            stringArrays.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            stringArrays.add(Manifest.permission.READ_PHONE_STATE);
            ThemeUtils.requestPermissions(
                    cordova.getActivity(), stringArrays, 100
            );
        }
    }

*/
/*    @Override
    protected void onActivityResult(int requestCode, int resultCode, @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(data.getExtras()!=null){
            switch (resultCode){
                case ImagePicker.RESULT_CODE_IMAGE_ITEMS:
                    ArrayList<ImageItem> list = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                    if(list.size()==0)
                        return;
                    correctImageFactory(list);
                    break;
                case VideoPicker.RESULT_CODE_VIDEO_ITEMS:
                    ArrayList<VideoItem> list2 = (ArrayList<VideoItem>) data.getSerializableExtra(EXTRA_VIDEO_ITEMS);
                    Intent intent = new Intent(this,ProgressActivity.class);
                    intent.putExtra("path", list2.get(0).path);
                    intent.putExtra("name", list2.get(0).name);
                    intent.putExtra("type", UploadConfig.TYPE_UPLOAD_VIDEO);
                    intent.putParcelableArrayListExtra("videos", list2);
                    start(intent);
                    break;
            }
        }
    }

    private void correctImageFactory(ArrayList<ImageItem> images) {
        new Thread(() -> {
            for (ImageItem image : images) {
                boolean saved = false;
                String destPath = ImagePicker.createFile(
                        ImagePicker.getInstance().getCropCacheFolder(cordova.getActivity()), "IMG_" + System.currentTimeMillis(), ".png"
                ).getAbsolutePath();
                if (ImagePicker.getInstance().isOrigin() || ImagePicker.getInstance().getOutPutX() == 0 || ImagePicker.getInstance().getOutPutY() == 0) {
                    //原图按图片原始尺寸压缩, size小于150kb的不压缩
                    if (isNeedCompress(150, image.path)) {
                        saved = BitmapUtil.saveBitmap2File(BitmapUtil.compress(image.path), destPath);
                    }
                } else {
                    //按给定的宽高压缩
                    saved = BitmapUtil.saveBitmap2File(
                            BitmapUtil.getScaledBitmap(
                                    image.path,
                                    ImagePicker.getInstance().getOutPutX(),
                                    ImagePicker.getInstance().getOutPutY()
                            ), destPath
                    );
                }
                if(saved)
                    image.path = destPath;
                else
                    image.path = image.path;
                image.name =
                        image.path.split("cropTemp/")[1];
            }
            Intent intent = new Intent(cordova.getActivity(), ProgressActivity.class);
            intent.putExtra("imageList", images);
            intent.putExtra("type", UploadConfig.TYPE_UPLOAD_IMG);
            start(intent);
        }).start();
    }

    private boolean isNeedCompress(int leastCompressSize, String path) {
        if (leastCompressSize > 0) {
            File source = new File(path);
            if (!source.exists()) {
                return false;
            }

            if (source.length() <= (leastCompressSize << 10)) {
                return false;
            }
        }
        return true;
    }*//*


}
*/
