package pt.cosmik.boostctrl.ui.person

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
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


class PersonFragment : BaseFragment() {

    private val vm: PersonViewModel by viewModel()

    private var viewPager: ViewPager? = null
    private var linePageIndicator: LinePageIndicator? = null
    private var personDescription: TextView? = null

    private var dividerItemDeco: DividerItemDecoration? = null
    private var recyclerView: RecyclerView? = null
    private val listAdapter = PersonListAdapter()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?  ): View? {
        return inflater.inflate(R.layout.fragment_person, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewPager = view.findViewById(R.id.view_pager)
        linePageIndicator = view.findViewById(R.id.page_indicator)
        personDescription = view.findViewById(R.id.text_person_desc)

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

        vm.viewState.observe(this, Observer {
            it.barTitle?.let { barTitle -> (activity as MainActivity).setActionBarTitle(barTitle) }
            it.personDetailItems?.let { items -> listAdapter.setItems(items) }
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
//            when (it) {}
        })

        vm.processEvent(PersonViewModel.PersonFragmentEvent.ViewCreated(arguments?.get("person") as? Person, context))
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

