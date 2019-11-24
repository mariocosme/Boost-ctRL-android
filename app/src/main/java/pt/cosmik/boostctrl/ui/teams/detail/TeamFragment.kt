package pt.cosmik.boostctrl.ui.teams.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.viewpagerindicator.LinePageIndicator
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Team
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.BoostCtrlSmallViewPagerAdapter
import pt.cosmik.boostctrl.ui.person.PersonFragmentDirections


class TeamFragment : BaseFragment() {

    private val vm: TeamViewModel by viewModel()

    private var loadingBar: ProgressBar? = null
    private var viewPager: ViewPager? = null
    private var linePageIndicator: LinePageIndicator? = null
    private var teamDescription: TextView? = null

    private var dividerItemDeco: DividerItemDecoration? = null
    private var teamGeneralDetailsRecyclerView: RecyclerView? = null
    private val teamGeneralDetailsListAdapter = TeamGeneralDetailsListAdapter()
    private var teamRosterRecyclerView: RecyclerView? = null
    private val teamRosterListAdapter = TeamRosterListAdapter(null)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_team_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)
        viewPager = view.findViewById(R.id.view_pager)
        linePageIndicator = view.findViewById(R.id.page_indicator)
        teamDescription = view.findViewById(R.id.text_team_desc)

        dividerItemDeco = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.bg_list_news_item_separator)?.let { dividerItemDeco?.setDrawable(it) }
        }

        teamGeneralDetailsRecyclerView = view.findViewById<RecyclerView>(R.id.team_general_details_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = teamGeneralDetailsListAdapter
        }

        teamRosterListAdapter.context = context
        teamRosterRecyclerView = view.findViewById<RecyclerView>(R.id.team_roster_recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = teamRosterListAdapter
        }

        disposables.add(teamRosterListAdapter.itemClickSubject.subscribe {
            vm.processEvent(TeamViewModel.TeamFragmentEvent.SelectedRosterItem(it))
        })

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            it.barTitle?.let { barTitle -> (activity as MainActivity).setActionBarTitle(barTitle) }
            it.teamGeneralDetailItems?.let { items -> teamGeneralDetailsListAdapter.setItems(items) }
            it.teamRosterPlayerItems?.let { items -> teamRosterListAdapter.setItems(items) }
            it.teamImages?.let { images ->
                if (viewPager?.adapter == null) {
                    viewPager?.adapter = BoostCtrlSmallViewPagerAdapter(context!!, images)
                    linePageIndicator?.setViewPager(viewPager)

                    if (images.isNotEmpty()) viewPager?.visibility = View.VISIBLE
                    if (images.size > 1) linePageIndicator?.visibility = View.VISIBLE
                }
            }
            teamDescription?.text = it.teamDescription
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is TeamViewModel.TeamFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is TeamViewModel.TeamFragmentViewEffect.PresentPersonFragment -> findNavController().navigate(PersonFragmentDirections.actionGlobalPersonFragment(it.person))
            }
        })

        vm.processEvent(TeamViewModel.TeamFragmentEvent.ViewCreated(arguments?.get("team") as? Team, context))
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.person) ?: ""
}

