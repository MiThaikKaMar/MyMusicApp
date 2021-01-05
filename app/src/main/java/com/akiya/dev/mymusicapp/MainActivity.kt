package com.akiya.dev.mymusicapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.view.MenuItem
import android.view.View
import android.widget.MediaController
import androidx.appcompat.app.AppCompatActivity
import com.akiya.dev.mymusicapp.MusicService.MusicBinder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


class MainActivity : AppCompatActivity(),MediaController.MediaPlayerControl {

    private var songList: ArrayList<Song>? = null

    private var musicSrv: MusicService? = null
    private var playIntent: Intent? = null
    private var musicBound = false

    private var controller: MusicController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE)) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE), 1)

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return;
        }
        setContentView(R.layout.activity_main)

        songList = arrayListOf()

        getSongList()

        Collections.sort(songList, object : Comparator<Song?> {
            override fun compare(o1: Song?, o2: Song?): Int {
                return o1?.title!!.compareTo(o2?.title.toString())
            }
        })

        val songAdt = SongAdapter(this, songList!!)
        song_list.adapter = songAdt

    }

    private fun setController() {
        //set the controller up
        controller = MusicController(this)
    }

    //connect to the service
    private val musicConnection: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            val binder = service as MusicBinder
            //get service
            musicSrv = binder.service
            //pass list
            musicSrv?.setList(songList)
            musicBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            musicBound = false
        }
    }

    override fun onStart() {
        super.onStart()
        if (playIntent == null) {
            playIntent = Intent(this, MusicService::class.java)
            bindService(playIntent, musicConnection, Context.BIND_AUTO_CREATE)
            startService(playIntent)
        }
    }

    fun songPicked(view: View) {
        musicSrv?.setSong(view.getTag().toString().toInt())
        musicSrv?.playSong()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_shuffle -> {
            }
            R.id.action_end -> {
                stopService(playIntent)
                musicSrv = null
                System.exit(0)
            }
        }
        return super.onOptionsItemSelected(item)
        //menu item selected
    }

    override fun onDestroy() {
        stopService(playIntent)
        musicSrv = null
        super.onDestroy()
    }


    @SuppressLint("Recycle")
    private fun getSongList(){
//        val musicResolver = contentResolver
//        val musicUri: Uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
//        val musicCursor: Cursor? = musicResolver.query(musicUri, null, null, null, null)
//
//        if (musicCursor != null && musicCursor.moveToFirst()) {
//            //get columns
//            val titleColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.TITLE)
//            val idColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media._ID)
//            val artistColumn = musicCursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)
//            //add songs to list
//            do {
//                val thisId = musicCursor.getLong(idColumn)
//                val thisTitle = musicCursor.getString(titleColumn)
//                val thisArtist = musicCursor.getString(artistColumn)
//                songList!!.add(Song(thisId, thisTitle, thisArtist))
//            } while (musicCursor.moveToNext())
//        }
        songList?.add(Song(1, "Blue", "Invisible"))
        songList?.add(Song(2, "Distance", "Invisible"))
    }

    override fun start() {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun getDuration(): Int {
        TODO("Not yet implemented")
    }

    override fun getCurrentPosition(): Int {
        TODO("Not yet implemented")
    }

    override fun seekTo(pos: Int) {
        TODO("Not yet implemented")
    }

    override fun isPlaying(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getBufferPercentage(): Int {
        TODO("Not yet implemented")
    }

    override fun canPause(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canSeekBackward(): Boolean {
        TODO("Not yet implemented")
    }

    override fun canSeekForward(): Boolean {
        TODO("Not yet implemented")
    }

    override fun getAudioSessionId(): Int {
        TODO("Not yet implemented")
    }
}