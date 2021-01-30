package com.mortarifabio.desafiofirebase.model

import android.os.Parcelable
import androidx.recyclerview.widget.DiffUtil
import kotlinx.parcelize.Parcelize

@Parcelize
data class Game(
    val id: String? = null,
    val name: String,
    val createdAt: String,
    val description: String,
    var image: String? = null
) : Parcelable {
    companion object {
        var DIFF_CALLBACK = object : DiffUtil.ItemCallback<Game>() {
            override fun areItemsTheSame(oldItem: Game, newItem: Game) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: Game, newItem: Game) = oldItem.id == newItem.id
        }
    }
}
