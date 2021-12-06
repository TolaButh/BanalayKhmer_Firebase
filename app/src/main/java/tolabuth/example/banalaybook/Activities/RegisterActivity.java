package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

import tolabuth.example.banalaybook.databinding.ActivityRegisterBinding;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    //firebase author
    private FirebaseAuth firebaseAuth;
    //progress Dialog
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //initial firebase
        firebaseAuth = FirebaseAuth.getInstance();
        //setup progress dialog
        progressDialog= new ProgressDialog(this);
        progressDialog.setTitle("Please wait");
        progressDialog.setCanceledOnTouchOutside(false);
        binding.btnRegisterSytem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();

            }
        });
        //hander back click
        binding.imggeRegisterBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
    private String name="", email="", password="", cpassword;
    private void validateData() {
        //Before create account , let's be check validation
        //ตารวจสอบความถุกต้องก่อน
        name= binding.edtRegisterName.getText().toString().trim();
        email= binding.edtRegisterEmail.getText().toString().trim();
        password= binding.edtRegisterPassword.getText().toString().trim();
        cpassword = binding.edtRegisterCpassword.getText().toString().trim();
        //validate data
        if (TextUtils.isEmpty(name)){
            Toast.makeText(RegisterActivity.this, "Enter you name.....", Toast.LENGTH_SHORT).show();
        }else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            Toast.makeText(this, "Invalide email pattern!.....",Toast.LENGTH_SHORT).show();
        }else if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Enter you password",Toast.LENGTH_SHORT).show();
        }else if (TextUtils.isEmpty(cpassword)){
            Toast.makeText(this, "Confirm password!!!..",Toast.LENGTH_SHORT).show();
        }else if(!password.equals(cpassword)){
            Toast.makeText(this,"Password doesn't match......",Toast.LENGTH_SHORT).show();
        }else {
            createUserAccount();
        }
    }

    private void createUserAccount() {
        //show progress
        progressDialog.setMessage("Creating account....");
        progressDialog.show();
        //create user in firebase author
        firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                //account create success
                //now add in firebase
                updateUserInfor();

            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //account create failed
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });

    }

    private void updateUserInfor() {
        //save your infor...
        progressDialog.setMessage("Saving in your infor....");
        //setTime timestamp
        long timestamp = System.currentTimeMillis();
        //get cerrent user uid, since user is registerso we can get now
        String uid = firebaseAuth.getUid();
        //setup data to database firebase
        HashMap<String,Object>hashMap = new HashMap<>();
        hashMap.put("uid", uid);
        hashMap.put("email", email);
        hashMap.put("name", name);
        hashMap.put("profileImage","");
        hashMap.put("userType", "user");//possible value are user, admin: will make admin manually in firebase realtime
        hashMap.put("timestamp", timestamp);
        //set data to database
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");
        ref.child(uid)
                .setValue(hashMap)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        //data add to database
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Account creating.....", Toast.LENGTH_SHORT).show();
                        //since user account 
                        startActivity(new Intent(RegisterActivity.this, DashboardUserActivity.class));
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //data faired
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });


    }
}