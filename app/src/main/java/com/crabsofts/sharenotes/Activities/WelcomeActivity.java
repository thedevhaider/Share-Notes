package com.crabsofts.sharenotes.Activities;

import android.animation.ArgbEvaluator;

import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;

import com.crabsofts.sharenotes.R;
import com.crabsofts.sharenotes.Adapters.ViewPagerAdapter;
import com.crabsofts.sharenotes.util.SharedPreferenceInfoUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import me.relex.circleindicator.CircleIndicator;

public class WelcomeActivity extends CoreActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private CircleIndicator circleIndicator;
    private View skip, next;
    private ViewPager viewPager;
    private PagerAdapter pagerAdapter;

    /*color for sliders*/
    private int[] color;

    /*required for color change transition*/
    private ArgbEvaluator argbEvaluator = new ArgbEvaluator();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_main);

        if (SharedPreferenceInfoUtil.isIntroShown(getContext())) {
            startHomeActivity();
            finish();
        }

        color = new int[]{
                getContext().getResources().getColor(R.color.screen1),
                getContext().getResources().getColor(R.color.screen2),
                getContext().getResources().getColor(R.color.screen3)
        };

        //set ViewPager adapter
        pagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        //set viewPager Indicator
        circleIndicator.setViewPager(viewPager);

    }

    @Override
    protected void bindViews() {
        circleIndicator = (CircleIndicator) findViewById(R.id.circulator_indicator);
        skip = findViewById(R.id.skip);
        next = findViewById(R.id.next);
        viewPager = (ViewPager) findViewById(R.id.intro_silder_pager);
    }

    @Override
    protected void setListeners() {
        skip.setOnClickListener(this);
        next.setOnClickListener(this);
        viewPager.addOnPageChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.next:
                if (viewPager.getCurrentItem() < 2)
                    viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
                else
                    startHomeActivity();
                break;
            case R.id.skip:
                startHomeActivity();
                break;
        }
    }

    private void startHomeActivity() {
        SharedPreferenceInfoUtil.addFirstTimeUser(getContext(), true);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Intent i = new Intent(this, LoginActivity.class);
        i.putExtra("name", user.getDisplayName().toString());
        i.putExtra("email", user.getEmail().toString());
        i.putExtra("photo", user.getPhotoUrl().toString());
        startActivity(i);
        finish();
    }


    /*viewPager Page change listener starting*/

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (position < (pagerAdapter.getCount() - 1) && position < (color.length - 1)) {
            viewPager.setBackgroundColor((Integer) argbEvaluator.evaluate(positionOffset, color[position], color[position + 1]));
        } else {
            viewPager.setBackgroundColor(color[color.length - 1]);
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    /*viewPager Page change listener ending*/

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
