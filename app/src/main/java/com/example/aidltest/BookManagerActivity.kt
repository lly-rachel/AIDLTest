package com.example.aidltest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class BookManagerActivity : AppCompatActivity() {

    private val TAG = "BookManagerActivity"

    private val mConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val bookManager = IBookManager.Stub.asInterface(p1)

            var list = bookManager.bookList
            Log.i(TAG,"query book list,list type:${list::class.java.canonicalName}")
            Log.i(TAG,list.toString())
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            TODO("Not yet implemented")
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this,BookManagerService::class.java)
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {
        unbindService(mConnection)
        super.onDestroy()
    }
}