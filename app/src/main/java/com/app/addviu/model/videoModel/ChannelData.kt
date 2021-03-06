package com.app.addviu.model.videoModel

import com.google.gson.annotations.Expose

import com.google.gson.annotations.SerializedName


class ChannelData {

    @SerializedName("id")
    @Expose
    var id: Int = 0

    @SerializedName("user_id")
    @Expose
    var userId: Int = 0

    @SerializedName("channel_name")
    @Expose
    var channelName: String = ""

    @SerializedName("slug")
    @Expose
    var slug: String = ""

    @SerializedName("description")
    @Expose
    var description: String = ""

    @SerializedName("banner")
    @Expose
    var banner: String = ""

    @SerializedName("cover_image")
    @Expose
    var coverImage: String = ""

    @SerializedName("category_id")
    @Expose
    var categoryId: Int = 0

    @SerializedName("category_name")
    @Expose
    var category_name: String = ""

    @SerializedName("default_channel")
    @Expose
    var defaultChannel: Int = 0

    @SerializedName("subscribers")
    @Expose
    var subscribers: Int = 0

    @SerializedName("no_of_videos")
    @Expose
    var no_of_videos: Int = 0

    @SerializedName("created_at")
    @Expose
    var createdAt: String = ""

    @SerializedName("updated_at")
    @Expose
    var updatedAt: String = ""
}