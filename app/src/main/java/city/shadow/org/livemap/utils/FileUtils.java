package city.shadow.org.livemap.utils;

import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import static android.content.res.AssetManager.ACCESS_STREAMING;

public class FileUtils {

    public static final String TAG = "FileUtils";

    public static String readFile(Context context, String filePath) {


        final StringBuilder stringBuilder = new StringBuilder();

        try {
            InputStream inputStream = context.getResources().getAssets()
                    .open(filePath, ACCESS_STREAMING);

            final BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );

            String s = null;

            while ((s = bufferedReader.readLine()) != null) {
                stringBuilder.append(s);
            }

            inputStream.close();

            bufferedReader.close();

        } catch (FileNotFoundException e) {
            Log.e(TAG, "FileNotFoundException", e);
            return null;
        } catch (IOException e) {
            Log.e(TAG, "IOException", e);
            return null;
        }

        return stringBuilder.toString();
    }

}
