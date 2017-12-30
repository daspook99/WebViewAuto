package org.openauto.webviewauto.utils;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class IOHandler {

    private Context context;
    private AssetManager assetMgr;

    public IOHandler(Context context) {
        this.context  = context;
        this.assetMgr = context.getAssets();
    }

    public Object readObject(String fileName) {
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Object obj = is.readObject();
            is.close();
            return obj;
        } catch (Exception e) {
            return null;
        }
    }

    public void saveObject(Object obj, String fileName) {
        try {
            FileOutputStream fos = context.openFileOutput(fileName,Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(obj);
            os.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
