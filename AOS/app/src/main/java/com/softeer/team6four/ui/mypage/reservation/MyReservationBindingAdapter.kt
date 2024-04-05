package com.softeer.team6four.ui.mypage.reservation

import android.widget.ImageView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.google.android.material.chip.Chip
import com.softeer.team6four.R

@BindingAdapter("setChipText")
fun setChipText(chip: Chip, state: String) {
    chip.text = when (state) {
        "WAIT" -> "대기중"
        "APPROVE" -> "승인"
        "USED" -> "사용완료"
        else -> "거절"
    }
}

@BindingAdapter("setChipBackgroundColor")
fun setChipBackgroundColor(chip: Chip, state: String) {
    chip.chipBackgroundColor = when (state) {
        "WAIT" -> ContextCompat.getColorStateList(chip.context, R.color.gray_050)
        "APPROVE" -> ContextCompat.getColorStateList(chip.context, R.color.main_400)
        "USED" -> ContextCompat.getColorStateList(chip.context, R.color.gray_050)
        else -> ContextCompat.getColorStateList(chip.context, R.color.red)
    }
}

@BindingAdapter("setChipTextColor")
fun setChipTextColor(chip: Chip, state: String) {
    chip.setTextColor(
        when (state) {
            "WAIT" -> ContextCompat.getColor(chip.context, R.color.gray_800)
            "APPROVE" -> ContextCompat.getColor(chip.context, R.color.gray_000)
            "USED" -> ContextCompat.getColor(chip.context, R.color.gray_800)
            else -> ContextCompat.getColor(chip.context, R.color.gray_000)
        }
    )
}

@BindingAdapter("setImageUrl")
fun setImageUrl(imageView: ImageView, url: String) {
    if (url.isNotEmpty()) {
        imageView.load(url)
    }
}