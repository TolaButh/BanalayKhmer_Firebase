package tolabuth.example.banalaybook.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import tolabuth.example.banalaybook.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {
    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
    }
}