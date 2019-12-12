package pt.cosmik.boostctrl.external.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import com.jakewharton.rxbinding3.view.clicks
import org.koin.android.viewmodel.ext.android.viewModel
import pt.cosmik.boostctrl.BoostCtrlApplication
import pt.cosmik.boostctrl.MainActivity
import pt.cosmik.boostctrl.R
import pt.cosmik.boostctrl.external.adapter.BracketsSectionAdapter
import pt.cosmik.boostctrl.external.model.ColumnData
import pt.cosmik.boostctrl.ui.common.BaseFragment
import pt.cosmik.boostctrl.utils.Constants
import java.util.concurrent.TimeUnit

class BracketsFragment: BaseFragment(), ViewPager.OnPageChangeListener {

    private val vm: BracketsViewModel by viewModel()

    private var viewPager: ViewPager? = null
    private var sectionAdapter: BracketsSectionAdapter? = null
    private var sectionList = arrayListOf<ColumnData>()
    private var mNextSelectedScreen = 0

    private var loadingBar: ProgressBar? = null
    private var containerButton: Button? = null
    private var sectionButton: Button? = null

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
        containerButton = view.findViewById<Button>(R.id.container_button)?.apply {
            disposables.add(clicks().throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS).subscribe {
                vm.processEvent(BracketsViewModel.BracketsFragmentEvent.SelectedChangeContainerButton)
            })
        }
        sectionButton = view.findViewById<Button>(R.id.section_button)?.apply {
            disposables.add(clicks().throttleFirst(Constants.THROTTLE_SINGLE_CLICK_MILLISECONDS, TimeUnit.MILLISECONDS).subscribe {
                vm.processEvent(BracketsViewModel.BracketsFragmentEvent.SelectedChangeSectionButton)
            })
        }

        vm.viewState.observe(this, Observer {
            if (it.isLoading) {
                loadingBar?.visibility = View.VISIBLE
                containerButton?.isEnabled = false
                sectionButton?.isEnabled = false
            }
            else {
                loadingBar?.visibility = View.GONE
                containerButton?.isEnabled = true
                sectionButton?.isEnabled = true
            }
        })

        vm.viewEffect.observe(this, Observer {
            when (it) {
                is BracketsViewModel.BracketsFragmentViewEffect.ShowError -> showErrorMessage(it.error)
                is BracketsViewModel.BracketsFragmentViewEffect.ShowBrackets -> {
                    sectionList = it.brackets
                    initialiseViewPagerAdapter()
                }
                BracketsViewModel.BracketsFragmentViewEffect.ShowNoBracketsAvailable -> {
                    showErrorMessage("There are no brackets available for this competition")
                    findNavController().popBackStack()
                }
                is BracketsViewModel.BracketsFragmentViewEffect.ShowDialogContainerPicker -> {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(context?.getString(R.string.select))
                        .setItems(it.containers.toTypedArray()) { _, which ->
                            containerButton?.text = it.containers[which]
                            sectionButton?.text = context?.getString(R.string.chose_section)
                            vm.processEvent(BracketsViewModel.BracketsFragmentEvent.SelectedContainer(it.containers[which]))
                        }
                    builder.create().show()

                }
                is BracketsViewModel.BracketsFragmentViewEffect.ShowDialogSectionPicker -> {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle(context?.getString(R.string.select))
                        .setItems(it.sections.toTypedArray()) { _, which ->
                            sectionButton?.text = it.sections[which]
                            vm.processEvent(BracketsViewModel.BracketsFragmentEvent.SelectedSection(it.sections[which]))
                        }
                    builder.create().show()
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
        if (sectionAdapter != null) {
            viewPager!!.adapter = BracketsSectionAdapter(childFragmentManager, sectionList)
        }
        else {
            sectionAdapter = BracketsSectionAdapter(childFragmentManager, sectionList)
            viewPager!!.offscreenPageLimit = 10
            viewPager!!.adapter = sectionAdapter
            viewPager!!.currentItem = 0
            viewPager!!.pageMargin = -200
            viewPager!!.isHorizontalFadingEdgeEnabled = true
            viewPager!!.setFadingEdgeLength(50)
            viewPager!!.addOnPageChangeListener(this)
        }
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