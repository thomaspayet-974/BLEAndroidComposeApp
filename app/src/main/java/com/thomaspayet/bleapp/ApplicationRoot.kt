package com.thomaspayet.bleapp
import android.app.Application
import android.content.Context

/**
 * Class that allows to get the context of the application
 * from anywhere in the code
 *
 * To do so, it is enough to call ApplicationRoot.getContext()
 *
 * This class is initialized in the AndroidManifest.xml
 * application android:name=".ApplicationRoot"
 * Android should automatically call the onCreate() method for us
 * to initialize the INSTANCE variable
 */
class ApplicationRoot: Application() {

    companion object {
        private lateinit var INSTANCE: Application

        fun getContext(): Context = INSTANCE.applicationContext
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
    }
}
