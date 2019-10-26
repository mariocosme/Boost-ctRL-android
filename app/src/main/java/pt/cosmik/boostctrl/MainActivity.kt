package pt.cosmik.boostctrl

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.snackbar.Snackbar
import pt.cosmik.boostctrl.ui.info.InfoFragment
import pt.cosmik.boostctrl.ui.matches.MatchesFragment
import pt.cosmik.boostctrl.ui.news.NewsFragment
import pt.cosmik.boostctrl.ui.standings.StandingsFragment
import pt.cosmik.boostctrl.ui.teams.TeamsFragment

class MainActivity : AppCompatActivity() {

    private var appActionBar: Toolbar? = null
    private var coordinatorLayout: CoordinatorLayout? = null

    private val mainFragments = hashMapOf(
        R.id.navigation_teams to TeamsFragment(),
        R.id.navigation_matches to MatchesFragment(),
        R.id.navigation_news to NewsFragment(),
        R.id.navigation_standings to StandingsFragment(),
        R.id.navigation_info to InfoFragment()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        coordinatorLayout = findViewById(R.id.coordinator_layout)

        appActionBar = findViewById<Toolbar>(R.id.action_bar)?.apply {
            setSupportActionBar(this)
        }

        val navController = findNavController(R.id.nav_host_fragment)
        setupActionBarWithNavController(navController, null)
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            // TODO: hide show supportActionBar based on the destination?
        }

        findViewById<BottomNavigationView>(R.id.nav_view)?.apply {
            setupWithNavController(navController)
            setOnNavigationItemSelectedListener {
                mainFragments[it.itemId]?.let { fragment ->
                    val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                    transaction.replace(R.id.nav_host_fragment, fragment)
                    transaction.disallowAddToBackStack()
                    transaction.commit()
                }
                true
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
}