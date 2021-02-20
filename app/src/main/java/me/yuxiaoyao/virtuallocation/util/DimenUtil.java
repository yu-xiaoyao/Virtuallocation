package me.yuxiaoyao.virtuallocation.util;


import android.content.Context;
import android.util.DisplayMetrics;

public class DimenUtil {

    public static int dp2px(Context ctx, float dp) {
        float scale = ctx.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }


    /**
     * 获取屏幕宽,高度
     *
     * @param ctx
     * @return
     */
    public static int[] displaySize(Context ctx) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

    /**
     * 获取屏幕高度
     *
     * @param ctx
     * @return
     */
    public static int displayHeightPixels(Context ctx) {
        DisplayMetrics displayMetrics = ctx.getResources().getDisplayMetrics();
        return displayMetrics.heightPixels;
    }

}


