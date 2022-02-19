package com.example.android.politicalpreparedness

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.android.politicalpreparedness.network.models.Election


object BindingAdapters {

    /**
     * Use binding adapter to set the recycler view data using livedata object
     */
//    @Suppress("UNCHECKED_CAST")
//    @BindingAdapter("android:liveData")
//    @JvmStatic
//    fun <T> setRecyclerViewData(recyclerView: RecyclerView, items: LiveData<List<T>>?) {
//        items?.value?.let { itemList ->
//            (recyclerView.adapter as? BaseRecyclerViewAdapter<T>)?.apply {
//                clear()
//                addData(itemList)
//            }
//        }
//    }

    /**
     * Use this binding adapter to show and hide the views using boolean variables
     */
    @BindingAdapter("android:fadeVisible")
    @JvmStatic
    fun setFadeVisible(view: View, visible: Boolean? = true) {
        if (view.tag == null) {
            view.tag = true
            view.visibility = if (visible == true) View.VISIBLE else View.GONE
        } else {
            view.animate().cancel()
            if (visible == true) {
                if (view.visibility == View.GONE)
                    view.visibility = View.VISIBLE
            } else {
                if (view.visibility == View.VISIBLE)
                    view.visibility = View.GONE
            }
        }
    }

    @BindingAdapter("setFollowState")
    @JvmStatic
    fun setFollowStateText(view: TextView, savedElection: LiveData<Election?>) {
        if (savedElection?.value != null) {
            view.text = "UnFollow Election"
        } else {
            view.text = "Follow Election"
        }
    }

//    @BindingAdapter("latlng")
//    @JvmStatic
//    fun setLatLng(textView: TextView, rem: ReminderDataItem) {
//        val msg = "Lat ${rem.latitude} Lng ${rem.longitude}"
//        textView.text = msg
//    }
}