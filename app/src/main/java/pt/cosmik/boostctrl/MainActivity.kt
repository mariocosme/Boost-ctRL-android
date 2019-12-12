package pt.cosmik.boostctrl

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.messaging.FirebaseMessaging
import pt.cosmik.boostctrl.ui.news.detail.NewsDetailFragmentDirections
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics
import pt.cosmik.boostctrl.utils.Constants

class MainActivity : AppCompatActivity() {

    private var appActionBar: Toolbar? = null
    private var coordinatorLayout: CoordinatorLayout? = null
    private lateinit var navController: NavController

    private lateinit var firebaseAnalytics: FirebaseAnalytics

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coordinatorLayout = findViewById(R.id.coordinator_layout)

        appActionBar = findViewById<Toolbar>(R.id.action_bar)?.apply {
            setSupportActionBar(this)
            setTitleTextColor(ContextCompat.getColor(context, R.color.colorCloudWhite))
        }

        val appBarConfiguration = AppBarConfiguration.Builder(setOf(
            R.id.navigation_teams,
            R.id.navigation_matches,
            R.id.navigation_news,
            R.id.navigation_competitions,
            R.id.navigation_info
        )).build()

        navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)

        findViewById<BottomNavigationView>(R.id.nav_view)?.apply {
            NavigationUI.setupWithNavController(this, navController)
            setupWithNavController(navController)
        }

        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.startDestination = R.id.navigation_news
        navController.graph = graph

        // Firebase
        firebaseAnalytics = FirebaseAnalytics.getInstance(this)
        BoostCtrlAnalytics.instance.initWith(firebaseAnalytics, this)
        FirebaseMessaging.getInstance().subscribeToTopic(getString(R.string.fcm_topic))
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "FCM Topic subscription failed.")
                }
                else {
                    if (BuildConfig.DEBUG) Log.d(Constants.LOG_TAG, "FCM Subscribed to topic with success.")
                }
            }
    }

    override fun onResume() {
        super.onResume()
        BoostCtrlAnalytics.instance.trackScreen("MainActivity")
        intent.extras?.let {
            if (it.keySet().contains(Constants.FCM_NEWS_ITEM)) {
                val newsItemId = it.get(Constants.FCM_NEWS_ITEM) as String
                navController.navigate(NewsDetailFragmentDirections.actionGlobalNewsItemDetailFragment(null, newsItemId))

                // TODO: Does this fix the issue when we open the app through a news article?
                it.remove(Constants.FCM_NEWS_ITEM)
            }
        }
    }

    fun setActionBarTitle(title: String) {
        appActionBar?.title = title
    }

    fun setActionBarVisible(visible: Boolean) {
        appActionBar?.visibility = if (visible) View.VISIBLE else View.GONE
    }

    fun showMessageInSnackBar(message: String) {
        coordinatorLayout?.let { Snackbar.make(it, message, Snackbar.LENGTH_LONG).show() }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) onBackPressed()
        return super.onOptionsItemSelected(item)
    }

    fun getFirebaseAnalytics(): FirebaseAnalytics = this.firebaseAnalytics
}