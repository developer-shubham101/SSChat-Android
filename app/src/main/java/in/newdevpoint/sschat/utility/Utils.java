package in.newdevpoint.sschat.utility;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import androidx.annotation.RequiresApi;

import in.newdevpoint.sschat.webService.APIClient;


public class Utils {


/*	public static String getImageString(String imageUrl) {
		return APIClient.IMAGE_URL + imageUrl;
	}*/

	public static boolean isImage(String fileUrl) {
		String lowerCaseFileUrl = fileUrl.toLowerCase();
		return lowerCaseFileUrl.endsWith("jpg") || lowerCaseFileUrl.endsWith("png") || lowerCaseFileUrl.endsWith("gif") || lowerCaseFileUrl.endsWith("PDF") || lowerCaseFileUrl.endsWith("jpeg");

	}

	public static boolean isVideo(String fileUrl) {
		String lowerCaseFileUrl = fileUrl.toLowerCase();
		return lowerCaseFileUrl.endsWith("mov") ||
				fileUrl.endsWith("ogg") ||
				fileUrl.endsWith("MP2") ||
				fileUrl.endsWith("mpeg") ||
				fileUrl.endsWith("mpe") ||
				fileUrl.endsWith("mpv") ||
				fileUrl.endsWith("mp4") ||
				fileUrl.endsWith("wmv") ||
				fileUrl.endsWith("m4p") ||
				fileUrl.endsWith("mpg");
	}

	@RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
	public static Bitmap blurRenderScript(Context context, Bitmap srcBitmap, int radius) {
		Bitmap bitmap = Bitmap.createBitmap(
				srcBitmap.getWidth(), srcBitmap.getHeight(),
				Bitmap.Config.ARGB_8888);

		RenderScript renderScript = RenderScript.create(context);

		Allocation blurInput = Allocation.createFromBitmap(renderScript, srcBitmap);
		Allocation blurOutput = Allocation.createFromBitmap(renderScript, bitmap);

		ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(renderScript,
				Element.U8_4(renderScript));
		blur.setInput(blurInput);
		blur.setRadius(radius); // radius must be 0 < r <= 25
		blur.forEach(blurOutput);

		blurOutput.copyTo(bitmap);
		renderScript.destroy();

		return bitmap;
	}
}
