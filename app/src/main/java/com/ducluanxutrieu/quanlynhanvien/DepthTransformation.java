package com.ducluanxutrieu.quanlynhanvien;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.google.firebase.database.annotations.NotNull;


public class DepthTransformation implements ViewPager.PageTransformer{
    @Override
    public void transformPage(@NotNull View pager, float position) {

        if (position < -1){    // [-Infinity,-1)
            // This pager is way off-screen to the left.
            pager.setAlpha(0);

        }
        else if (position <= 0){    // [-1,0]
            pager.setAlpha(1);
            pager.setTranslationX(0);
            pager.setScaleX(1);
            pager.setScaleY(1);

        }
        else if (position <= 1){    // (0,1]
            pager.setTranslationX(-position*pager.getWidth());
            pager.setAlpha(1-Math.abs(position));
            pager.setScaleX(1-Math.abs(position));
            pager.setScaleY(1-Math.abs(position));

        }
        else {    // (1,+Infinity]
            // This pager is way off-screen to the right.
            pager.setAlpha(0);

        }


    }
}