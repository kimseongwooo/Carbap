package com.softeer.team6four.ui.mypage.reservation.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softeer.team6four.data.remote.reservation.model.ReservationInfoModel
import com.softeer.team6four.data.remote.reservation.model.ReservationTimeModel
import com.softeer.team6four.databinding.ProgressBarItemBinding
import com.softeer.team6four.databinding.ReservationHistoryItemBinding

class ReservationHistoryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val reservationHistoryList = ArrayList<ReservationInfoModel>()

    class ReservationHistoryViewHolder(private val binding: ReservationHistoryItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detailModel: ReservationInfoModel) {
            binding.model = detailModel
        }
    }

    class LoadingViewHolder(binding: ProgressBarItemBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (reservationHistoryList[position].reservationId) {
            0 -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ReservationHistoryItemBinding.inflate(layoutInflater, parent, false)
                ReservationHistoryViewHolder(binding)
            }

            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ProgressBarItemBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is ReservationHistoryViewHolder) {
            holder.bind(reservationHistoryList[position])
        }
    }

    override fun getItemCount(): Int = reservationHistoryList.size

    fun setReservationHistoryList(reservationInfoList: List<ReservationInfoModel>) {
        if (this.reservationHistoryList.isNotEmpty() && this.reservationHistoryList.last().reservationId == 0) {
            this.reservationHistoryList.removeAt(this.reservationHistoryList.size - 1)
        }
        this.reservationHistoryList.addAll(reservationInfoList)
        notifyDataSetChanged()
    }

    fun removeLoadingFooter() {
        if (this.reservationHistoryList.isNotEmpty() && this.reservationHistoryList.last().reservationId == 0) {
            this.reservationHistoryList.removeAt(this.reservationHistoryList.size - 1)
            notifyItemRemoved(this.reservationHistoryList.size)
        }
    }

    fun setProgressbar() {

        reservationHistoryList.add(
            ReservationInfoModel(
                "", "", "", 0, ReservationTimeModel("", ""),
                "", "", 0
            )
        )
        notifyDataSetChanged()
        Log.d("setProgressbar", "true")
    }


    fun getLastReservationHistoryId(): Int? {
        return reservationHistoryList.lastOrNull()?.reservationId
    }

    fun clearReservationHistoryList() {
        reservationHistoryList.clear()
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}