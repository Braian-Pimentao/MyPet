package com.aplicacion.mypet.utils;

public class AppInfo {
    public static boolean IN_ACTIVITY_CHAT = false;
    public static boolean AVISO_REALIZADO = false;
    public static void init(boolean inActivityChat){
        IN_ACTIVITY_CHAT = inActivityChat;
    }
    public static void aviso(boolean aviso){
        AVISO_REALIZADO = aviso;
    }
}
