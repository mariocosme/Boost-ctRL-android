package pt.cosmik.boostctrl.external.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.BoostCtrlApplication
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.external.adapter.BracketsSectionAdapter
import pt.cosmik.boostctrl.external.model.ColumnData
import pt.cosmik.boostctrl.ui.common.BaseFragment

class BracketsFragment: BaseFragment(), ViewPager.OnPageChangeListener {

    private val vm: BracketsViewModel by viewModel()

    private var viewPager: ViewPager? = null
    private var sectionAdapter: BracketsSectionAdapter? = null
    private var sectionList = arrayListOf<ColumnData>()
    private var mNextSelectedScreen = 0

    private var loadingBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_brackets, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadingBar = view.findViewById(R.id.loading_bar)

        vm.viewState.observe(this, Observer {
            loadingBar?.visibility = if (it.isLoading) View.VISIBLE else View.GONE
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is BracketsViewModel.BracketsFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is BracketsViewModel.BracketsFragmentViewEffect.ShowBrackets -> {
                    sectionList.addAll(it.brackets)
                    initialiseViewPagerAdapter()
                }
            }
        })

        vm.processEvent(BracketsViewModel.BracketsFragmentEvent.ViewCreated(arguments?.get("competitionId") as? String))
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        initViews()
    }

    private fun initialiseViewPagerAdapter() {
        sectionAdapter = BracketsSectionAdapter(childFragmentManager, sectionList)
        viewPager!!.offscreenPageLimit = 10
        viewPager!!.adapter = sectionAdapter
        viewPager!!.currentItem = 0
        viewPager!!.pageMargin = -200
        viewPager!!.isHorizontalFadingEdgeEnabled = true
        viewPager!!.setFadingEdgeLength(50)
        viewPager!!.addOnPageChangeListener(this)
    }

    private fun initViews() {
        viewPager = view!!.findViewById(R.id.container)
    }

    override fun showErrorMessage(message: String) {
        (activity as MainActivity).showMessageInSnackBar(message)
    }

    override fun getActionBarTitle(): String = "title"

    override fun onDestroyView() {
        super.onDestroyView()
        removeObservers()
    }

    override fun removeObservers() {
        vm.viewState.removeObservers(this)
        vm.viewEffect.removeObservers(this)
    }

    override fun onPageScrolled(
        position: Int,
        positionOffset: Float,
        positionOffsetPixels: Int
    ) {
        if (positionOffset > 0.5) { // Closer to next screen than to current
            if (position + 1 != mNextSelectedScreen) {
                mNextSelectedScreen = position + 1
                //update view here
                if (getBracketsFragment(position)!!.columnList[0].height
                    != (activity!!.application as BoostCtrlApplication).dpToPx(
                        131
                    )
                ) getBracketsFragment(position)!!.shrinkView(
                    (activity!!.application as BoostCtrlApplication).dpToPx(
                        131
                    )
                )
                if (getBracketsFragment(position + 1)!!.columnList[0].height
                    != (activity!!.application as BoostCtrlApplication).dpToPx(
                        131
                    )
                ) getBracketsFragment(position + 1)!!.shrinkView(
                    (activity!!.application as BoostCtrlApplication).dpToPx(
                        131
                    )
                )
            }
        } else { // Closer to current screen than to next
            if (position != mNextSelectedScreen) {
                mNextSelectedScreen = position
                //update View here
                if (getBracketsFragment(position + 1)!!.currentBracketSize ==
                    getBracketsFragment(position + 1)!!.previousBracketSize
                ) {
                    getBracketsFragment(position + 1)!!.shrinkView(
                        (activity!!.application as BoostCtrlApplication).dpToPx(
                            131
                        )
                    )
                    getBracketsFragment(position)!!.shrinkView(
                        (activity!!.application as BoostCtrlApplication).dpToPx(
                            131
                        )
                    )
                } else {
                    val currentFragmentSize: Int =
                        getBracketsFragment(position + 1)!!.currentBracketSize
                    val previousFragmentSize: Int =
                        getBracketsFragment(position + 1)!!.previousBracketSize
                    if (currentFragmentSize != previousFragmentSize) {
                        getBracketsFragment(position + 1)!!.expandHeight(
                            (activity!!.application as BoostCtrlApplication).dpToPx(
                                262
                            )
                        )
                        getBracketsFragment(position)!!.shrinkView(
                            (activity!!.application as BoostCtrlApplication).dpToPx(
                                131
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onPageSelected(position: Int) {}

    override fun onPageScrollStateChanged(state: Int) {}

    private fun getBracketsFragment(position: Int): BracketsColumnFragment? {
        var bracketsFragment: BracketsColumnFragment? = null
        val fragments = childFragmentManager.fragments
        for (fragment in fragments) {
            if (fragment is BracketsColumnFragment) {
                bracketsFragment = fragment
                if (bracketsFragment.sectionNumber == position) break
            }
        }
        return bracketsFragment
    }
}