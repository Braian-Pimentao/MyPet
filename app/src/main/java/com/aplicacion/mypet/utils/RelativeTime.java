package com.aplicacion.mypet.utils;

import android.app.Application;
import android.content.Context;

import com.aplicacion.mypet.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RelativeTime extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        // TODO: localize
        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return ctx.getString(R.string.hace_un_momento);
        } else if (diff < 2 * MINUTE_MILLIS) {
            return ctx.getString(R.string.hace_un_minuto);
        } else if (diff < 50 * MINUTE_MILLIS) {
            return ctx.getString(R.string.hace_unos_minutos,diff / MINUTE_MILLIS);
        } else if (diff < 90 * MINUTE_MILLIS) {
            return ctx.getString(R.string.hace_una_hora);
        } else if (diff < 24 * HOUR_MILLIS) {
            return ctx.getString(R.string.hace_unas_horas,diff / HOUR_MILLIS);
        } else if (diff < 48 * HOUR_MILLIS) {
            return ctx.getString(R.string.ayer);
        } else {
            return ctx.getString(R.string.hace_unos_dias,diff / DAY_MILLIS);
        }
    }

    public static String timeFormatAMPM(long timestamp) {

        SimpleDateFormat formatter = new SimpleDateFormat("H:mm");
        return formatter.format(new Date(timestamp));
    }

}
