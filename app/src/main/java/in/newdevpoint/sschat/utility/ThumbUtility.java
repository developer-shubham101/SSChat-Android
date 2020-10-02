package in.newdevpoint.sschat.utility;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

public class ThumbUtility implements Runnable {

    private static final String TAG = "ThumbUtilityy:";
    private ThumbUtilityCallBack callback;
    private Context context;
    private String image;

    public ThumbUtility(Context context, String image, ThumbUtilityCallBack callback) {
        this.callback = callback;
        this.context = context;
        this.image = image;
    }

    private static String saveToInternalStorage(Context appContext, Bitmap bitmapImage, String fileUrl) {
        ContextWrapper cw = new ContextWrapper(appContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, MD5.stringToMD5(fileUrl));

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(Context appContext, String fileUrl) throws FileNotFoundException {
        ContextWrapper cw = new ContextWrapper(appContext);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
        File f = new File(directory, MD5.stringToMD5(fileUrl));
        return BitmapFactory.decodeStream(new FileInputStream(f));

    }

    private static Bitmap retriveVideoFrameFromVideo(String videoPath) throws Throwable {
        Bitmap bitmap = null;
        MediaMetadataRetriever mediaMetadataRetriever = null;
        try {
            mediaMetadataRetriever = new MediaMetadataRetriever();
            mediaMetadataRetriever.setDataSource(videoPath, new HashMap<>());
            String time = mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            Log.d(TAG, "retriveVideoFrameFromVideo: " + time);
            //   mediaMetadataRetriever.setDataSource(videoPath);
            bitmap = mediaMetadataRetriever.getFrameAtTime();
        } catch (Exception e) {
            e.printStackTrace();
            throw new Throwable("Exception in retriveVideoFrameFromVideo(String videoPath)" + e.getMessage());

        } finally {
            if (mediaMetadataRetriever != null) {
                mediaMetadataRetriever.release();
            }
        }
        return bitmap;
    }

    private synchronized static Bitmap getThumb(Context context, String fileUrl) {
        //        if (oldImage == null) {
//            oldImage = getBitmapNullImage(context, fileUrl, oldImage);
//        }
//        Log.d(TAG, "getThumb: " + fileUrl);
//        Log.d(TAG, "getThumb: " + oldImage.getHeight());
        return getBitmapNullImage(context, fileUrl);
    }

    private static Bitmap getBitmapNullImage(Context context, String fileUrl) {
        Bitmap oldImage = null;
        try {
            Bitmap image = retriveVideoFrameFromVideo(fileUrl);
            if (image != null) {
                String path = saveToInternalStorage(context, image, fileUrl);
                Log.d(TAG, "saved Path => " + path);
            }
            oldImage = image;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return oldImage;
    }

    @Override
    public void run() {
        callback.thumbDownloaded(getBitmapNullImage(context, image));
    }


    public interface ThumbUtilityCallBack {
        void thumbDownloaded(Bitmap thumb);
    }


}
