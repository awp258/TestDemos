/*
package com.jw.uploaddemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.Toast;
import com.jw.galary.VoiceRecordDialog2;
import com.jw.galary.img.ImagePicker;
import com.jw.galary.img.bean.ImageItem;
import com.jw.galary.img.crop.AspectRatio;
import com.jw.galary.img.loader.GlideImageLoader;
import com.jw.galary.img.ui.ImageGridActivity;
import com.jw.galary.img.util.BitmapUtil;
import com.jw.galary.img.view.CropImageView;
import com.jw.galary.video.VideoGridActivity;
import com.jw.galary.video.VideoItem;
import com.jw.galary.video.VideoPicker;
import com.jw.shotRecord.ShotRecordMainActivity;
import com.jw.uploaddemo.activity.ProgressActivity;
import com.jw.uploaddemo.base.utils.ThemeUtils;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.jw.galary.video.VideoPicker.EXTRA_VIDEO_ITEMS;

*/
/**
 * This class echoes a string called from JavaScript.
 *//*

public class UploadPluginMain extends CordovaPlugin {
    private CallbackContext mCallbackContext;
    private int outputType = 0;//输出格式，0表示输出路径，1表示base64字符串

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        this.mCallbackContext = callbackContext;
        JSONObject params = args.getJSONObject(0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();
        }
        initUserConfig(params);
        switch (params.getInt("type")){
            case 1:
                voiceRecord(params);
                break;
            case 3:
                getPictures(params);
                Intent intent = new Intent(cordova.getActivity(), ImageGridActivity.class);
                cordova.startActivityForResult(this, intent, 400);
                break;
            case 4:
                getVideos(params);
                break;
            case 2:
                UploadConfig.INSTANCE.setSHOT_TYPE(4);
                shot(params);
                break;
            case 5:
                UploadConfig.INSTANCE.setSHOT_TYPE(5);
                shot(params);
                break;
            case 6:
                UploadConfig.INSTANCE.setSHOT_TYPE(6);
                shot(params);
                break;
        }
        return true;
    }

    private void initUserConfig(JSONObject params) throws JSONException {
        if (params != null) {
            UploadConfig.INSTANCE.setBASE_HTTP(params.getString("serverUrl"));
            UploadConfig.INSTANCE.setAppid(String.valueOf(params.getLong("appid")));
            UploadConfig.INSTANCE.setRegion(params.getString("region"));
            UploadConfig.INSTANCE.setTicket(params.getLong("ticket"));
            UploadConfig.INSTANCE.setOrgId(params.getLong("orgId"));
        }
    }

    private void getPictures(JSONObject params) {
        ImagePicker imagePicker = ImagePicker.getInstance();
        ImagePicker.getInstance().setImageLoader(new GlideImageLoader());
        int cutType = 2, maximumImagesCount = 1;
        if (params != null) {
            outputType = params.optInt("outputType", 0);
            maximumImagesCount = params.optInt("maximumImagesCount",9);
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
    }

    private void getVideos(JSONObject params) {
        VideoPicker videoPicker = VideoPicker.getInstance();
        VideoPicker.getInstance().setVideoLoader(new GlideImageLoader());
        int maximumVideosCount = params.optInt("maximumVideosCount",1);
        UploadConfig.INSTANCE.setVIDEO_RECORD_LENGTH(params.optInt("duration",60)*1000);
        videoPicker.setSelectLimit(maximumVideosCount);
        Intent intent = new Intent(cordova.getActivity(), VideoGridActivity.class);
        cordova.startActivityForResult(this, intent, 0);
    }

    private void voiceRecord(JSONObject params) {
        int hasPermission = ThemeUtils.checkPermission(
                cordova.getActivity(), Manifest.permission.RECORD_AUDIO
        );
        //如果开启
        if (hasPermission == PackageManager.PERMISSION_GRANTED) {
            UploadConfig.INSTANCE.setVOICE_RECORD_LENGTH(params.optInt("duration",60)*1000);
            VoiceRecordDialog2 dialog = new VoiceRecordDialog2();
            dialog.show(cordova.getActivity().getFragmentManager(), "costumeBuyDialog");
        } else {
            //弹出请求框请求用户开启
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ThemeUtils.requestPermission(cordova.getActivity(),
                        Manifest.permission.RECORD_AUDIO
                        , 200
                );
            }
        }
    }

    private void shot(JSONObject params) {
        int hasPermission = ThemeUtils.checkPermission(
                cordova.getActivity(), Manifest.permission.CAMERA
        );
        int hasPermission2 = ThemeUtils.checkPermission(
                cordova.getActivity(), Manifest.permission.RECORD_AUDIO
        );
        if (hasPermission != PackageManager.PERMISSION_GRANTED || hasPermission2 != PackageManager.PERMISSION_GRANTED) {
            ArrayList stringArrays = new ArrayList<String>();
            stringArrays.add(Manifest.permission.CAMERA);
            stringArrays.add(Manifest.permission.RECORD_AUDIO);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                ThemeUtils.requestPermissions(
                        cordova.getActivity(), stringArrays, 300
                );
            }
        } else {
            getPictures(params);
            UploadConfig.INSTANCE.setVIDEO_RECORD_LENGTH(params.optInt("duration",60)*1000);
            Intent intent = new Intent(cordova.getActivity(), ShotRecordMainActivity.class);
            cordova.startActivityForResult(this, intent, 0);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data!=null&&data.getExtras() != null) {
            switch (resultCode) {
                case ImagePicker.RESULT_CODE_IMAGE_ITEMS:
                    ArrayList<ImageItem> list = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                    if (list.size() == 0)
                        return;
                    correctImageFactory(list);
                    break;
                case VideoPicker.RESULT_CODE_VIDEO_ITEMS:
                    ArrayList<VideoItem> list2 = (ArrayList<VideoItem>) data.getSerializableExtra(EXTRA_VIDEO_ITEMS);
                    Intent intent = new Intent(cordova.getActivity(), ProgressActivity.class);
                    intent.putExtra("path", list2.get(0).path);
                    intent.putExtra("name", list2.get(0).name);
                    intent.putExtra("type", UploadConfig.TYPE_UPLOAD_VIDEO);
                    intent.putParcelableArrayListExtra("videos", list2);
                    cordova.startActivityForResult(this, intent, 0);
                    break;
                case UploadConfig.RESULT_UPLOAD_SUCCESS:
                    //this.mCallbackContext.success(data.getStringExtra("result"));
                    Log.v("bbbbb",data.getStringExtra("result"));
                    break;
            }
        }
    }

    @Override
    public void onRequestPermissionResult(int requestCode, String[] permissions, int[] grantResults) throws JSONException {
        super.onRequestPermissionResult(requestCode, permissions, grantResults);
        Log.v("aaaaaaaaaa",permissions.toString());
        switch (requestCode){
            case 100:
                for (String permission : permissions) {
                    if (permission.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(cordova.getActivity(), "存储卡读写全蝎没有开启,应用无法运行", Toast.LENGTH_SHORT).show();
                        System.exit(0);
                    } else if (permission.equals(Manifest.permission.READ_PHONE_STATE) && grantResults[1] == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(cordova.getActivity(), "读取系统状态权限没有开启,将失去部分功能", Toast.LENGTH_SHORT).show();
                }
                break;
            case 200:
                if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    Toast.makeText(cordova.getActivity(), "录音权限没有开启,无法录音", Toast.LENGTH_SHORT).show();
                } else {
                    //VoiceRecordDialog().show(supportFragmentManager, "costumeBuyDialog")
                    new VoiceRecordDialog2().show(cordova.getActivity().getFragmentManager(), "costumeBuyDialog");
                }
                break;
            case 300:
                for (String permission : permissions) {
                    if (permission.equals(Manifest.permission.CAMERA) && grantResults[0] == PackageManager.PERMISSION_DENIED) {
                        Toast.makeText(cordova.getActivity(), "相机权限没有开启,无法录屏", Toast.LENGTH_SHORT).show();
                    } else if (permission.equals(Manifest.permission.RECORD_AUDIO) && grantResults[1] == PackageManager.PERMISSION_DENIED)
                        Toast.makeText(cordova.getActivity(), "录音权限没有开启,无法录音", Toast.LENGTH_SHORT).show();
                }
                if (grantResults[0] == PackageManager.PERMISSION_DENIED && grantResults[1] == PackageManager.PERMISSION_DENIED)
                    cordova.startActivityForResult(this,new Intent(cordova.getActivity(), ShotRecordMainActivity.class), 0);
                break;
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
                if (saved)
                    image.path = destPath;
                else
                    image.path = image.path;
                image.name =
                        image.path.split("cropTemp/")[1];
            }
            Intent intent = new Intent(cordova.getActivity(), ProgressActivity.class);
            intent.putExtra("imageList", images);
            intent.putExtra("type", UploadConfig.TYPE_UPLOAD_IMG);
            cordova.startActivityForResult(this, intent, 0);
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
    }
}
*/
