package com.example.aidltest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

open class BookManagerService : Service() {

    private val TAG = "BMS"

    private var mBookList = CopyOnWriteArrayList<Book>()

    private var mIsServiceDestoryed = AtomicBoolean(false)

    //订阅者列表
    private var mListenerList = CopyOnWriteArrayList<IOnNewBookArrivedListener>();

    private val  mBinder = object : IBookManager.Stub(){
        override fun getBookList(): MutableList<Book> {
            return mBookList
        }

        override fun addBook(book: Book?) {
            mBookList.add(book)
        }

        override fun registerListener(listener: IOnNewBookArrivedListener?) {
            if(!mListenerList.contains(listener)){
                mListenerList.add(listener)
            }else{
                Log.d(TAG,"$listener always existed")
            }
            Log.d(TAG,"registerListener,size:${mListenerList.size}")
        }

        override fun unregisterListener(listener: IOnNewBookArrivedListener?) {
            if(mListenerList.contains(listener)){
                mListenerList.remove(listener)
                Log.d(TAG,"unregister success.")
            }else{
                Log.d(TAG,"not found,can not unregister.")
            }
            Log.d(TAG,"unregisterListener,size:${mListenerList.size}")
        }


    }

    override fun onCreate() {
        super.onCreate()
        mBookList.add(Book("Android疯狂讲义",1))
        mBookList.add(Book("Android开发艺术探索",2))

        Thread(ServiceWorker()).start()
    }

    override fun onDestroy() {
        mIsServiceDestoryed.set(true)
        super.onDestroy()
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder;
    }

    //通知订阅者新书已到达
    private fun onNewBookArrived(book:Book){
        mBookList.add(book)
        Log.d(TAG,"onNewBookArrived,notify listeners:${mListenerList.size}")
        for(i in 0 until mListenerList.size){
            var listener = mListenerList.get(i)
            Log.d(TAG,"OnNewBookArrived,notify listener:$listener")
            listener.onNewBookArrived(book)
        }
    }

    private inner class ServiceWorker : Runnable{
        override fun run() {
            while (!mIsServiceDestoryed.get()){
                Thread.sleep(1000)

                var bookId = mBookList.size + 1
                var  newBook = Book("new book#$bookId",bookId)
                onNewBookArrived(newBook)
            }
        }

    }


}