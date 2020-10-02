package in.newdevpoint.sschat.utility;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class ImagePickerClass {
	private static int RESULT_LOAD_IMAGE = 786;
	private ImagePickerMethod imagePickerMethod;
	private Activity activity;

	public ImagePickerClass(ImagePickerMethod imagePickerMethod, Activity activity) {
		this.imagePickerMethod = imagePickerMethod;
		this.activity = activity;
	}

	public void openPicker() {
		Intent i = new Intent(
				Intent.ACTION_PICK,
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

		activity.startActivityForResult(i, RESULT_LOAD_IMAGE);
	}

	public void onActivityResult(int requestCode, int resultCode, Intent data) {


		if (requestCode == RESULT_LOAD_IMAGE && resultCode == Activity.RESULT_OK && null != data) {
			Uri selectedImage = data.getData();
			String[] filePathColumn = {MediaStore.Images.Media.DATA};

			Cursor cursor = activity.getContentResolver().query(selectedImage,
					filePathColumn, null, null, null);
			cursor.moveToFirst();

			int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
			String picturePath = cursor.getString(columnIndex);
			cursor.close();
			imagePickerMethod.fileUrl(picturePath, selectedImage);
		}
	}


	public interface ImagePickerMethod {
		void fileUrl(String url, Uri uri);

	}
}