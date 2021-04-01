package com.example.aidltest

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class BookManagerActivity : AppCompatActivity() {


    companion object{
        const val MESSAGE_NEW_BOOK_ARRIVED = 1
        const val TAG = "BookManagerActivity"
    }


    private var mRemoteBookManager : IBookManager ?= null

    private var mHandler :Handler = object :Handler(Looper.getMainLooper()){
        override fun handleMessage(msg: Message) {
            when(msg?.what){
                MESSAGE_NEW_BOOK_ARRIVED -> {
                    Log.d(TAG,"receive new book : ${msg.obj}")
                }
                else ->{
                    super.handleMessage(msg)
                }
            }

        }
    }


    private val mConnection = object : ServiceConnection{
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val bookManager = IBookManager.Stub.asInterface(p1)

            mRemoteBookManager = bookManager

            var list = bookManager.bookList
            Log.i(TAG,"query book list,list type:${list::class.java.canonicalName}")
            Log.i(TAG,list.toString())

            //在客户端调用一下 addBook
            var book = Book("Android进阶之光",3)
            bookManager.addBook(book)
            var newList = bookManager.bookList
            Log.i(TAG,newList.toString())


            //订阅消息
            bookManager.registerListener(mIOnNewBookArrivedListener)
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            mRemoteBookManager = null
            Log.e(TAG,"binder died")
        }

    }

    private var mIOnNewBookArrivedListener = object : IOnNewBookArrivedListener.Stub(){
        override fun onNewBookArrived(newBook: Book?) {
            mHandler.obtainMessage(MESSAGE_NEW_BOOK_ARRIVED,newBook).sendToTarget()
        }

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val intent = Intent(this,BookManagerService::class.java)
        bindService(intent,mConnection, Context.BIND_AUTO_CREATE)
    }

    override fun onDestroy() {

        if(mRemoteBookManager!=null&& mRemoteBookManager!!.asBinder().isBinderAlive){
            Log.i(TAG,"unregister listen:$mIOnNewBookArrivedListener")
            mRemoteBookManager!!.unregisterListener(mIOnNewBookArrivedListener)
        }

        unbindService(mConnection)
        super.onDestroy()
    }
}