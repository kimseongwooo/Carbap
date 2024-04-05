package com.softeer.team6four.ui.mypage.charger.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.softeer.team6four.data.remote.reservation.model.ChargerReservationInfoModel
import com.softeer.team6four.data.remote.reservation.model.ReservationDetail
import com.softeer.team6four.databinding.MyChargerReservationItemBinding
import com.softeer.team6four.databinding.ProgressBarItemBinding


class ChargerReservationAdapter(private val navigationCallback: (reservationDetail: ReservationDetail) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private val chargerReservationInfoList = ArrayList<ChargerReservationInfoModel>()

    inner class ChargerReservationViewHolder(private val binding: MyChargerReservationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(detailModel: ChargerReservationInfoModel) {
            with(binding) {
                model = detailModel
                root.setOnClickListener {
                    navigationCallback(
                        ReservationDetail(
                            detailModel.reservationId,
                            detailModel.guestNickname,
                            detailModel.rentalDate,
                            detailModel.rentalTime,
                            detailModel.totalFee
                        )
                    )
                }
            }
        }
    }

    inner class LoadingViewHolder(binding: ProgressBarItemBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemViewType(position: Int): Int {
        return when (chargerReservationInfoList[position].reservationId) {
            0 -> VIEW_TYPE_LOADING
            else -> VIEW_TYPE_ITEM
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_ITEM -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = MyChargerReservationItemBinding.inflate(layoutInflater, parent, false)
                ChargerReservationViewHolder(binding)
            }

            else -> {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ProgressBarItemBinding.inflate(layoutInflater, parent, false)
                LoadingViewHolder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChargerReservationViewHolder -> {
                holder.bind(chargerReservationInfoList[position])
            }
        }
    }

    override fun getItemCount(): Int {
        return chargerReservationInfoList.size
    }

    fun setChargerReservationList(chargerReservationList: List<ChargerReservationInfoModel>) {
        if (chargerReservationInfoList.isNotEmpty() && chargerReservationInfoList.last().reservationId == 0) {
            chargerReservationInfoList.removeAt(chargerReservationInfoList.size - 1)
        }
        chargerReservationInfoList.addAll(chargerReservationList)
        notifyDataSetChanged()
    }

    fun removeLoadingFooter() {
        if (chargerReservationInfoList.isNotEmpty() && chargerReservationInfoList.last().reservationId == 0) {
            chargerReservationInfoList.removeAt(chargerReservationInfoList.size - 1)
            notifyItemRemoved(chargerReservationInfoList.size)
        }
    }

    fun clearChargerReservationList() {
        chargerReservationInfoList.clear()
        notifyDataSetChanged()
    }

    companion object {
        private const val VIEW_TYPE_ITEM = 0
        private const val VIEW_TYPE_LOADING = 1
    }
}
