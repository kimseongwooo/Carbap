package com.softeer.team6four.ui.mypage.reservation

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.softeer.team6four.R
import com.softeer.team6four.data.Resource
import com.softeer.team6four.data.remote.reservation.model.ReservationInfoListModel
import com.softeer.team6four.databinding.FragmentMyReservationBinding
import com.softeer.team6four.ui.mypage.reservation.adapter.ReservationHistoryAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MyReservationFragment : Fragment() {
    private var _binding: FragmentMyReservationBinding? = null
    private val myReservationViewModel: MyReservationViewModel by viewModels()
    private val binding
        get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyReservationBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            viewModel = myReservationViewModel
            lifecycleOwner = viewLifecycleOwner

            ibBack.setOnClickListener {
                findNavController().popBackStack()
            }

            val adapter = ReservationHistoryAdapter()
            binding.rvReservationList.adapter = adapter

            myReservationViewModel.fetchMyReservationHistory()
            binding.rvReservationList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)

                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                    val itemTotalCount = recyclerView.adapter!!.itemCount - 1

                    if (!binding.rvReservationList.canScrollVertically(1)
                        && lastVisibleItemPosition == itemTotalCount
                        && myReservationViewModel.myReservationHistory.value is Resource.Success
                    ) {
                        if ((myReservationViewModel.myReservationHistory.value
                                    as Resource.Success<ReservationInfoListModel>).data.hasNext
                        ) {
                            myReservationViewModel.fetchMyReservationHistory(
                                adapter.getLastReservationHistoryId()
                            )
                        }
                    }
                }
            })

            reservationStateChipGroup.setOnCheckedStateChangeListener { _, checkedIds ->
                val checkedId = checkedIds[0]
                myReservationViewModel.updateSortType(
                    when (checkedId) {
                        R.id.btn_reservation_wait -> "WAIT"
                        R.id.btn_reservation_approve -> "APPROVE"
                        R.id.btn_reservation_reject -> "REJECT"
                        else -> "WAIT"
                    }
                )
                if (myReservationViewModel.myReservationHistory.value is Resource.Success) {
                    adapter.clearReservationHistoryList()
                }
                myReservationViewModel.fetchMyReservationHistory()
            }

            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    myReservationViewModel.myReservationHistory.collectLatest { reservationHistoryList ->
                        Log.d("reservationHistory", reservationHistoryList.toString())
                        when (reservationHistoryList) {
                            is Resource.Success -> {
                                val list = reservationHistoryList.data.content
                                adapter.removeLoadingFooter()
                                adapter.setReservationHistoryList(list)
                                if (adapter.itemCount == 0) {
                                    binding.ivEmptyState.visibility = View.VISIBLE
                                    binding.tvEmptyPointHistory.visibility = View.VISIBLE
                                } else {
                                    binding.ivEmptyState.visibility = View.INVISIBLE
                                    binding.tvEmptyPointHistory.visibility = View.INVISIBLE
                                }
                            }

                            is Resource.Loading -> {
                                adapter.setProgressbar()
                                binding.ivEmptyState.visibility = View.INVISIBLE
                                binding.tvEmptyPointHistory.visibility = View.INVISIBLE
                            }

                            is Resource.Error -> {
                                Log.e("ReservationHistory", reservationHistoryList.message)
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}