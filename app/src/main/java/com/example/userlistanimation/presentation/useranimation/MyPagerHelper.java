package com.example.userlistanimation.presentation.useranimation;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;

import androidx.interpolator.view.animation.LinearOutSlowInInterpolator;
import androidx.viewpager2.widget.ViewPager2;

public class MyPagerHelper {

    /**
     * Save the previous animatedValue
     */
    private static int previousValue;

    /**
     * Set the current Item
     * @param pager viewpager2
     * @param item The item to jump to next
     * @param duration scroll duration
     */
    public static void setCurrentItem(final ViewPager2 pager, int item, long duration) {
        previousValue = 0;
        int currentItem = pager.getCurrentItem();
        int pagePxWidth = pager.getWidth();
        int pxToDrag = pagePxWidth * (item-currentItem);
        final ValueAnimator animator = ValueAnimator.ofInt(0, pxToDrag);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int currentValue = (int) animation.getAnimatedValue();
                float currentPxToDrag = (float) (currentValue-previousValue);
                pager.fakeDragBy(-currentPxToDrag);
                previousValue = currentValue;
            }
        });
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                pager.beginFakeDrag();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                pager.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) {}

            @Override
            public void onAnimationRepeat(Animator animation) {}
        });
        animator.setInterpolator(new AccelerateInterpolator());
        animator.setDuration(duration);
        animator.start();
    }
}
