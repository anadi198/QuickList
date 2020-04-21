package com.katokateki.quicklist.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.katokateki.quicklist.R;
import com.katokateki.quicklist.adapters.FragmentCollectionAdapter;
import com.katokateki.quicklist.fragments.AddListingFragment;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    ViewPager2 myViewPager2;
    FragmentCollectionAdapter fragmentCollectionAdapter;
    TabLayout tabLayout;
    TextView toolTitle;
    FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setSupport();
        myViewPager2 = findViewById(R.id.viewpager);
        fragmentCollectionAdapter = new FragmentCollectionAdapter(this);
        myViewPager2.setAdapter(fragmentCollectionAdapter);
        tabLayout = findViewById(R.id.tabs);
        toolTitle = findViewById(R.id.toolbar_title);
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FragmentManager fm = getSupportFragmentManager();
                AddListingFragment dFragment = new AddListingFragment();
                dFragment.show(fm, "Add listing!");
            }
        });
        new TabLayoutMediator(tabLayout, myViewPager2, (tab, position) ->
        {
            if(position==0)
                tab.setText("Store Listings");
            else
                tab.setText("Your Listings");
        }
        ).attach();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        toolTitle.setText(auth.getCurrentUser().getDisplayName()+"'s QuickList");
    }
    public void signOut() {
    AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener(new OnCompleteListener<Void>() {
                public void onComplete(@NonNull Task<Void> task) {
                    Intent intent = new Intent(MainActivity.this, AuthActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                }
            });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.sign_out_button)
        {
            signOut();
        }
        return true;
    }
    public void setSupport()
    {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.sign_out_button)
        {
            signOut();
        }
    }
}
