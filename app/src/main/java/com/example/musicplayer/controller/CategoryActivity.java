package com.example.musicplayer.controller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

import com.example.musicplayer.R;
import com.example.musicplayer.controller.adapter.PagerAdapter;
import com.example.musicplayer.controller.adapter.ViewHolders;
import com.example.musicplayer.model.Qualifier;
import com.example.musicplayer.model.Song;
import com.google.android.material.tabs.TabLayout;

public class CategoryActivity extends AppCompatActivity implements ViewHolders.CallBacks {

    private static int STORAGE_PERMISSION_REQ_CODE = 1;
    private Bundle savedInstanceState;

    private ViewPager mViewPager;
    private TabLayout mTabLayout;
    private View mIndicator;
    private int mIndicatorWidth;
    private PagerAdapter mAdapter;

    public static Intent newIntent(Context target){
        Intent intent = new Intent(target,CategoryActivity.class);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);
        this.savedInstanceState = savedInstanceState;


        if (ContextCompat.checkSelfPermission(CategoryActivity.this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(CategoryActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE , Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    STORAGE_PERMISSION_REQ_CODE);

        } else RunActivity();

    }

    private void RunActivity(){

        initUI();

        mTabLayout.post(() -> {
            mIndicatorWidth = mTabLayout.getWidth()/ mTabLayout.getTabCount();

            FrameLayout.LayoutParams indicatorParams = (FrameLayout.LayoutParams) mIndicator.getLayoutParams();
            indicatorParams.width = mIndicatorWidth;
            mIndicator.setLayoutParams(indicatorParams);
        });

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams)mIndicator.getLayoutParams();

                //Multiply positionOffset with indicatorWidth to get translation

                float translationOffset =  (positionOffset+position) * mIndicatorWidth ;
                params.leftMargin = (int) translationOffset;
                mIndicator.setLayoutParams(params);
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private void initUI(){
        mViewPager = findViewById(R.id.view_pager);
        mTabLayout = findViewById(R.id.tab_layout);
        mIndicator = findViewById(R.id.indicator);

        mAdapter = new PagerAdapter(this, getSupportFragmentManager());
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    /**
     * Handle the permissions request response
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == STORAGE_PERMISSION_REQ_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                RunActivity();
            } else if(grantResults.length > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                RunActivity();
            }else {
                CategoryActivity.this.onBackPressed();
            }
        }
    }


    @Override
    public void SingleSong(Song song) {

    }

    @Override
    public void SongList(String albumOrArtist, Qualifier qualifier) {
        startActivity(SongListActivity.newIntent(CategoryActivity.this,albumOrArtist,qualifier));
    }
}
