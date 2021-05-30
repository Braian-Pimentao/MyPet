package com.aplicacion.mypet.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;

import com.aplicacion.mypet.providers.AuthProvider;
import com.aplicacion.mypet.providers.UserProvider;

import java.util.List;

public class ViewedMessageHelper {

    public static void updateOnline(boolean status, final Context context) {
        UserProvider usersProvider = new UserProvider();
        AuthProvider authProvider = new AuthProvider();
        if (authProvider.getUid() != null) {
            if (isApplicationSentToBackground(context)) {
                usersProvider.updateOnline(authProvider.getUid(), status);
            }
            else if (status){
                usersProvider.updateOnline(authProvider.getUid(), status);
            }
        }
    }

    public static boolean isApplicationSentToBackground(final Context context) {
        /*
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = activityManager.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            System.out.println("NAME-----------------------------------------------------------------"+topActivity);
            System.out.println("qweqwe-----------------------------------------------------------------"+context.getPackageName());

            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;

         */

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.AppTask> task = activityManager.getAppTasks();

        if (!task.isEmpty()) {
            ComponentName componentInfo = task.get(0).getTaskInfo().topActivity;
            System.out.println("NAME-----------------------------------------------------------------"+componentInfo);
            if (!componentInfo.getPackageName().equals(context.getPackageName()))
                return true;
        }
        return false;



    }

}