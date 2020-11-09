package com.unity3d.player;

import android.content.Context;
import android.util.Log;

import com.aic.libnilu.nlu.MainManager;
import com.aic.libnilu.nlu.ResponseObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Utilities {

    private static String appliance_data = "appliance_test6.txt";

    private static String model_file = "model_tiny_9_5.pt";

    private static String vocab_file_name = "vocab.txt";

    private static String config_file = "config.json";

    private static String vocab_class_file = "vocab1.class";

    private static String vocab_slot_file = "vocab1.tag";

    public static String getAppliance_data() {
        return appliance_data;
    }

    public static String getModel_file() {
        return model_file;
    }

    public static String getVocab_file_name() {
        return vocab_file_name;
    }

    public static String getConfig_file() {
        return config_file;
    }

    public static String getVocab_class_file() {
        return vocab_class_file;
    }

    public static String getVocab_slot_file() {
        return vocab_slot_file;
    }

    public synchronized static String assetFilePath(Context context, String assetName) {
        File file = new File(context.getExternalFilesDir(null), assetName);
        //Timber.d("file:%s", file.getAbsolutePath());
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }
        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        } catch (IOException e) {
            Log.e("ASSET",assetName);
        }
        return null;
    }

    public synchronized static ResponseObject returnResponse(Context contex, String question){

        String appliance_filePath = Utilities.assetFilePath(contex, Utilities.getAppliance_data());
        String file_name = Utilities.assetFilePath(contex, Utilities.getModel_file());
        String vocab_path = Utilities.assetFilePath(contex, Utilities.getVocab_file_name());
        String config_path = Utilities.assetFilePath(contex, Utilities.getConfig_file());
        String vocab_class_path = Utilities.assetFilePath(contex, Utilities.getVocab_class_file());
        String vocab_slot_path = Utilities.assetFilePath(contex, Utilities.getVocab_slot_file());

        ResponseObject a = MainManager.getAnswer(question, appliance_filePath, file_name, vocab_path, config_path, vocab_class_path, vocab_slot_path);


        return a;
    }

}
