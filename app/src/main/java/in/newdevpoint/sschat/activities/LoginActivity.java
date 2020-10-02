package in.newdevpoint.sschat.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import in.newdevpoint.sschat.R;
import in.newdevpoint.sschat.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {
	private static final String EMAIL = "email";
	private ActivityLoginBinding loginBinding;

	private FirebaseAuth auth = FirebaseAuth.getInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

//        loginBinding.loginFbBtn.setReadPermissions(Arrays.asList(EMAIL));


//        loginBinding.loginFbBtn.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                Toast.makeText(LoginActivity.this, "login successful", Toast.LENGTH_SHORT).show();
//                loginResult.getAccessToken().getUserId();
////                AccessToken accessToken = AccessToken.getCurrentAccessToken();
////                boolean isLoggedIn = accessToken != null && !accessToken.isExpired();
//            }
//
//            @Override
//            public void onCancel() {
//
//            }
//
//            @Override
//            public void onError(FacebookException error) {
//
//            }
//        });


//		loginBinding.signup.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
//				startActivity(intent);
//			}
//		});
		Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
		startActivity(intent);
		loginBinding.loginBtn.setOnClickListener(v -> {

			//authenticate user
			auth.signInWithEmailAndPassword(loginBinding.loginUserEmail.getText().toString(), "123456")
					.addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
						@Override
						public void onComplete(@NonNull Task<AuthResult> task) {
							Intent intent = new Intent(LoginActivity.this, UsersActivity.class);
							startActivity(intent);


						}
					});


		});

	}


}
