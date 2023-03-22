package cryogenetics.logistics

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import cryogenetics.logistics.ui.main.MainFragment
import cryogenetics.logistics.ui.sidebar.SidebarFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SidebarFragment.newInstance())
                .commitNow()
        }
    }
}