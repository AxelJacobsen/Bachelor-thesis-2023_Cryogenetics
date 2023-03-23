package cryogenetics.logistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import cryogenetics.logistics.ui.main.MainFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hides appBar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        // Loads MainFragment on top of activity
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.activityMain, MainFragment.newInstance())
                .commitNow()
        }
    }
}