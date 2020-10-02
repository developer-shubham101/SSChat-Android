package in.newdevpoint.sschat.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import in.newdevpoint.sschat.R;
import in.newdevpoint.sschat.adapter.UsersListAdapter;
import in.newdevpoint.sschat.model.FSChatModel;
import in.newdevpoint.sschat.model.FSUsersModel;
import in.newdevpoint.sschat.utility.UserDetails;


public class UsersActivity extends AppCompatActivity {
	private static final String TAG = "UsersActivity:";
	//    private static final String TAG = "UsersActivity";
	private static boolean BACK_PRESSED = false;
	private FirebaseUser currentUser;
	private RecyclerView recyclerView;
	private TextView noUsersText;
	//    -----------------------
//	private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
//	private DatabaseReference mUserRef = mRootRef.child("users");
	private UsersListAdapter adapter;
	//	private ArrayList<UsersListModel> usersList = new ArrayList<>();


	private FirebaseAuth.AuthStateListener authListener;
	private FirebaseAuth auth;
	private FirebaseFirestore db = FirebaseFirestore.getInstance();


	private FSUsersModel myDetail;
	private HashMap<String, FSUsersModel> alterNativeUserList = new HashMap<>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user);


		recyclerView = findViewById(R.id.usersList);
		noUsersText = findViewById(R.id.noUsersText);


//		FloatingActionButton openGroupChat = findViewById(R.id.openGroupChat);

//		openGroupChat.setOnClickListener(v -> startActivity(new Intent(UsersActivity.this, GroupChatActivity.class)));


		auth = FirebaseAuth.getInstance();
		currentUser = auth.getCurrentUser();

		authListener = new FirebaseAuth.AuthStateListener() {
			@Override
			public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
				FirebaseUser user = firebaseAuth.getCurrentUser();
				if (user == null) {
//					String newToken = preferenceUtils.getData("newToken", UsersActivity.this);
					auth = FirebaseAuth.getInstance();
					HashMap<String, Object> update = new HashMap<>();
//					update.put("FCMToken", newToken);
					update.put("isOnline", true);
//					mRootRef.child("users").child(currentUser.getUid()).updateChildren(update);

					// user auth state is changed - user is null
					// launch openSignupPage activity
//					startActivity(new Intent(UsersActivity.this, StartupActivity.class));
//					finish();
				}
			}
		};

		initRecycler();

	}


	private void initRecycler() {

		adapter = new UsersListAdapter(new UsersListAdapter.CallBackForSinglePost() {
			@Override
			public void onClick(int position) {

			}

			@Override
			public void onClick(FSChatModel item) {

				HashMap<String, FSUsersModel> chatUsersMap = new HashMap<>();
				chatUsersMap.put(currentUser.getUid(), myDetail);
				chatUsersMap.put(item.getSenderUserDetail().getId(), item.getSenderUserDetail());

				UserDetails.roomId = item.getRoomId();
				UserDetails.chatUsers = chatUsersMap;
				UserDetails.myDetail = myDetail;
				startActivity(new Intent(UsersActivity.this, ChatActivity.class));
			}

		});


		recyclerView.setHasFixedSize(true);
		LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
		recyclerView.setLayoutManager(mLayoutManager);
		recyclerView.setAdapter(adapter);


	}

//    @Override
//    protected void onStop() {
//        super.onStop();
//        mUserRef.child(currentUser.getUid()).child("isOnline").setValue(false);
//        if (authListener != null) {
//            auth.removeAuthStateListener(authListener);
//        }
//    }

	@Override
	protected void onDestroy() {
		super.onDestroy();
//		mUserRef.child(currentUser.getUid()).child("isOnline").setValue(false);
		if (authListener != null) {
			auth.removeAuthStateListener(authListener);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		auth.addAuthStateListener(authListener);
//		mUserRef.child(currentUser.getUid()).child("isOnline").setValue(true);
//		Query query = mUserRef.orderByChild("profileDisplayName");
//		query.addValueEventListener(new ValueEventListener() {
//			@Override
//			public void onDataChange(DataSnapshot dataSnapshot) {
//				ArrayList<UsersListModel> tmpUsersList = new ArrayList<>();
//				for (DataSnapshot ds : dataSnapshot.getChildren()) {
//					if (ds.getKey().equals(currentUser.getUid())) continue;
//					tmpUsersList.add(new UsersListModel(ds));
//				}
//
//				if (tmpUsersList.isEmpty()) {
//					noUsersText.setVisibility(View.VISIBLE);
//					recyclerView.setVisibility(View.GONE);
//				} else {
//					noUsersText.setVisibility(View.GONE);
//					recyclerView.setVisibility(View.VISIBLE);
//					adapter.updateNewList(tmpUsersList);
//				}
//
//
//			}
//
//			@Override
//			public void onCancelled(DatabaseError databaseError) {
//
//			}
//		});


//		db.collection("threads").whereEqualTo("users." + currentUser.getUid(), true).addSnapshotListener(new EventListener<QuerySnapshot>() {
//			@Override
//			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//
//				if (e != null) {
//					Log.w(TAG, "Listen failed.", e);
//					return;
//				}
//
//				for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
//					switch (dc.getType()) {
//						case ADDED:
//							Log.d(TAG, "New city: " + dc.getDocument().getData());
//							break;
//						case MODIFIED:
//							Log.d(TAG, "Modified city: " + dc.getDocument().getData());
//							break;
//						case REMOVED:
//							Log.d(TAG, "Removed city: " + dc.getDocument().getData());
//							break;
//					}
//				}
//
//			}
//		});


		ArrayList<Map<String, Object>> chatRoomList = new ArrayList<>();
		ArrayList<String> alternateuserIdList = new ArrayList<>();

		db.collection("threads").whereEqualTo("users." + currentUser.getUid(), true).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
			@Override
			public void onComplete(@NonNull Task<QuerySnapshot> task) {
				if (task.isSuccessful()) {
					QuerySnapshot document = task.getResult();
					for (DocumentChange dc : document.getDocumentChanges()) {
						Log.d(TAG, "New city: " + dc.getDocument().getData());

						Map<String, Object> threadData = dc.getDocument().getData();
						threadData.put("docId", dc.getDocument().getId());
						chatRoomList.add(threadData);

						Map<String, Boolean> usersListMap = (Map<String, Boolean>) threadData.get("users");
						Log.d(TAG, "usersListMap: " + usersListMap);

						for (String key : usersListMap.keySet()) {
							if (!key.equals(currentUser.getUid())) {
								alternateuserIdList.add(key);
							}
						}

					}
					alternateuserIdList.add(currentUser.getUid());

					db.collection("users").whereIn("userID", alternateuserIdList).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
						@Override
						public void onComplete(@NonNull Task<QuerySnapshot> task) {
							if (task.isSuccessful()) {
								QuerySnapshot document = task.getResult();
								for (DocumentChange dc : document.getDocumentChanges()) {
									Log.d(TAG, "User Data: " + dc.getDocument().getData());

									Map<String, Object> usersData = dc.getDocument().getData();

									if (usersData.get("userID").equals(currentUser.getUid())) {
										myDetail = new FSUsersModel(usersData);
									} else {
										FSUsersModel otherUserObj = new FSUsersModel(usersData);
										alterNativeUserList.put(otherUserObj.getId(), otherUserObj);
									}

								}

								ArrayList<FSChatModel> chatList = new ArrayList<>();
								for (Map<String, Object> element : chatRoomList) {

									Map<String, Boolean> usersListMap = (Map<String, Boolean>) element.get("users");
									String otherUserId = "";
									for (String key : usersListMap.keySet()) {
										if (!key.equals(currentUser.getUid())) {
											otherUserId = key;
											break;
										}
									}


									chatList.add(new FSChatModel(element, alterNativeUserList.get(otherUserId)));
								}

								adapter.addAll(chatList);
//								Iterator it = chatRoomList.iterator();
//
//								while (it.hasNext()) {
//									Map.Entry pair = (Map.Entry)it.next();
//									System.out.println(pair.getKey() + " = " + pair.getValue());
//									it.remove(); // avoids a ConcurrentModificationException
//								}
							} else {
								Log.d(TAG, "get failed with ", task.getException());
							}
						}
					});

				} else {
					Log.d(TAG, "get failed with ", task.getException());
				}
			}
		});

		db.collection("threads").whereEqualTo("users." + currentUser.getUid(), true).addSnapshotListener(new EventListener<QuerySnapshot>() {
			@Override
			public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
				if (e != null) {
					Log.w(TAG, "Listen failed.", e);
					return;
				}


				if (queryDocumentSnapshots != null && !queryDocumentSnapshots.isEmpty()) {
					for (DocumentChange dc : queryDocumentSnapshots.getDocumentChanges()) {
						if (dc.getType() == DocumentChange.Type.ADDED) {

						} else if (dc.getType() == DocumentChange.Type.MODIFIED) {
							Log.d(TAG, "New city: " + dc.getDocument().getData());

							Map<String, Object> modifyData = dc.getDocument().getData();

							for (FSChatModel element : adapter.getAllList()) {
								if (element.getRoomId().equals(dc.getDocument().getId())) {
									try {
										HashMap<String, Object> chatUserData = (HashMap<String, Object>) modifyData.get(currentUser.getUid());
										if (chatUserData != null) {
											long newMessage = (long) chatUserData.get("newMessage");
											element.setNewMessage(newMessage);
										}
									} catch (ClassCastException ignored) {
										ignored.printStackTrace();
									}
									element.setLastMessage((String) modifyData.get("lastMessage"));
									break;
								}
							}

							adapter.notifyDataSetChanged();
						} else if (dc.getType() == DocumentChange.Type.REMOVED) {

						}
					}
					alternateuserIdList.add(currentUser.getUid());

				} else {
					Log.d(TAG, "Current data: null");
				}
			}
		});

	}
}