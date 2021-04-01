package com.example.aidltest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.os.RemoteCallbackList
import android.util.Log
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicBoolean

open class BookManagerService : Service() {

    private val TAG = "BMS"

    private var mBookList = CopyOnWriteArrayList<Book>()

    private var mIsServiceDestoryed = AtomicBoolean(false)

    //订阅者列表
    //RemoteCallbackList是系统专门提供的用于删除跨进程 listener 的接口
    private var mListenerList = RemoteCallbackList<IOnNewBookArrivedListener>();

    private val  mBinder = object : IBookManager.Stub(){
        override fun getBookList(): MutableList<Book> {
            return mBookList
        }

        override fun addBook(book: Book?) {
            mBookList.add(book)
        }

        override fun registerListener(listener: IOnNewBookArrivedListener?) {
            mListenerList.register(listener)
        }

        override fun unregisterListener(listener: IOnNewBookArrivedListener?) {
            mListenerList.unregister(listener)
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

        var N = mListenerList.beginBroadcast()
        for(i in 0 until N ){
            var listener = mListenerList.getBroadcastItem(i)
            listener?.onNewBookArrived(book)
        }
        mListenerList.finishBroadcast()
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