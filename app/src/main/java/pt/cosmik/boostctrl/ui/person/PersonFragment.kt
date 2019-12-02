package pt.cosmik.boostctrl.ui.person

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.viewpagerindicator.LinePageIndicator
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.models.Person
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.ui.common.BoostCtrlSmallViewPagerAdapter
import pt.cosmik.boostctrl.ui.common.KeyValueListAdapter
import pt.cosmik.boostctrl.ui.common.SocialsListAdapter
import pt.cosmik.boostctrl.utils.BoostCtrlAnalytics


class PersonFragment : BaseFragment() {

    private val vm: PersonViewModel by viewModel()

    private var viewPager: ViewPager? = null
    private var linePageIndicator: LinePageIndicator? = null
    private var personDescription: TextView? = null
    private var socialsText: TextView? = null

    private var dividerItemDeco: DividerItemDecoration? = null
    private var recyclerView: RecyclerView? = null
    private val listAdapter = KeyValueListAdapter()
    private var socialsRecyclerView: RecyclerView? = null
    private val socialsAdapter = SocialsListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.view_pager)
        linePageIndicator = view.findViewById(R.id.page_indicator)
        personDescription = view.findViewById(R.id.text_person_desc)
        socialsText = view.findViewById(R.id.text_socials)

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

        socialsAdapter.context = context
        socialsRecyclerView = view.findViewById<RecyclerView>(R.id.socials_recycler_view)?.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(context, 3)
            adapter = socialsAdapter
        }

        disposables.add(socialsAdapter.onItemClickEvent().subscribe {
            vm.processEvent(PersonViewModel.PersonFragmentEvent.SelectedPersonSocial(it))
        })

        vm.viewState.observe(this, Observer {
            it.barTitle?.let { barTitle -> (activity as MainActivity).setActionBarTitle(barTitle) }
            it.personDetailItems?.let { items -> listAdapter.setItems(items) }
            it.personSocialItems?.let { items ->
                if (items.isNotEmpty()) socialsText?.visibility = View.VISIBLE
                socialsAdapter.setItems(items)
            }
            it.personImages?.let { images ->
                if (viewPager?.adapter == null) {
                    viewPager?.adapter = BoostCtrlSmallViewPagerAdapter(context!!, images)
                    linePageIndicator?.setViewPager(viewPager)

                    if (images.isNotEmpty()) viewPager?.visibility = View.VISIBLE
                    if (images.size > 1) linePageIndicator?.visibility = View.VISIBLE
                }
            }
            personDescription?.text = it.personDescription
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is PersonViewModel.PersonFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is PersonViewModel.PersonFragmentViewEffect.OpenActivity -> startActivity(it.intent)
            }
        })

        vm.processEvent(PersonViewModel.PersonFragmentEvent.ViewCreated(arguments?.get("person") as? Person, context))
    }

    override fun onResume() {
        super.onResume()
        vm.viewState.value?.barTitle?.let { BoostCtrlAnalytics.instance.trackScreen("PersonFragment: $it") }
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = context?.getString(R.string.person) ?: ""

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }
}

