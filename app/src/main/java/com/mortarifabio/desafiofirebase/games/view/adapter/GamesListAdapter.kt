package com.mortarifabio.desafiofirebase.games.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.analytics.ktx.analytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.mortarifabio.desafiofirebase.GlideApp
import com.mortarifabio.desafiofirebase.R
import com.mortarifabio.desafiofirebase.databinding.ItemGamesListBinding
import com.mortarifabio.desafiofirebase.model.Game
import com.mortarifabio.desafiofirebase.model.Game.Companion.DIFF_CALLBACK
import com.mortarifabio.desafiofirebase.utils.Constants.Analytics.ANALYTICS_GAME_DETAILS_EVENT

class GamesListAdapter(
    private val onItemClicked: (Game?) -> Unit
) : PagedListAdapter<Game, GamesListAdapter.ViewHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemGamesListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position), onItemClicked)
    }

    class ViewHolder(
        private val binding: ItemGamesListBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        private val storageRef by lazy {
            Firebase.storage.reference
        }
        private val analytics by lazy {
            Firebase.analytics
        }
        fun bind(game: Game?, onItemClicked: (Game?) -> Unit) = with(binding) {
            game?.let {
                val imgRef = it.image?.let { image ->storageRef.child(image) }
                GlideApp.with(itemView.context)
                    .load(imgRef)
                    .placeholder(R.drawable.bg_placeholder)
                    .centerCrop()
                    .into(ivGamesListImage)
                tvGamesListName.text = it.name
                tvGamesListYear.text = it.createdAt
                cvGamesListCard.setOnClickListener {
                    analytics.logEvent(ANALYTICS_GAME_DETAILS_EVENT, null)
                    onItemClicked(game)
                }
            }
        }
    }
}