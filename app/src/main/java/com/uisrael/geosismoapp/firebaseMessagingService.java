package com.uisrael.geosismoapp;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

public class firebaseMessagingService extends FirebaseMessagingService {

    //Declaración de variables
    private int contador=0;

    //Método que recibe las notificaciones
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from = remoteMessage.getFrom();
        Log.d("NOTICIAS", "Mensaje recibido de: "+from);
        if (remoteMessage.getNotification() !=null){
            Log.d("NOTICIAS", "Notificacion: "+remoteMessage.getNotification().getBody());
        }
        if (remoteMessage.getData().size() > 0) {
            Log.d("NOTICIAS", "Data: " + remoteMessage.getData());
        }
        SharedPreferences prefs = getSharedPreferences("ContadorNotificaciones", MODE_PRIVATE);
        contador = prefs.getInt("contador",0);
        mostrarNotificacion(remoteMessage.getNotification().getTitle(),remoteMessage.getNotification().getBody());
    }

    //Método para mostrar la notificación en los dispositivos móviles
    private void mostrarNotificacion(String title, String body) {
        Gson gson = new Gson();
        sismos sismo = gson.fromJson(body, sismos.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String CHANNEL_ID = "Sismo";
            String name="Sismo Notificación";
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, NotificationManager.IMPORTANCE_HIGH);
            mChannel.enableLights(true);
            mChannel.setLightColor(Color.BLUE);
            mChannel.enableVibration(true);
            mChannel.setLockscreenVisibility(NotificationCompat.PRIORITY_HIGH);
            mChannel.setVibrationPattern(new long[] { 1000, 1000, 1000, 1000, 1000 });
            // Create a notification and set the notification channel.
            Notification notification = new NotificationCompat.Builder(this,CHANNEL_ID)
                    .setContentTitle(title)
                    .setContentText("AVISO DE SISMOS REGISTRADOS")
                    .setSmallIcon(R.drawable.logo)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("Ciudad mas cercana: "+sismo.getCiudadMasCercana()+"\n"+
                                    "Fecha: "+sismo.getHoraLocal()+"\n"+
                                    "Magnitud: "+sismo.getMagnitud()+"\n"+
                                    "Profundidad: "+sismo.getProfundidad()))
                    .setLargeIcon( BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.logo))
                    .setDefaults(Notification.DEFAULT_SOUND)
                    .setVibrate(new long[]{1000, 1000, 1000, 1000, 1000})
                    .build();

            NotificationManager notificationManager =  (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(mChannel);
            notificationManager.notify(contador, notification);
        }
        contador++;
        SharedPreferences.Editor editor = getSharedPreferences("ContadorNotificaciones", MODE_PRIVATE).edit();
        editor.putInt("contador", contador);
        editor.apply();
    }
}
