package com.ducluanxutrieu.quanlynhanvien.util

import androidx.viewpager.widget.ViewPager
import android.view.View

import com.google.firebase.database.annotations.NotNull
import kotlin.math.abs


class DepthTransformation : ViewPager.PageTransformer {
    override fun transformPage(@NotNull pager: View, position: Float) {

        when {
            position < -1 -> // [-Infinity,-1)
                // This pager is way off-screen to the left.
                pager.alpha = 0f
            position <= 0 -> {    // [-1,0]
                pager.alpha = 1f
                pager.translationX = 0f
                pager.scaleX = 1f
                pager.scaleY = 1f

            }
            position <= 1 -> {    // (0,1]
                pager.translationX = -position * pager.width
                pager.alpha = 1 - abs(position)
                pager.scaleX = 1 - abs(position)
                pager.scaleY = 1 - abs(position)

            }
            else -> // (1,+Infinity]
                // This pager is way off-screen to the right.
                pager.alpha = 0f
        }


    }
}