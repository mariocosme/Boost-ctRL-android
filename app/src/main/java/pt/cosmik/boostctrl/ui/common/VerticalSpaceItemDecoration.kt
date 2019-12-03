package pt.cosmik.boostctrl.ui.common

import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class VerticalSpaceItemDecoration: RecyclerView.ItemDecoration() {

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        parent.adapter?.itemCount?.let {
            if (parent.getChildAdapterPosition(view) != it - 1) {
                outRect.bottom = 5
            }
        }
    }

}