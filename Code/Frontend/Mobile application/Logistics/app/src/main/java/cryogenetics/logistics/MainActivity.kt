package cryogenetics.logistics

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Window
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.preferencesDataStore
import cryogenetics.logistics.ui.host.HostFragment
import cryogenetics.logistics.ui.login.LoginFragment
import androidx.datastore.preferences.core.Preferences

// Set up data store/preferences
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Hides appBar
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE)

        // Loads MainFragment on top of activity
        setContentView(R.layout.activity_main)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.activityMain, LoginFragment())
                .commitNow()
        }
    }
}