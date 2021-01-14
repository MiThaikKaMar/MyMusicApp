package com.akiya.dev.mymusicapp.allsonglist;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.akiya.dev.mymusicapp.R;

import java.util.ArrayList;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnErrorListener, MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {
    //media player
    private MediaPlayer player;
    //song list
    private ArrayList<Song> songs;
    //current position
    private int songPosn;

    private String songTitle="";
    private static final int NOTIFY_ID=1;

    private final IBinder musicBind = new MusicBinder();

    private boolean shuffle=false;
    private Random rand;

    private String channelID = "CHANNEL_ID";

    @Override
    public void onCreate() {
        super.onCreate();
//initialize position
        songPosn = 0;
//create player
        player = new MediaPlayer();

        initMusicPlayer();
        rand=new Random();

    }


    public void setShuffle(){
        if(shuffle) shuffle=false;
        else shuffle=true;
    }

    //skip to next
    public void playNext(){
        if(shuffle){
            int newSong = songPosn;
            while(newSong==songPosn){
                newSong=rand.nextInt(songs.size());
            }
            songPosn=newSong;
        }
        else{
            songPosn++;
            if(songPosn>=songs.size()) songPosn=0;
        }
        playSong();
    }

    public void initMusicPlayer(){
        //set player properties
        player.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);

        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
    }

    @Override
    public boolean onUnbind(Intent intent){
        player.stop();
        player.release();
        return false;
    }



    public void setList(ArrayList<Song> theSongs){
        songs=theSongs;
    }

    public class MusicBinder extends Binder {
        MusicService getService() {
            return MusicService.this;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return musicBind;
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        mp.reset();
        return false;
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        if(player.getCurrentPosition()>0){
            mp.reset();
            playNext();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onPrepared(MediaPlayer mp) {

        //start playback
        mp.start();

        createNotificationChannel();
        Intent notIntent = new Intent(this, MainActivity.class);
        notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendInt = PendingIntent.getActivity(this, 0,
                notIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder builder = new Notification.Builder(this,channelID);

        builder.setContentIntent(pendInt)
                .setSmallIcon(R.drawable.ic_baseline_play_arrow_24)
                .setTicker(songTitle)
                .setOngoing(true)
                .setContentTitle("Playing")
                .setContentText(songTitle);
        Notification not = builder.build();

        startForeground(NOTIFY_ID, not);
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String name = "Akiya Research";
            int important = NotificationManager.IMPORTANCE_DEFAULT;
            String descriptionContent = "Akiya Research";
            NotificationChannel channel = new NotificationChannel(channelID, name, important);
            channel.setDescription(descriptionContent);

            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(channel);
        }
    }


    public void setSong(int songIndex){
        songPosn=songIndex;
    }

    public void playSong(){
        //play a song
        player.reset();

        //get song
        Song playSong = songs.get(songPosn);
        songTitle=playSong.getTitle();

//get id
        long currSong = playSong.getId();
//set uri
        Uri trackUri = ContentUris.withAppendedId(
                android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                currSong);

        Uri url = null;
        if(currSong == 1) {
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.blue);
        }else if(currSong == 2 ){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.distance);
        }else if (currSong == 3){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.forest);
        }
        else if (currSong == 4){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.mind);
        }
        else if (currSong == 5){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.morning);
        }
        else if (currSong == 6){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.my_algorithm);
        }
        else if (currSong == 7){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.night);
        }
        else if (currSong == 8){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.second_rain);
        }
        else if (currSong == 9){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.technicolor);
        }
        else if (currSong == 10){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.the_logical_flight_of_a_paper_plane);
        }
        else if (currSong == 11){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.tree_year);
        }
        else if (currSong == 12){
            url = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.winter_sun);
        }

        try{
            player.setDataSource(getApplicationContext(), url);
        }
        catch(Exception e){
            Log.e("MUSIC SERVICE", "Error setting data source", e);
        }

        player.prepareAsync();

    }

    public int getPosn(){
        return player.getCurrentPosition();
    }

    public int getDur(){
        return player.getDuration();
    }

    public boolean isPng(){
        return player.isPlaying();
    }

    public void pausePlayer(){
        player.pause();
    }

    public void seek(int posn){
        player.seekTo(posn);
    }

    public void go(){
        player.start();
    }

    public void playPrev(){
        songPosn--;
        if(songPosn<0) songPosn=songs.size()-1;
        playSong();
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
    }
}
