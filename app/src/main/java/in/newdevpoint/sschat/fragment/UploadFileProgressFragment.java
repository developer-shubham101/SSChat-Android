package in.newdevpoint.sschat.fragment;


import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.Date;
import java.util.HashMap;

import in.newdevpoint.sschat.R;
import in.newdevpoint.sschat.model.ChatModel;
import in.newdevpoint.sschat.model.UploadFileMode;
import in.newdevpoint.sschat.databinding.FragmentUploadFileBinding;
import in.newdevpoint.sschat.webService.FileUploader;
import in.newdevpoint.sschat.webService.ResponseModel;


public class UploadFileProgressFragment extends Fragment {

	private static final String TAG = "UploadFileProgressFrag:";

	public String fragmentTag;
	private FragmentUploadFileBinding binding;
	private String placeId;
	private LinearLayout hotelList, foodList, addAdviseList;
	private UploadFileProgressCallback uploadFileProgressCallback;
	private File file;
	private Date date;
	private ChatModel.MessageType messageType;
	private HashMap<String, Object> messageMeta;

	public UploadFileProgressFragment() {
		Log.d(TAG, "UploadFileProgressFragment: ");

		fragmentTag = "Upload" + new Date().getTime();

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {

		binding = DataBindingUtil.inflate(inflater, R.layout.fragment_upload_file, container, false);
		View view = binding.getRoot();
		//here data must be an instance of the class MarsDataProvider
//		binding.setMarsdata(data);

		binding.progressPercentage.setText("10%");

		binding.progressCancel.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				uploadFileProgressCallback.uploadFinished(fragmentTag, null, messageType, date, messageMeta);
			}
		});


		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
			binding.progressBar.setProgress(10, true);
		} else {
			binding.progressBar.setProgress(10);
		}

		binding.progressFileName.setText(file.getName());


		return view;
	}

	public void uploadFiles(File file, File thumb, ChatModel.MessageType messageType, HashMap<String, Object> messageMeta, UploadFileProgressCallback uploadFileProgressCallback) {
		this.uploadFileProgressCallback = uploadFileProgressCallback;
		this.file = file;
		this.messageType = messageType;
		this.date = new Date();
		this.messageMeta = messageMeta;

//		showProgress("Uploading media ...");
		FileUploader fileUploader = new FileUploader();
		fileUploader.uploadFiles( "file", file, thumb, new FileUploader.FileUploaderCallback() {
			@Override
			public void onError() {
//				hideProgress();
				uploadFileProgressCallback.uploadFinished(fragmentTag, null, messageType, date, messageMeta);
			}

			@Override
			public void onFinish(String data) {
//				hideProgress();

				//					Log.e("RESPONSE " + i, responses[i]);

				Gson gson = new Gson();
				Type type = new TypeToken<ResponseModel<UploadFileMode>>() {
				}.getType();

				Log.d("Response : ", data);
				ResponseModel<UploadFileMode> obj = gson.fromJson(data, type);
				uploadFileProgressCallback.uploadFinished(fragmentTag, obj.getData(), messageType, date, messageMeta);

			}

			@Override
			public void onProgressUpdate(int currentpercent, int totalpercent) {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
					binding.progressBar.setProgress(currentpercent, true);
				} else {
					binding.progressBar.setProgress(currentpercent);
				}

				binding.progressPercentage.setText(currentpercent + "%");
//				updateProgress(totalpercent, "Uploading file " + filenumber, "");
				Log.e("Progress Status", currentpercent + " " + totalpercent);
			}
		}, "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsImp0aSI6IjhmYTBiOTcxMTBmM2EzZWQ0ZjI0ZWU1NjQxMmZkMWUwNTJiNmI1ZDM3NmU5MmNiNDgyZGVjMjNkNjk0NDI2NzdjNDFlYzI1Nzc4NjY3MTAyIn0.eyJhdWQiOiIxIiwianRpIjoiOGZhMGI5NzExMGYzYTNlZDRmMjRlZTU2NDEyZmQxZTA1MmI2YjVkMzc2ZTkyY2I0ODJkZWMyM2Q2OTQ0MjY3N2M0MWVjMjU3Nzg2NjcxMDIiLCJpYXQiOjE1ODk4ODg0MTAsIm5iZiI6MTU4OTg4ODQxMCwiZXhwIjoxNjIxNDI0NDA5LCJzdWIiOiIyMCIsInNjb3BlcyI6W119.di40nRlxEKWrJrGqeHb5oOzkAJAqwOokFI1IRdfjRw1usvhM3Q5O2Wop3QtYryjqA5A3NryxYTdl6D1x58zM7CITDeT7wzChw7RYaIm9Nl8Jf1m3AQ6lgJTGp4ZoZ9sUEPQMdPQK163c9rrLYjuxreHglEUr59iS-0dKsIhqak7GzUqX6I-q8IhVuZG1TZ1JQpWds1x2bGUVwHWiylJkq61pp6t7Zbn6-35aCpizDbWjfBY-1KbEldLtlnEmZzHYJZkNskpzMTwZQRKlZNaOdZSDtkm88Awx5vhIQo3ha_YQCr7EklgJUiSyUh2APQ1yy40lIMkEEoA02aajDXhXMpKfTVRQFVAaPpCbwCC4pZVDaHUi0-FhEENW1FE83g6qTK9EV1cjN3UJej8Dm494rkMNI4BNKwyzmew5j5j49BU4VqAi9_JiHNB02K2jhakM_NUn3OAbFRbKCrjbeB1qWffd4j1FoR1HNClNikYd-bsV-Nspe2TTYWaV1wWvX787LwBfa0zqm5b1dNjqCmKlbN8HrfFamS68VhiiqU-NCTgf4StqASQAgVVzJqzxRAersoGpJ8QiRiu8T6umkJkt8mD1OWu6MvJYtBj7MooDmaO5GgbXW9US1GgPiiCE8zNr1-rBVL4-HPgS10g_7WnJt78Yc4GsziqHQGx2SO4x0t4");


	}

	public interface UploadFileProgressCallback {
		void uploadFinished(String fragmentTag, @Nullable UploadFileMode data, ChatModel.MessageType messageType, Date date, HashMap<String, Object> messageMeta);
	}

}
