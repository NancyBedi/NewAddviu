package com.app.addviu.view.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.app.addviu.AppController
import com.app.addviu.R
import com.app.addviu.model.videoModel.ChannelData
import com.app.addviu.view.activity.ChannelPage
import com.app.addviu.view.activity.MyChannels
import com.app.addviu.view.fragments.MyChannelFragment
import com.app.addviu.view.viewInterface.YesClick
import com.app.naxtre.mvvmfinal.data.helper.Util
import com.nostra13.universalimageloader.core.DisplayImageOptions
import com.nostra13.universalimageloader.core.ImageLoader
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer
import kotlinx.android.synthetic.main.recycle_mychannel_item.view.*


class ChannelListAdapter(
    private val imageLoader: ImageLoader,
    private val mainList: ArrayList<ChannelData>,
    val activity: MyChannelFragment,
    val context: Context
) :
    RecyclerView.Adapter<ChannelListAdapter.ViewHolder>(), YesClick {

    public var channelData = ChannelData()
    private var contactView: View? = null
    var channelId = ""

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val context = parent.context
        val inflater = LayoutInflater.from(context)

        contactView = inflater.inflate(R.layout.recycle_mychannel_item, parent, false)

        return ViewHolder(contactView!!)
    }

    override fun getItemCount(): Int {
        return mainList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = mainList[position]
        if (!data.channelName.isNullOrEmpty()) {
            holder.channelName.text = data.channelName
        }
        if (data.subscribers != null) {
            holder.txtsubscriber.text = "${data.subscribers} Subscribers"
        }
        if (data.no_of_videos != null) {
            holder.txtVideo.text = "${data.no_of_videos} Videos"
        }
        if (data.coverImage.isNullOrEmpty()) {
            imageLoader.displayImage(
                "drawable://" + R.drawable.circle_user,
                holder.channelImage,
                Util.roundProfilePic()
            )
        } else {
            imageLoader.displayImage(data.coverImage, holder.channelImage, Util.roundProfilePic())
        }

        holder.moreIcon.setOnClickListener {
            val popupMenu: PopupMenu = PopupMenu(activity.activity, holder.moreIcon)
            popupMenu.menuInflater.inflate(R.menu.popup_menu, popupMenu.menu)

            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.edit -> {
                        (context as MyChannels).getData(data)
                    }
                    R.id.delete -> {
                        channelId = data.id.toString()
                        activity.showDeleteDialog(
                            context,
                            "Are you really want to delete this channel: ${data.channelName}?",
                            this
                        )
                    }
                }
                true
            })
            popupMenu.show()
        }
    }

    inner class ViewHolder(row: View) : RecyclerView.ViewHolder(row), View.OnClickListener {

        val channelImage = row.channelImage
        val channelName = row.channelName
        val txtsubscriber = row.txtsubscriber
        val txtVideo = row.txtVideo
        val moreIcon = row.moreIcon

        init {
            row.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val data = mainList[adapterPosition]
            val intent = Intent(context, ChannelPage::class.java)
            intent.putExtra("name",data.channelName)
            intent.putExtra("banner",data.banner)
            intent.putExtra("coverImg",data.coverImage)
            intent.putExtra("id",data.id.toString())
            context.startActivity(intent)
        }
    }


    override fun yesClick() {
        AppController.instance?.dataManager?.deleteChannel(
            channelId,
            activity,
            activity.activity
        )
    }
}