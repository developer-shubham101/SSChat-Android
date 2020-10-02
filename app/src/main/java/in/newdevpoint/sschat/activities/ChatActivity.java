package in.newdevpoint.sschat.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.CancellationSignal;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Size;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.downloader.OnDownloadListener;
import com.downloader.request.DownloadRequest;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.commons.io.FilenameUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import in.newdevpoint.sschat.R;
import in.newdevpoint.sschat.adapter.ChatAdapter;
import in.newdevpoint.sschat.adapter.HeaderDataImpl;
import in.newdevpoint.sschat.dialog.ContactDialog;
import in.newdevpoint.sschat.fragment.PlayAudioFragment;
import in.newdevpoint.sschat.fragment.UploadFileProgressFragment;
import in.newdevpoint.sschat.model.ChatModel;
import in.newdevpoint.sschat.model.ContactModel;
import in.newdevpoint.sschat.model.FSUsersModel;
import in.newdevpoint.sschat.model.MediaMetaModel;
import in.newdevpoint.sschat.model.MediaModel;
import in.newdevpoint.sschat.model.UploadFileMode;
import in.newdevpoint.sschat.stickyheader.stickyView.StickHeaderItemDecoration;
import in.newdevpoint.sschat.utility.DownloadUtility;
import in.newdevpoint.sschat.utility.FileOpenUtility;
import in.newdevpoint.sschat.utility.MD5;
import in.newdevpoint.sschat.utility.PermissionClass;
import in.newdevpoint.sschat.utility.UserDetails;
import in.newdevpoint.sschat.utility.Utils;
import in.newdevpoint.sschat.databinding.ActivityChatBinding;
import in.newdevpoint.sschat.webService.APIClient;


public class ChatActivity extends AppCompatActivity implements View.OnClickListener,
		PermissionClass.PermissionRequire,
		UploadFileProgressFragment.UploadFileProgressCallback,
		PlayAudioFragment.PlayAudioCallback {
	private static final String TAG = "ChatActivity";
	private static final int REQUEST_READ_STORAGE_FOR_UPLOAD_IMAGE = 3;
	private static final int REQUEST_READ_STORAGE_FOR_UPLOAD_VIDEO = 4;
	private static final int REQUEST_RECORD_AUDIO_PERMISSION = 5;
	private static final int REQUEST_ADD_CONTACT = 8;
	private static final String VIDEO_DIRECTORY = "/demonuts";
	private static final int REQUEST_SELECT_CONTACTS = 9;
	public static RequestOptions userProfileDefaultOptions = new RequestOptions().placeholder(R.drawable.user_profile_image).error(R.drawable.user_profile_image);
	private static int CHAT_BUNCH_COUNT = 30;
	private static int GALLERY = 1, CAMERA = 2;
	private static String fileName = null;
	public boolean mStartRecording = true;
	private boolean isMute = false;
	private String displayName = "";
	private FirebaseFirestore db = FirebaseFirestore.getInstance();

	private FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
	private CollectionReference chatReference;


	private FSUsersModel otherUser;
	///Document for pagination
	private int currentBunch = CHAT_BUNCH_COUNT;
	private ArrayList<ChatModel> chatListTmp = new ArrayList<>();
	private HashMap<Date, ArrayList<ChatModel>> chatList = new HashMap<>();
	private ActivityChatBinding binding;
	private PermissionClass permissionClass;
	private MediaRecorder recorder = null;
	private ChatAdapter adapter;
	private PlayAudioFragment uploadFragment = new PlayAudioFragment();
	private long startTime, timeBuff, updateTime = 0L; //millisecondTime,
	private Handler handler;
	private int seconds, minutes, hours;
	public Runnable runnable = new Runnable() {

		public void run() {

			seconds = (int) (timeBuff + System.currentTimeMillis() / 1000 - startTime);

			hours = seconds / 3600;
			minutes = seconds / 60;

			seconds = seconds % 60;

			binding.chatRecordingTime.setText("" + hours + ":" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds));

			handler.postDelayed(this, 0);
		}

	};
	private boolean isLoadingOldMessage = false;
	private DocumentReference threadDocReference;
	private boolean applyBlur = false;
	private boolean isSetWallpaper = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_chat);

		// Record to the external cache directory for visibility
		fileName = getExternalCacheDir().getAbsolutePath();
		fileName += "/audiorecordtest.m4a";

		handler = new Handler();

		binding = DataBindingUtil.setContentView(ChatActivity.this, R.layout.activity_chat);
		permissionClass = new PermissionClass(this, this);
		binding.attachmentWrapper.setVisibility(View.GONE);

		this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


		binding.sendButton.setOnClickListener(this);
		binding.chatAttachment.setOnClickListener(this);
		binding.attachmentDoc.setOnClickListener(this);
		binding.attachmentCamera.setOnClickListener(this);
		binding.attachmentGallery.setOnClickListener(this);
		binding.attachmentLocation.setOnClickListener(this);
		binding.attachmentContact.setOnClickListener(this);
		binding.chatStartRecording.setOnClickListener(this);
		binding.sendAudioButton.setOnClickListener(this);
		binding.openRecorder.setOnClickListener(this);
		binding.chatCloseRecording.setOnClickListener(this);

		adapter = setUpRecyclerView();

		binding.audioPlayerFragment.setVisibility(View.GONE);


		setAudioPlayer();

		setUpFirebaseCollections();

		initChatMessageListener();


		addChatMenu();


		String wallpaperFilePath = DownloadUtility.createPath(getApplicationContext(), DownloadUtility.FILE_PATH_WALLPAPER) + "/wallpaper.jpg";
		File wallpaperFile = new File(wallpaperFilePath);
		if (wallpaperFile.exists()) {
			Bitmap myBitmap = BitmapFactory.decodeFile(wallpaperFilePath);
			binding.chatBgImage.setImageBitmap(myBitmap);
		}
	}

	void addChatMenu() {
		binding.chatMenu.setOnClickListener(v -> {
			PopupMenu popup = new PopupMenu(ChatActivity.this, binding.chatMenu);
			//inflating menu from xml resource
			popup.inflate(R.menu.chat_menu);
			if (isMute) {
				popup.getMenu().getItem(0).setTitle("Un-mute");
			} else {
				popup.getMenu().getItem(0).setTitle("Mute");
			}
			//adding click listener
			popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
				@Override
				public boolean onMenuItemClick(MenuItem item) {
					switch (item.getItemId()) {
						case R.id.chatMute:
							//IF is single user chat then check some other user details and set it
							if (!UserDetails.isGroup) {
								isMute = !isMute;
								HashMap<String, Object> newValue = new HashMap<>();
								newValue.put(currentUser.getUid() + ".isMute", isMute);
								threadDocReference.update(newValue);
							}
							break;
						case R.id.changeWallpaper:


							PopupMenu popup = new PopupMenu(ChatActivity.this, binding.chatMenu);
							//inflating menu from xml resource
							popup.inflate(R.menu.change_chat_wallpaper_menu);

							//adding click listener
							popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
								@Override
								public boolean onMenuItemClick(MenuItem item) {
									DisplayMetrics displayMetrics = new DisplayMetrics();
									getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
									int height = displayMetrics.heightPixels;
									int width = displayMetrics.widthPixels;

									switch (item.getItemId()) {
										case R.id.changeWallpaperDefault:
											String wallpaperFilePath = DownloadUtility.createPath(getApplicationContext(), DownloadUtility.FILE_PATH_WALLPAPER) + "/wallpaper.jpg";
											File wallpaperFile = new File(wallpaperFilePath);
											if (wallpaperFile.exists()) {
												wallpaperFile.delete();
												binding.chatBgImage.setImageResource(R.drawable.bg_chat);
											}
											break;
										case R.id.changeWallpaperWithOutBlur:
											applyBlur = false;
											isSetWallpaper = true;
											CropImage.activity().setFixAspectRatio(true).setAspectRatio(width, height).start(ChatActivity.this);
											break;
										case R.id.changeWallpaperWithBlur:
											applyBlur = true;
											isSetWallpaper = true;
											CropImage.activity().setFixAspectRatio(true).setAspectRatio(width, height).start(ChatActivity.this);
											break;
									}

									return false;
								}
							});
							//displaying the popup
							popup.show();

							break;
					}

					return false;
				}
			});
			//displaying the popup
			popup.show();
		});
	}

	private void setAudioPlayer() {
		uploadFragment.setPlayAudioCallback(this);
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		ft.add(R.id.audioPlayerFragment, uploadFragment);
		ft.commit();
	}

	private void initChatMessageListener() {
		///IF is single user chat then check some other user details and set it
		if (!UserDetails.isGroup) {
			///if it is single user chat then fetch oher user's details


//			Collection<FSUsersModel> otherUsers = Collections2.filter(new ArrayList<>(UserDetails.chatUsers.values()), input -> !input.getId().equals(currentUser.getUid()));

			ArrayList<FSUsersModel> allOtherUsers = new ArrayList<>(UserDetails.chatUsers.values());

			for (FSUsersModel element : allOtherUsers) {
				if (!element.getId().equals(currentUser.getUid())) {
					otherUser = element;
					break;
				}
			}
			if (otherUser != null) {
				threadDocReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
					@Override
					public void onComplete(@NonNull Task<DocumentSnapshot> task) {

						if (task.isSuccessful()) {

							DocumentSnapshot documentSnapshot = task.getResult();

							if (documentSnapshot != null) {
								Map<String, Object> data = documentSnapshot.getData();
								if (data != null) {
									Map<String, Object> otherUserData = (Map<String, Object>) data.get(otherUser.getId());


									if (otherUserData != null) {
										Object isMuteObj = otherUserData.get("isMute");
										isMute = (isMuteObj instanceof Boolean) && (boolean) isMuteObj;
										if (isMute) {
											binding.attachmentWrapper.setVisibility(View.GONE);
											binding.blockedWrapper.setVisibility(View.VISIBLE);
											binding.chatRecordingWrapper.setVisibility(View.GONE);
											binding.chatMessageWrapper.setVisibility(View.GONE);
										} else {
											binding.attachmentWrapper.setVisibility(View.GONE);
											binding.blockedWrapper.setVisibility(View.GONE);
											binding.chatRecordingWrapper.setVisibility(View.GONE);
											binding.chatMessageWrapper.setVisibility(View.VISIBLE);
										}
									}


								}
							}


						} else {
							Log.d(TAG, "get failed with ", task.getException());
						}
					}
				});


				binding.chatChatWithUserName.setText(otherUser.getName());


				Glide.with(this).setDefaultRequestOptions(userProfileDefaultOptions).load(otherUser.getProfile_image()).into(binding.chatUserProfile);

				db.collection("users/").document(otherUser.getId()).addSnapshotListener(new EventListener<DocumentSnapshot>() {
					@Override
					public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
						if (e != null) {
							Log.w(TAG, "Listen failed.", e);
							return;
						}

						if (documentSnapshot != null) {
							Map<String, Object> data = documentSnapshot.getData();

							if (data != null) {
								boolean onlineStatus = (boolean) data.get("online");

								Object timeObject = data.get("last_active");

								if (onlineStatus) {
									binding.chatChatWithUserStatus.setText("Online");
								} else if (timeObject instanceof Timestamp) {
									Timestamp time = (Timestamp) timeObject;
									Date tempDate = new Date(time.getSeconds() * 1000);
									String timestamp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(tempDate);
									binding.chatChatWithUserStatus.setText(timestamp);
								} else {
									binding.chatChatWithUserStatus.setText("Offline");
								}
							}

						}
					}
				});
			}

			chatReference.orderBy("time", Query.Direction.DESCENDING).limit(currentBunch).addSnapshotListener(new EventListener<QuerySnapshot>() {
				@Override
				public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
					if (e != null) {
						Log.w(TAG, "Listen failed.", e);
						return;
					}

					if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
						for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
							switch (dc.getType()) {
								case ADDED:
									Log.d(TAG, "New city: " + dc.getDocument().getData());
									appendMessage(dc);
									binding.chatRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
									break;
								case MODIFIED:
									Log.d(TAG, "Modified city: " + dc.getDocument().getData());
									break;
								case REMOVED:
									Log.d(TAG, "Removed city: " + dc.getDocument().getData());
									break;
							}
						}

					} else {
						Log.d(TAG, "Current data: null");
					}
				}
			});

		}
	}


	private void appendMessage(DocumentChange dc) {
		Map<String, Object> chatData = dc.getDocument().getData();
		FSUsersModel senderDetails = UserDetails.chatUsers.get(chatData.get("sender_id"));

		ChatModel chatModel = new ChatModel(dc.getDocument().getId(), chatData, senderDetails);

		chatListTmp.add(chatModel);

		Date tempDate = chatModel.getCreatedDate();
		ArrayList<ChatModel> correspondingChatList = chatList.get(tempDate);


		if (correspondingChatList == null) {
			correspondingChatList = new ArrayList<>();
			chatList.put(tempDate, correspondingChatList);
		}
		correspondingChatList.add(chatModel);


		Collections.sort(correspondingChatList, (o1, o2) -> o1.getMessageDate().compareTo(o2.getMessageDate()));

		ArrayList<Date> keys = new ArrayList<>(chatList.keySet());

		Collections.sort(keys);


		adapter.clearAll();
		for (Date key : keys) {
			ArrayList<ChatModel> chatForThatDay = chatList.get(key);
//										HeaderDataImpl headerData1 = new HeaderDataImpl(R.layout.header1_item_recycler, key);
			HeaderDataImpl headerData1 = new HeaderDataImpl(R.layout.header1_item_recycler, key);
			adapter.setHeaderAndData(chatForThatDay, headerData1);
		}

	}

	private void setUpFirebaseCollections() {
		chatReference = db.collection("threads").document(UserDetails.roomId).collection("messages");
		threadDocReference = db.collection("threads").document(UserDetails.roomId);
	}

	private ChatAdapter setUpRecyclerView() {

		/*binding.chatRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
			int currentScrollPosition = 0;

			@Override
			public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
				super.onScrollStateChanged(recyclerView, newState);
			}

			@Override
			public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
//				super.onScrolled(recyclerView, dx, dy);

				currentScrollPosition += dy;
				Log.d(TAG, "currentScrollPosition: " + currentScrollPosition);
				if (currentScrollPosition <= 0 && !isLoadingOldMessage) {
					Log.d(TAG, "// We're at the top: ");

					isLoadingOldMessage = true;
					currentBunch = CHAT_BUNCH_COUNT + currentBunch;
					chatReference.orderBy("time", Query.Direction.DESCENDING).limit(currentBunch).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
						@Override
						public void onComplete(@NonNull Task<QuerySnapshot> task) {
							isLoadingOldMessage = false;
							if (task.isSuccessful()) {
								Log.d(TAG, "Next bunch called");
								QuerySnapshot document = task.getResult();
								chatList.clear();
								for (DocumentChange dc : document.getDocumentChanges()) {
									Log.d(TAG, "New city: " + dc.getDocument().getData());

									appendMessage(dc);
									binding.chatRecyclerView.smoothScrollToPosition(adapter.getItemCount() - 1);
								}
							} else {
								Log.d(TAG, "get failed with ", task.getException());
							}
						}
					});
				}
			}
		});*/


		ChatAdapter adapter = new ChatAdapter(this, new ChatAdapter.ChatCallbacks() {
			@Override
			public void onClickDownload(ChatModel chatModel, MediaModel messageContent, boolean stopDownloading, OnDownloadListener onDownloadListener) {
				downloadFile(chatModel, messageContent, stopDownloading, onDownloadListener);
			}

			@Override
			public void onClickContact(ChatModel chatModel, ContactModel contactModel) {
				ContactDialog.DialogBuilder dialogBuilder = new ContactDialog.DialogBuilder(ChatActivity.this).setOnItemClick(new ContactDialog.OnItemClick() {
					@Override
					public void addContactNew(ContactDialog dialog) {
					/*	Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
						contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

						contactIntent
								.putExtra(ContactsContract.Intents.Insert.NAME, contactModel.getFirst_name() + " " + contactModel.getLast_name())
								.putExtra(ContactsContract.Intents.Insert.PHONE, contactModel.getMobile());

						startActivityForResult(contactIntent, REQUEST_ADD_CONTACT);*/

						Intent contactIntent = new Intent(Intent.ACTION_INSERT_OR_EDIT);
						contactIntent.setType(ContactsContract.Contacts.CONTENT_ITEM_TYPE);

						contactIntent
								.putExtra(ContactsContract.Intents.Insert.NAME, contactModel.getFirst_name() + " " + contactModel.getLast_name())
								.putExtra(ContactsContract.Intents.Insert.PHONE, contactModel.getMobile());

						startActivityForResult(contactIntent, REQUEST_ADD_CONTACT);

					}

					@Override
					public void close(ContactDialog dialog) {
						dialog.cancel();
					}
				}).setContactNumber(contactModel.getMobile()).setTitle(contactModel.getFirst_name() + " " + contactModel.getLast_name());

				ContactDialog dialog = dialogBuilder.build();
				Display display = getWindowManager().getDefaultDisplay();
				Point size = new Point();
				display.getSize(size);
				int width = (int) (size.x * 0.90);
//        int height = size.y;
				dialog.show();
				dialog.getWindow().setLayout(width, WindowManager.LayoutParams.WRAP_CONTENT);
			}

			@Override
			public void onLongClick(ChatModel chatModel) {
				Toast.makeText(ChatActivity.this, "On long click ", Toast.LENGTH_SHORT).show();
			}
		});
		LinearLayoutManager layoutManager = new LinearLayoutManager(this);

//		setData(adapter);

		binding.chatRecyclerView.setAdapter(adapter);
		binding.chatRecyclerView.setLayoutManager(layoutManager);
		binding.chatRecyclerView.addItemDecoration(new StickHeaderItemDecoration(adapter));
		return adapter;
	}

	private void downloadFile(ChatModel chatModel, MediaModel appListModel, boolean stopDownloading, OnDownloadListener onDownloadListener) {
		File downloadDir = getExternalFilesDir(null);

		String downloadUrl = appListModel.getFile_url();
		try {
			URL url = new URL(downloadUrl);
			String downloadFileName = FilenameUtils.getName(url.getPath());


			File downloadFile = new File(DownloadUtility.getPath(getApplicationContext(), DownloadUtility.FILE_PATH_CHAT_FILES) + "/" + downloadFileName);
			boolean isAppDownloaded = downloadFile.exists();


			if (isAppDownloaded) {
				chatModel.setDownloadStatus(ChatModel.DownloadStatus.DOWNLOADED);
				String filePath = downloadFile.getAbsolutePath();
				if (FileOpenUtility.isVideo(filePath)) {
					Intent intent = new Intent(this, PlayerActivity.class);
					intent.putExtra(PlayerActivity.INTENT_EXTRA_FILE_PATH, filePath);

					startActivity(intent);
				} else if (FileOpenUtility.isAudio(filePath)) {

					binding.audioPlayerFragment.setVisibility(View.VISIBLE);
					uploadFragment.play(filePath);
				} else {
					FileOpenUtility.openFile(ChatActivity.this, filePath);
				}

			} else {
				DownloadRequest task = DownloadUtility.downloadList.get(MD5.stringToMD5(downloadUrl));

				if (task == null) {

					DownloadUtility.downloadFile(getApplicationContext(),
							DownloadUtility.getPath(
									getApplicationContext(),
									DownloadUtility.FILE_PATH_CHAT_FILES),
							downloadUrl, downloadFileName,
							MD5.stringToMD5(downloadUrl),
							onDownloadListener);
					chatModel.setDownloadStatus(ChatModel.DownloadStatus.DOWNLOADING);


				} else {
					if (stopDownloading) {
						task.cancel();
						DownloadUtility.downloadList.remove(MD5.stringToMD5(downloadUrl));
						chatModel.setDownloadStatus(ChatModel.DownloadStatus.PENDING);

					}
				}
			}
			adapter.notifyDataSetChanged();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
//        mUserRef.child(currentUser.getUid()).child("isOnline").setValue(true);
//        mUserRef.child(currentUser.getUid()).addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(com.google.firebase.database.DataSnapshot dataSnapshot) {
//                displayName = dataSnapshot.child("profileDisplayName").getValue(String.class);
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//
//            }
//        });
	}

	@Override
	protected void onStop() {
		super.onStop();
//        mUserRef.child(currentUser.getUid()).child("isOnline").setValue(false);

	}

	private void showPicVideoDialog() {
		AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
		pictureDialog.setTitle("Select Action");
		String[] pictureDialogItems = {
				"Select video from gallery",
				"Record video from camera"};
		pictureDialog.setItems(pictureDialogItems,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
							case 0:
								chooseVideoFromGallery();
								break;
							case 1:
								takeVideoFromCamera();
								break;
						}
					}
				});
		pictureDialog.show();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.sendButton: {
				if (!binding.messageArea.getText().toString().isEmpty()) {
					sendMessage(binding.messageArea.getText().toString(), ChatModel.MessageType.text, new Date(), new HashMap<>());
					binding.messageArea.setText("");
				}
				break;
			}
			case R.id.openRecorder: {
				binding.chatMessageWrapper.setVisibility(View.GONE);
				binding.chatRecordingWrapper.setVisibility(View.VISIBLE);
				break;
			}
			case R.id.chatAttachment: {
				toggleAttachmentWrapper();
				break;
			}
			case R.id.chatCloseRecording: {
				stopRecording();
				mStartRecording = true;
				binding.chatMessageWrapper.setVisibility(View.VISIBLE);
				binding.chatRecordingWrapper.setVisibility(View.GONE);
				break;
			}
			case R.id.attachmentGallery: {
				toggleAttachmentWrapper();
				binding.attachmentWrapper.setVisibility(View.GONE);
				permissionClass.askPermission(REQUEST_READ_STORAGE_FOR_UPLOAD_VIDEO);
				break;
			}
			case R.id.attachmentCamera: {
				toggleAttachmentWrapper();
				binding.attachmentWrapper.setVisibility(View.GONE);
				permissionClass.askPermission(REQUEST_READ_STORAGE_FOR_UPLOAD_IMAGE);
				break;
			}
			case R.id.chatStartRecording: {
				permissionClass.askPermission(REQUEST_RECORD_AUDIO_PERMISSION);
				break;
			}
			case R.id.sendAudioButton: {
				sendRecording();
				break;
			}
			case R.id.attachmentContact: {
				toggleAttachmentWrapper();
				getContacts();
				break;
			}
		}
	}

	private void toggleAttachmentWrapper() {
		binding.attachmentWrapper.setVisibility(binding.attachmentWrapper.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
	}

	private void getContacts() {
		Intent intent = new Intent(this, ContactListActivity.class);

		startActivityForResult(intent, REQUEST_SELECT_CONTACTS);
	}

	private void sendRecording() {
		File audio = new File(fileName);
		HashMap<String, Object> fileMeta = new HashMap<>();
		fileMeta.put(MediaMetaModel.KEY_FILE_TYPE, MediaMetaModel.MediaType.audioM4A.toString());
		fileMeta.put(MediaMetaModel.KEY_FILE_NAME, audio.getName());

		if (audio.exists()) {
			addFragment(audio, null, ChatModel.MessageType.document, fileMeta);
		}
	}

	private void sendMessage(String message, ChatModel.MessageType messageType, Date time, HashMap<String, Object> messageContent) {
		HashMap<String, Object> messageMap = new HashMap<>();
		messageMap.put("message", message);
		messageMap.put("sender_id", UserDetails.myDetail.getId());
		messageMap.put("message_type", messageType.toString());
		messageMap.put("message_content", messageContent);
		messageMap.put("time", time);

		chatReference.add(messageMap);
	}

	private void chooseVideoFromGallery() {
		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
				MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(galleryIntent, GALLERY);
	}

	private void takeVideoFromCamera() {
		Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
		startActivityForResult(intent, CAMERA);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
			CropImage.ActivityResult result = CropImage.getActivityResult(data);
			if (resultCode == RESULT_OK) {
				Uri resultUri;
				if (result != null) {
					resultUri = result.getUri();
					if (resultUri != null) {
						if (isSetWallpaper) {
							setWallpaper(resultUri);
						} else {
							uploadImageFormUri(resultUri);
						}
					}
				}
			} else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
				Exception error;
				if (result != null) {
					error = result.getError();
					error.printStackTrace();
				}

			}
		} else if (requestCode == GALLERY) {
			if (data != null) {
				Uri contentURI = data.getData();
				if (contentURI != null) {
					uploadVideoFormUri(contentURI);
				}
			}
		} else if (requestCode == CAMERA) {
			if (data != null) {
				Uri contentURI = data.getData();
				if (contentURI != null) {
					uploadVideoFormUri(contentURI);
				}
			}
		} else if (requestCode == REQUEST_SELECT_CONTACTS && resultCode == RESULT_OK) {
			if (data != null) {
				Bundle extras = data.getExtras();
				if (extras != null) {
					String contacts = extras.getString(ContactListActivity.INTENT_SELECTED_CONTACTS);

					Gson gson = new Gson();
					Type type = new TypeToken<ArrayList<ContactModel>>() {
					}.getType();

					Log.d("ContactList: ", contacts);
					ArrayList<ContactModel> contactList = gson.fromJson(contacts, type);

					for (ContactModel element : contactList) {
						sendMessage("", ChatModel.MessageType.contact, new Date(), element.getList());
					}


					System.out.println(contactList);

				}
			}
		}
	}

	private void uploadImageFormUri(Uri resultUri) {
		String imagePath = resultUri.getPath();

		try {
			File thumbnailFile = new File(getCacheDir(), "image.jpg");
			thumbnailFile.createNewFile();
			thumbnailFile.exists();
			Bitmap bitmap;
			if (android.os.Build.VERSION.SDK_INT <= 29) {
				bitmap = ThumbnailUtils.createImageThumbnail(imagePath,
						MediaStore.Images.Thumbnails.MINI_KIND);
			} else {
				// TODO: 4/17/2020 here we will do code for crete thumnail for latest api version 29 bcoz createVideoThumbnail is depricate for this version
				CancellationSignal signal = new CancellationSignal();
				Size size = new Size(100, 100);
				File file = new File(imagePath);
				bitmap = ThumbnailUtils.createImageThumbnail(file,
						size, signal);
			}
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
			byte[] bitmapData = bos.toByteArray();

			//write the bytes in file
			FileOutputStream fos = null;
			try {
				fos = new FileOutputStream(thumbnailFile);
				File file = new File(imagePath);
				if (file.exists()) {

					HashMap<String, Object> fileMeta = new HashMap<>();
					fileMeta.put(MediaMetaModel.KEY_FILE_TYPE, MediaMetaModel.MediaType.imageJPG.toString());
					addFragment(file, null, ChatModel.MessageType.image, fileMeta);

					Log.d(TAG, "onActivityResult: " + file);
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			try {
				if (fos != null) {
					fos.write(bitmapData);
					fos.flush();
					fos.close();
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void setWallpaper(Uri resultUri) {
		String imagePath = resultUri.getPath();
		try {
			File file = new File(imagePath);
			if (file.exists()) {
				String wallpaperFilePath = DownloadUtility.createPath(getApplicationContext(), DownloadUtility.FILE_PATH_WALLPAPER) + "/wallpaper.jpg";
				File wallpaperFile = new File(wallpaperFilePath);

				if (wallpaperFile.exists()) {
					wallpaperFile.delete();
				}

				Bitmap srcBitmap = BitmapFactory.decodeFile(imagePath);

				if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
					if (applyBlur) {
						Bitmap blurred = Utils.blurRenderScript(ChatActivity.this, srcBitmap, 25);

						try (FileOutputStream out = new FileOutputStream(wallpaperFilePath)) {
							blurred.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
							// PNG is a lossless format, the compression factor (100) is ignored
						} catch (IOException e) {
							e.printStackTrace();
							DownloadUtility.copy(file, wallpaperFile);
						}
					} else {
						DownloadUtility.copy(file, wallpaperFile);
					}
				} else {
					DownloadUtility.copy(file, wallpaperFile);
				}


				if (wallpaperFile.exists()) {
					Bitmap myBitmap = BitmapFactory.decodeFile(wallpaperFilePath);
					binding.chatBgImage.setImageBitmap(myBitmap);
				}
				Log.d(TAG, "onActivityResult: " + file);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void uploadVideoFormUri(Uri contentURI) {
		String selectedVideoPath = getPath(contentURI);
		Log.d("path", selectedVideoPath);
		saveVideoToInternalStorage(selectedVideoPath);

		try {
			File thumbnailFile = new File(getCacheDir(), "image.jpg");
			thumbnailFile.createNewFile();
//			thumbnailFile.exists();
			Log.d(TAG, "onActivityResult: " + thumbnailFile.exists());
			//Convert bitmap to byte array
			try {
				Bitmap bitmap;
				if (android.os.Build.VERSION.SDK_INT <= 29) {
					bitmap = ThumbnailUtils.createVideoThumbnail(selectedVideoPath,
							MediaStore.Images.Thumbnails.MINI_KIND);
				} else {
					// TODO: 4/17/2020 here we will do code for crete thumbnail for latest api version 29 because createVideoThumbnail is deprecated for this version
					CancellationSignal signal = new CancellationSignal();
					Size size = new Size(100, 100);
					File file = new File(selectedVideoPath);
					bitmap = ThumbnailUtils.createVideoThumbnail(file,
							size, signal);
				}
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				if (bitmap != null) {
					bitmap.compress(Bitmap.CompressFormat.PNG, 100 /*ignored for PNG*/, bos);
				}
				byte[] bitMapData = bos.toByteArray();

				//write the bytes in file
				FileOutputStream fos = null;
				try {
					fos = new FileOutputStream(thumbnailFile);
					File file = new File(selectedVideoPath);
					if (file.exists()) {

						HashMap<String, Object> fileMeta = new HashMap<>();
						fileMeta.put(MediaMetaModel.KEY_FILE_TYPE, MediaMetaModel.MediaType.videoMP4.toString());
						addFragment(file, thumbnailFile, ChatModel.MessageType.video, fileMeta);

						Log.d(TAG, "onActivityResult: " + file);
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				try {
					if (fos != null) {
						fos.write(bitMapData);
						fos.flush();
						fos.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();


			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void addFragment(File file, File thumb, ChatModel.MessageType messageType, HashMap<String, Object> messageMeta) {
		FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
		UploadFileProgressFragment uploadFragment = new UploadFileProgressFragment();
		ft.add(R.id.uploadFileWrapper, uploadFragment, uploadFragment.fragmentTag);
		ft.commit();


		uploadFragment.uploadFiles(file, thumb, messageType, messageMeta, this);
	}

	private void saveVideoToInternalStorage(String filePath) {
		File newFile;
		try {
			File currentFile = new File(filePath);
			File wallpaperDirectory = new File(Environment.getExternalStorageDirectory() + VIDEO_DIRECTORY);
			newFile = new File(wallpaperDirectory, Calendar.getInstance().getTimeInMillis() + ".mp4");

			if (!wallpaperDirectory.exists()) {
				wallpaperDirectory.mkdirs();
			}
			if (currentFile.exists()) {
				InputStream in = new FileInputStream(currentFile);
				OutputStream out = new FileOutputStream(newFile);

				// Copy the bits from instream to outstream
				byte[] buf = new byte[1024];
				int len;

				while ((len = in.read(buf)) > 0) {
					out.write(buf, 0, len);
				}
				in.close();
				out.close();
				Log.v("vii", "Video file saved successfully.");
			} else {
				Log.v("vii", "Video saving failed. Source file missing.");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String getPath(Uri uri) {
		String[] projection = {MediaStore.Video.Media.DATA};
		Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
		if (cursor != null) {
			// HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
			// THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
			int column_index = cursor
					.getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
			cursor.moveToFirst();
			return cursor.getString(column_index);
		} else
			return null;
	}

	@Override
	public void permissionDeny() {

	}

	private void selectCameraImage() {
//		CropImage.activity().start(this, AddPostBottomSheetFragment.this);
		isSetWallpaper = false;
		CropImage.activity().start(this);

//		Intent galleryIntent = new Intent(Intent.ACTION_PICK,
//				MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//
//		galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
//		startActivityForResult(galleryIntent, GALLERY);
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		permissionClass.onRequestPermissionsResult(requestCode, permissions, grantResults);
	}

	private void onRecord(boolean start) {
		if (start) {
			startRecording();
		} else {
			stopRecording();
		}
	}

	private void stopRecording() {
		if (recorder != null) {
			recorder.stop();
			recorder.release();
			recorder = null;
		}

		resetTimer();
	}

	private void startRecording() {
		recorder = new MediaRecorder();
		recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
		recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		recorder.setOutputFile(fileName);
		recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

		try {
			recorder.prepare();

			startTime = System.currentTimeMillis() / 1000;
			startTimer();
		} catch (IOException e) {
			Log.e(TAG, "prepare() failed");
		}

		recorder.start();
	}

	private void startTimer() {
		handler.postDelayed(runnable, 1000);
	}

	private void resetTimer() {
//        millisecondTime = 0L;
		startTime = 0L;
		timeBuff = 0L;
		updateTime = 0L;
		seconds = 0;
		minutes = 0;

		handler.removeCallbacks(runnable);

		binding.chatRecordingTime.setText("00:00:00");
	}

	@Override
	public void permissionGranted(int flag) {
		switch (flag) {
			case REQUEST_READ_STORAGE_FOR_UPLOAD_IMAGE:
				selectCameraImage();
				break;

			case REQUEST_READ_STORAGE_FOR_UPLOAD_VIDEO:
				showPicVideoDialog();
				break;
			case REQUEST_RECORD_AUDIO_PERMISSION:

				onRecord(mStartRecording);
				if (mStartRecording) {
					binding.chatStartRecording.setText("Stop recording");
				} else {
					binding.chatStartRecording.setText("Start recording");
				}
				mStartRecording = !mStartRecording;
				break;
		}
	}

	@Override
	public String[] listOfPermission(int flag) {
		if (flag == REQUEST_RECORD_AUDIO_PERMISSION) {
			return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.RECORD_AUDIO};
		} else if (flag == REQUEST_READ_STORAGE_FOR_UPLOAD_IMAGE || flag == REQUEST_READ_STORAGE_FOR_UPLOAD_VIDEO) {
			return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
					Manifest.permission.WRITE_EXTERNAL_STORAGE,
					Manifest.permission.CAMERA
			};
		}
		return new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
				Manifest.permission.WRITE_EXTERNAL_STORAGE,
				Manifest.permission.CAMERA,
				Manifest.permission.RECORD_AUDIO};
	}


	@Override
	public void uploadFinished(String fragmentTag, UploadFileMode data, ChatModel.MessageType messageType, Date date, HashMap<String, Object> messageData) {

		Fragment fragment = getSupportFragmentManager().findFragmentByTag(fragmentTag);
		if (fragment != null) {
			getSupportFragmentManager().beginTransaction().remove(fragment).commit();
			if (data != null) {

				if (data.getThumbnail() != null && !data.getThumbnail().isEmpty()) {
					messageData.put(MediaMetaModel.KEY_FILE_THUMB,/* APIClient.IMAGE_URL +*/ data.getThumbnail());
				}
				HashMap<String, Object> messageContent = new HashMap<>();

				String fileUrl = /*APIClient.IMAGE_URL + */data.getFile();
				messageContent.put("file_url", fileUrl);
				messageContent.put("file_meta", messageData);


				sendMessage("", messageType, date, messageContent);

			}
		}


	}

	@Override
	public void closeAudioPlayer() {
		binding.audioPlayerFragment.setVisibility(View.GONE);
	}
}