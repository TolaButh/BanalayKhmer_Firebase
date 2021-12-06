package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import tolabuth.example.banalaybook.R;

public class SplashActivity extends AppCompatActivity {
     //firebase author
    private FirebaseAuth firebaseAuth;
    private final static String TAG = "CheckUser";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        //initial firebase author
        firebaseAuth = FirebaseAuth.getInstance();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
               checkUser();

            }
        },2000);// mean 2 second
    }

    private void checkUser() {
        //get current user, if logged in
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        Toast.makeText(SplashActivity.this,""+firebaseUser,Toast.LENGTH_SHORT).show();

        if (firebaseUser == null){
            //user not login
            //start main screen
            startActivity(new Intent(SplashActivity.this, MainActivity.class));
            finish();//finish activity
        }else {
            //user login check user type , same as login
            //get current user
           // FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
            //check in Database
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Users");

            ref.child(firebaseUser.getUid())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {

                            //get user type
                            String userType =""+snapshot.child("userType").getValue();
                            //check user Type
                            if (userType.equals("user")){
                                Log.d(TAG,"Ta: "+firebaseUser);
                                //this is simple user, open user dashboard
                                startActivity(new Intent(SplashActivity.this, DashboardUserActivity.class));
                                finish();
                            }else if (userType.equals("admin")){
                                //this is simple admin, open admin dashboard
                                startActivity(new Intent(SplashActivity.this, DashboardAdminActivity.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
        }
    }
}