package com.akiya.dev.mymusicapp.allsonglist

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
import com.akiya.dev.mymusicapp.R
import com.akiya.dev.mymusicapp.allsonglist.MusicService.MusicBinder
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity(), MediaController.MediaPlayerControl {

    private var songList: ArrayList<Song>? = null

    private var musicSrv: MusicService? = null
    private var playIntent: Intent? = null
    private var musicBound = false

    private var controller: MusicController? = null

    private var paused = false
    private  var playbackPaused:kotlin.Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
            if (shouldShowRequestPermissionRationale(
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )) {
                // Explain to the user why we need to read the contacts
            }

            requestPermissions(
                    arrayOf(
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE
                    ), 1
            )

            // MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE is an
            // app-defined int constant that should be quite unique

            return
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

        setController()
    }

    private fun setController() {
        //set the controller up
        controller = MusicController(this)

        controller!!.setPrevNextListeners({ playNext() }) { playPrev() }

        controller!!.setMediaPlayer(this)
        controller!!.setAnchorView(findViewById(R.id.song_list))
        controller!!.setEnabled(true)
    }

    //play next
    private fun playNext() {
        musicSrv?.playNext()
        if(playbackPaused){
            setController()
            playbackPaused=false
        }
        controller?.show(0)
    }

    //play previous
    private fun playPrev() {
        musicSrv?.playPrev()
        if(playbackPaused){
            setController()
            playbackPaused=false
        }
        controller?.show(0)
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
        if(playbackPaused){
            setController();
            playbackPaused=false
        }
        controller?.show(0)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_shuffle -> {
                musicSrv?.setShuffle()
                System.exit(0)
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
        songList?.add(Song(3, "Forest", "Invisible"))
        songList?.add(Song(4, "Mind", "Invisible"))
        songList?.add(Song(5, "Morning", "Invisible"))
        songList?.add(Song(6, "My algorithm", "Invisible"))
        songList?.add(Song(7, "Night", "Invisible"))
        songList?.add(Song(8, "Second rain", "Invisible"))
        songList?.add(Song(9, "Technicolor", "Invisible"))
        songList?.add(Song(10, "The logical flight of a paper plane", "Invisible"))
        songList?.add(Song(11, "Tree year", "Invisible"))
        songList?.add(Song(12, "Winter sun", "Invisible"))
    }

    override fun start() {
        musicSrv?.go()
    }

    override fun pause() {
        playbackPaused=true
        musicSrv?.pausePlayer()
    }

    override fun onPause() {
        super.onPause()
        paused=true
    }

    override fun onResume() {
        super.onResume()
        if (paused) {
            setController()
            paused = false
        }
    }

    override fun onStop() {
        controller!!.hide()
        super.onStop()
    }

    override fun getDuration(): Int {
        if(musicSrv!=null && musicBound && musicSrv!!.isPng())
        return musicSrv!!.getDur();
        else return 0;
    }

    override fun getCurrentPosition(): Int {
        if(musicSrv!=null && musicBound && musicSrv!!.isPng())
        return musicSrv!!.getPosn()
        else return 0
    }

    override fun seekTo(pos: Int) {
        musicSrv?.seek(pos)
    }

    override fun isPlaying(): Boolean {
        if(musicSrv!=null && musicBound)
        return musicSrv!!.isPng();
        return false;
    }

    override fun getBufferPercentage(): Int {
        return 0
    }

    override fun canPause(): Boolean {
        return true
    }

    override fun canSeekBackward(): Boolean {
        return true
    }

    override fun canSeekForward(): Boolean {
        return true
    }

    override fun getAudioSessionId(): Int {
        return 0
    }

    override fun onBackPressed() {
        super.onBackPressed()

    }


}