package pt.cosmik.boostctrl

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar

class MainActivity : AppCompatActivity() {

    private var appActionBar: Toolbar? = null
    private var coordinatorLayout: CoordinatorLayout? = null

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
            R.id.navigation_standings,
            R.id.navigation_info
        )).build()

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, appBarConfiguration)

        findViewById<BottomNavigationView>(R.id.nav_view)?.apply {
            NavigationUI.setupWithNavController(this, navController)
            setupWithNavController(navController)
        }

        val graph = navController.navInflater.inflate(R.navigation.nav_graph)
        graph.startDestination = R.id.navigation_news
        navController.graph = graph
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
}