package pt.cosmik.boostctrl

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentTransaction
import androidx.navigation.findNavController
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import pt.cosmik.boostctrl.ui.info.InfoFragment
import pt.cosmik.boostctrl.ui.matches.MatchesFragment
import pt.cosmik.boostctrl.ui.news.NewsFragment
import pt.cosmik.boostctrl.ui.standings.StandingsFragment
import pt.cosmik.boostctrl.ui.teams.TeamsFragment

class MainActivity : AppCompatActivity() {

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

        supportActionBar?.hide()
        supportActionBar?.setShowHideAnimationEnabled(true)

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
}