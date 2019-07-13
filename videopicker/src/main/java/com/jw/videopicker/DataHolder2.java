

package com.jw.videopicker;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DataHolder2 {
    public static final String DH_CURRENT_IMAGE_FOLDER_ITEMS = "dh_current_image_folder_items";
    private static DataHolder2 mInstance;
    private Map<String, List<VideoItem>> data = new HashMap();

    public static DataHolder2 getInstance() {
        if (mInstance == null) {
            Class var0 = DataHolder2.class;
            synchronized(DataHolder2.class) {
                if (mInstance == null) {
                    mInstance = new DataHolder2();
                }
            }
        }

        return mInstance;
    }

    private DataHolder2() {
    }

    public void save(String id, List<VideoItem> object) {
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
