package com.akiya.dev.mymusicapp

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.akiya.dev.mymusicapp.R.*


class SongAdapter(c: Context?, theSongs: ArrayList<Song>) : BaseAdapter() {

    private var songs: ArrayList<Song> = theSongs
    private var songInf: LayoutInflater? = LayoutInflater.from(c)

    override fun getCount(): Int {
        return songs.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        //map to song layout
        val songLay = songInf?.inflate(layout.song, parent, false)
        //get song using position
        val currSong = songs[position]!!
        //get title and artist strings
        val songView = songLay?.findViewById<TextView>(R.id.song_title)
        val artistView = songLay?.findViewById<TextView>(id.song_artist)
        songView?.text = currSong.title
        artistView?.text = currSong.artist
        //set position as tag
        songLay?.tag = position
        return songLay
    }
}

