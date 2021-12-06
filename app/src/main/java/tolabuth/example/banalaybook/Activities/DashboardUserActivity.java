package tolabuth.example.banalaybook.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import tolabuth.example.banalaybook.BooksUserFragment;
import tolabuth.example.banalaybook.R;
import tolabuth.example.banalaybook.databinding.ActivityDashboardUserBinding;
import tolabuth.example.banalaybook.model.ModelCategory;

public class DashboardUserActivity extends AppCompatActivity {
    //view binding
    private ActivityDashboardUserBinding binding;
    //firebase
    private FirebaseAuth firebaseAuth;
    //show in tabs
    public ArrayList<ModelCategory>categoryArrayList;
    public ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardUserBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //initial
        firebaseAuth = FirebaseAuth.getInstance();


        checkUser();
        setupViewPagerAdapter(binding.viewPage);
        binding.tabs.setupWithViewPager(binding.viewPage);
        //handler logout

        binding.ImbDUserlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firebaseAuth.signOut();
               // checkUser();
                startActivity(new Intent(DashboardUserActivity.this, MainActivity.class));
                finish();
            }
        });
        //handle click , open of Profile
        binding.btnProfileUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DashboardUserActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    private  void setupViewPagerAdapter(ViewPager viewPager){
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,this);
        categoryArrayList = new ArrayList<>();
        //load categories from firebase
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Categories");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //clear before adding to list
                //load Categories -Static e.g
                ModelCategory modelAll =  new ModelCategory("01", "All","", 1);
                ModelCategory modelMostView =  new ModelCategory("02", "Most View","", 1);
                ModelCategory modelMostDownloaded =  new ModelCategory("03", "Most Download","", 1);
                //add model to list;
                categoryArrayList.add(modelAll);
                categoryArrayList.add(modelMostView);
                categoryArrayList.add(modelMostDownloaded);
                //add data to list
                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelAll.getId()
                        ,""+modelAll.getCategory()
                        ,""+modelAll.getUdi()
                ), modelAll.getCategory());
                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelMostView.getId()
                        ,""+modelMostView.getCategory()
                        ,""+modelMostView.getUdi()
                ),modelMostView.getCategory());

                viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                        ""+modelMostDownloaded.getId()
                        ,""+modelMostDownloaded.getCategory()
                        ,""+modelMostDownloaded.getUdi()
                ),modelMostDownloaded.getCategory());
                //refresh list
                viewPagerAdapter.notifyDataSetChanged();
                //Now Load from firebase
                for (DataSnapshot ds: snapshot.getChildren()){
                    //get data
                     ModelCategory model = ds.getValue(ModelCategory.class);
                     //add data to list
                    categoryArrayList.add(model);
                    //add data to view page Adapter
                    viewPagerAdapter.addFragment(BooksUserFragment.newInstance(
                            ""+model.getId()
                            ,""+model.getCategory()
                            ,""+model.getUdi()
                    ),model.getCategory());
                    //refresh list
                    viewPagerAdapter.notifyDataSetChanged();

                }




            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        //set adapter to view pager
        viewPager.setAdapter(viewPagerAdapter);

    }


   public class ViewPagerAdapter extends  FragmentPagerAdapter{
        private ArrayList<BooksUserFragment>fragmentList = new ArrayList<>();
        private ArrayList<String>fragmentTitleList = new ArrayList<>();
        private Context context;

       public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior,Context context) {
           super(fm, behavior);
           this.context = context;
       }

       @NonNull
       @Override
       public Fragment getItem(int position) {
           return fragmentList.get(position);
       }

       @Override
       public int getCount() {
           return fragmentList.size();
       }
       private void addFragment(BooksUserFragment fragment, String title){
           //add fragment   passed
           fragmentList.add(fragment);
           fragmentTitleList.add(title);

       }

       @Nullable
       @Override
       public CharSequence getPageTitle(int position) {
           return fragmentTitleList.get(position);
       }
   }
   //set


    private void checkUser() {
        //getCurrent User
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser== null){
            //not logged in , goto main screen
//            startActivity(new Intent(DashboardUserActivity.this, MainActivity.class));
//            finish();
            binding.tvDUserTitle.setText("Not Logged In");


        }else {
            //logged in,get user info
            String email = firebaseUser.getEmail();
            binding.tvDUserEmail.setText(email);

        }
    }
}