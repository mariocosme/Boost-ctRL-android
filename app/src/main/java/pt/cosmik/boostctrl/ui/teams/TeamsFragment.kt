package pt.cosmik.boostctrl.ui.teams

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.teams.detail.TeamFragmentDirections

class TeamsFragment : BaseFragment() {

    private val vm: TeamsViewModel by viewModel()

    private var loadingBar: ProgressBar? = null

    private var recyclerView: RecyclerView? = null
    private val listAdapter = TeamsListAdapter(null)
    private var dividerItemDeco: DividerItemDecoration? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_teams, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)

        dividerItemDeco = DividerItemDecoration(context, LinearLayoutManager.VERTICAL)
        context?.let { context ->
            ContextCompat.getDrawable(context, R.drawable.bg_list_news_item_separator)?.let { dividerItemDeco?.setDrawable(it) }
        }

        listAdapter.context = context
        recyclerView = view.findViewById<RecyclerView>(R.id.recycler_view)?.apply {
            setHasFixedSize(true)
            dividerItemDeco?.let { addItemDecoration(it) }
            layoutManager = LinearLayoutManager(context)
            adapter = listAdapter
        }

        disposables.add(listAdapter.itemClickSubject.subscribe {
            vm.processEvent(TeamsViewModel.TeamsFragmentEvent.DidSelectTeam(it))
        })

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
            listAdapter.setTeamItems(it.teams)
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is TeamsViewModel.TeamsFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is TeamsViewModel.TeamsFragmentViewEffect.PresentTeamFragment -> findNavController().navigate(TeamFragmentDirections.actionGlobalTeamDetailFragment(it.team))
            }
        })

        vm.processEvent(TeamsViewModel.TeamsFragmentEvent.ViewCreated)
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.teams) ?: ""

    override fun onResume() {
        super.onResume()
        (activity as MainActivity).setActionBarTitle(getActionBarTitle())
    }

}