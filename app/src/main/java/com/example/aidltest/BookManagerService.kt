package com.example.aidltest

import android.app.Service
import android.content.Intent
import android.os.IBinder
import java.util.concurrent.CopyOnWriteArrayList

open class BookManagerService : Service() {

    private val TAG = "BMS"

    private var mBookList = CopyOnWriteArrayList<Book>()

    private val  mBinder = object : IBookManager.Stub(){
        override fun getBookList(): MutableList<Book> {
            return mBookList
        }

        override fun addBook(book: Book?) {
            mBookList.add(book)
        }

    }

    override fun onCreate() {
        super.onCreate()
        mBookList.add(Book("Android疯狂讲义",1))
        mBookList.add(Book("Android开发艺术探索",2))
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder;
    }
}