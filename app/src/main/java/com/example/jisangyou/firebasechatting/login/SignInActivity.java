package com.example.jisangyou.firebasechatting.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

import com.example.jisangyou.firebasechatting.R;
import com.example.jisangyou.firebasechatting.activity.RoomListActivity;
import com.example.jisangyou.firebasechatting.verificationUtil.DialogUtil;
import com.example.jisangyou.firebasechatting.verificationUtil.PreferenceUtil;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignInActivity extends AppCompatActivity {

    FirebaseAuth auth;
    FirebaseDatabase database;
    DatabaseReference userRef;
    private EditText editEmail;
    private EditText editPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 파이어베이스 모듈 사용하기
        auth = FirebaseAuth.getInstance();
        if(PreferenceUtil.getString(this,"auto_sign").equals("true")){
            String email = PreferenceUtil.getString(this,"email");
            String password = PreferenceUtil.getString(this,"password");
            signin(email, password);
        }else {
            setContentView(R.layout.activity_sign_in);

            database = FirebaseDatabase.getInstance();
            // 데이터베이스 user 레퍼런스 생성
            userRef = database.getReference("user");
            initView();
        }
    }

    private void signin(final String email, final String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            FirebaseUser fUser = auth.getCurrentUser();
                            //if(fUser.isEmailVerified()){
                            // preference에 값을 저장
                            PreferenceUtil.setValue(getBaseContext(), "user_id",email.replace(".","_"));
                            PreferenceUtil.setValue(getBaseContext(), "email",  email);
                            PreferenceUtil.setValue(getBaseContext(), "password",password);
                            PreferenceUtil.setValue(getBaseContext(), "auto_sign","true");

                            // 로그인진행
                            Intent intent = new Intent(SignInActivity.this, RoomListActivity.class);
                            startActivity(intent);
                            finish();
                            //}else{
                            //    DialogUtil.showDialog("이메일을 확인하셔야 합니다!",SigninActivity.this, false);
                            //}
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                DialogUtil.showDialog("오류발생:"+e.getMessage(),SignInActivity.this, false);
            }
        });
    }

    public void signin(View view) {
        String email = editEmail.getText().toString();
        String password = editPassword.getText().toString();
        signin(email, password);
    }


    public void signup(View view) {
        Intent intent = new Intent(this, SignUpActivity.class);
        startActivity(intent);
    }

    private void initView() {
        editEmail = (EditText) findViewById(R.id.editEmail);
        editPassword = (EditText) findViewById(R.id.editPassword);
    }
}