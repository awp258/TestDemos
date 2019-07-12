package com.jw.galary.video.trim;

import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 创建时间：2019/7/1010:21
 * 更新时间 2019/7/1010:21
 * 版本：
 * 作者：Mr.jin
 * 描述：
 */
public class FfmpegUtil {
    public static MediaMetadataRetriever retriever = new MediaMetadataRetriever();

    //根据路径得到视频缩略图
    public static String getVideoPhoto(String videoPath,String videoName)  {
        String thumbName = videoName.replace(".mp4",".png");
        retriever.setDataSource(videoPath);
        Bitmap bitmap = retriever.getFrameAtTime();
        File file = new File(videoPath);
        String cropDirectoryPath = file.getParentFile().getAbsolutePath()+"/cover/";
        if(!new File(cropDirectoryPath).exists())
            new File(cropDirectoryPath).mkdirs();
        try {
            saveBitmap(bitmap,cropDirectoryPath,thumbName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cropDirectoryPath+thumbName;
    }

    //获取视频总时长
    public static long getVideoDuration(String path) {
        retriever.setDataSource(path);
        String duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION); //
        return Long.parseLong(duration);
    }

    public static void saveBitmap(Bitmap bitmap,String path, String filename) throws IOException
    {
        File file = new File(path + filename);
        if(file.exists()){
            file.delete();
        }
        FileOutputStream out;
        try{
            out = new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.PNG, 100, out))
            {
                out.flush();
                out.close();
            }
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
