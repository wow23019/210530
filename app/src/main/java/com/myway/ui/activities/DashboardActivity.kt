package com.myway.ui.activities

import android.os.Bundle
import android.view.MotionEvent
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.myway.R


/**
 *  Dashboard Screen of the app.
 */
class DashboardActivity : BaseActivity() {
    private val myTouchListeners = ArrayList<MyTouchListener>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        // Update the background color of the action bar as per our design requirement.
        supportActionBar!!.setBackgroundDrawable(
            ContextCompat.getDrawable(
                this@DashboardActivity,
                R.drawable.app_gradient_color_background
            )
        )

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        val navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_dashboard,
                R.id.navigation_orders,
//                R.id.navigation_chat,
                R.id.navigation_live
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)

        navView.setupWithNavController(navController)
    }

    override fun onBackPressed() {
        doubleBackToExit()
    }

    interface MyTouchListener {
        fun onTouchEvent(event: MotionEvent?): Boolean
    }

    fun registerMyTouchListener(listener: MyTouchListener?) {
        myTouchListeners.add(listener!!)
    }

    fun unRegisterMyTouchListener(listener: MyTouchListener?) {
        myTouchListeners.remove(listener)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        for (listener in myTouchListeners) {
            listener.onTouchEvent(ev)
        }
        return super.dispatchTouchEvent(ev)
    }
}