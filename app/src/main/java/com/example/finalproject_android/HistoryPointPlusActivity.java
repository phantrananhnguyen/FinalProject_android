package com.example.finalproject_android;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.finalproject_android.afterlogin.HistoryPointPlusFragment;

public class HistoryPointPlusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_point_plus);

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HistoryPointPlusFragment())
                    .commit();
        }
    }
}
