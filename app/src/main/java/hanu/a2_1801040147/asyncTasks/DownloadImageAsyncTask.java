package hanu.a2_1801040147.asyncTasks;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import hanu.a2_1801040147.models.BitmapContainer;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class DownloadImageAsyncTask extends AsyncTask<String, Void, Bitmap> {

    private static final String TAG = "DownloadImageTask";

    private final BitmapContainer bitmapContainer;

    private final DownloadImageTaskCaller caller;

    @Override
    protected Bitmap doInBackground(String... urls) {
        assert urls[0] != null;

        String urldisplay = urls[0];

        try {
            URL url = new URL(urldisplay);
            URLConnection connection = url.openConnection();
            connection.setUseCaches(true);

            InputStream inputStream = connection.getInputStream();
            return BitmapFactory.decodeStream(inputStream);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(Bitmap result) {
        bitmapContainer.setBitmap(result);
        caller.onBitmapDownloaded(result);
    }

    public interface DownloadImageTaskCaller {
        void onBitmapDownloaded(Bitmap bitmap);
    }
}