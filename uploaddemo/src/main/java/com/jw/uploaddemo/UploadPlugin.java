package com.jw.uploaddemo;

import android.content.Context;

/**
 * 创建时间：
 * 更新时间 2017/10/30 0:35
 * 版本：
 * 作者：Mr.jin
 * 描述：得到整个应用上下文对象
 */

public class UploadPlugin {
	private static UploadPlugin instance=new UploadPlugin();
	private static Context context;

	public static UploadPlugin getInstance() {
		return instance;
	}

	public static Context getContext() {
		if (context == null) {
			throw new RuntimeException(
				"请初始化全局环境");
		}
		return context;
	}

	public void init(Context context) {
		this.context = context;
	}

}