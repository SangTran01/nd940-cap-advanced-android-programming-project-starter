package com.example.android.politicalpreparedness

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import com.bumptech.glide.Glide
import com.example.android.politicalpreparedness.network.models.Channel
import com.example.android.politicalpreparedness.network.models.Election


object BindingAdapters {

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

    @BindingAdapter(value = ["app:url", "app:context"])
    @JvmStatic
    fun setOfficialImage(imageView: ImageView, url: String?, context: Context) {
        Glide
            .with(context)
            .load(url)
            .centerCrop()
            .error(R.drawable.ic_profile)
            .placeholder(R.drawable.ic_profile)
            .into(imageView);
    }

    @BindingAdapter(value = ["app:channels", "app:context"])
    @JvmStatic
    fun showSocialMediaUrl(imageView: ImageView, channels: List<Channel>?, context: Context) {
        val id = imageView.id

        if (id == R.id.imageView_fb) {
            val channel = channels?.find { it.type == "Facebook" }
            if (channel != null) {
                val url = "https://www.facebook.com/${channel.id}"
                imageView.setImageResource(R.drawable.ic_facebook)
                imageView.setOnClickListener {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(browserIntent)
                }
            } else {
                imageView.visibility = View.INVISIBLE
            }
        } else if (id == R.id.imageView_twitter) {
            val channel = channels?.find { it.type == "Twitter" }
            if (channel != null) {
                val url = "https://twitter.com/${channel.id}"
                imageView.setImageResource(R.drawable.ic_twitter)
                imageView.setOnClickListener {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(browserIntent)
                }
            } else {
                imageView.visibility = View.INVISIBLE
            }
        } else if (id == R.id.imageView_www) {
            val channel = channels?.find { it.type == "Facebook" }
            if (channel != null) {
                val url = "https://www.facebook.com/${channel.id}"
                imageView.setImageResource(R.drawable.ic_facebook)
                imageView.setOnClickListener {
                    val browserIntent =
                        Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    context.startActivity(browserIntent)
                }
            } else {
                imageView.visibility = View.INVISIBLE
            }
        }
    }

    @BindingAdapter(value = ["app:urls", "app:context"])
    @JvmStatic
    fun showWWWUrl(imageView: ImageView, urls: List<String>?, context: Context) {
        val url = urls?.first()
        if (url != null) {
            imageView.setImageResource(R.drawable.ic_www)
            imageView.setOnClickListener {
                val browserIntent =
                    Intent(Intent.ACTION_VIEW, Uri.parse(url))
                context.startActivity(browserIntent)
            }
        } else {
            imageView.visibility = View.INVISIBLE
        }
    }
}