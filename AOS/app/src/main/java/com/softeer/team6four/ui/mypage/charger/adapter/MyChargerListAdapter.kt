package com.softeer.team6four.ui.mypage.charger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softeer.team6four.data.remote.charger.model.MyChargerSimpleInfoModel
import com.softeer.team6four.databinding.MyChargerItemBinding
import com.softeer.team6four.databinding.ProgressBarItemBinding

class MyChargerListAdapter(private val navigationCallback: (idAndNickname: Pair<Int, String>) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val myChargerList = ArrayList<MyChargerSimpleInfoModel>()

    inner class MyChargerViewHolder(private val binding: MyChargerItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detailModel: MyChargerSimpleInfoModel) {
            binding.model = detailModel
            binding.root.setOnClickListener {
                navigationCallback(Pair(detailModel.carbobId, detailModel.nickname))
            }
        }
    }

    inner class LoadingViewHolder(binding: ProgressBarItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (myChargerList[position].carbobId) {
            0 -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MyChargerItemBinding.inflate(layoutInflater, parent, false)
                MyChargerViewHolder(binding)
            }

            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ProgressBarItemBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is MyChargerViewHolder) {
            holder.bind(myChargerList[position])
        }
    }

    override fun getItemCount(): Int = myChargerList.size

    fun setMyChargerList(myChargerList: List<MyChargerSimpleInfoModel>) {
        if (this.myChargerList.isNotEmpty() && this.myChargerList.last().carbobId == 0) {
            this.myChargerList.removeAt(this.myChargerList.size - 1)
        }
        this.myChargerList.addAll(myChargerList)
        notifyDataSetChanged()
    }

    fun removeLoadingFooter() {
        if (this.myChargerList.isNotEmpty() && this.myChargerList.last().carbobId == 0) {
            this.myChargerList.removeAt(this.myChargerList.size - 1)
            notifyItemRemoved(this.myChargerList.size)
        }
    }

    fun getLastChargerIdAndReservationCount(): Pair<Int?, Int?> {
        return Pair(
            myChargerList.lastOrNull()?.carbobId,
            myChargerList.lastOrNull()?.reservationCount
        )
    }

    fun clearMyChargerList() {
        myChargerList.clear()
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}
