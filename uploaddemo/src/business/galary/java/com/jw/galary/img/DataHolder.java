

package com.jw.galary.img;

import com.jw.galary.img.bean.ImageItem;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHolder {
    public static final String DH_CURRENT_IMAGE_FOLDER_ITEMS = "dh_current_image_folder_items";
    private static DataHolder mInstance;
    private Map<String, List<ImageItem>> data = new HashMap();

    public static DataHolder getInstance() {
        if (mInstance == null) {
            Class var0 = DataHolder.class;
            synchronized(DataHolder.class) {
                if (mInstance == null) {
                    mInstance = new DataHolder();
                }
            }
        }

        return mInstance;
    }

    private DataHolder() {
    }

    public void save(String id, List<ImageItem> object) {
        if (this.data != null) {
            this.data.put(id, object);
        }

    }

    public Object retrieve(String id) {
        if (this.data != null && mInstance != null) {
            return this.data.get(id);
        } else {
            throw new RuntimeException("你必须先初始化");
        }
    }
}
