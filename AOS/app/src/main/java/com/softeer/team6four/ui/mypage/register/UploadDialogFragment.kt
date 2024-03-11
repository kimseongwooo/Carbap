package com.softeer.team6four.ui.mypage.register

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.content.res.AppCompatResources
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.softeer.team6four.R
import com.softeer.team6four.databinding.FragmentUploadDialogBinding

class UploadDialogFragment(private val navigationCallback: () -> Unit) : DialogFragment() {
    private var _binding: FragmentUploadDialogBinding? = null
    private val registerViewModel: RegisterViewModel by activityViewModels()

    private val binding
        get() = _binding!!

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = FragmentUploadDialogBinding.inflate(layoutInflater)

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setBackground(
                AppCompatResources.getDrawable(
                    requireContext(),
                    R.drawable.background_upload_dialog
                )
            )

        builder.setView(binding.root)
        with(binding) {
            lifecycleOwner = this@UploadDialogFragment
            viewModel = registerViewModel
            btnUpload.setOnClickListener {
                registerViewModel.registerCharger()
                dismiss()
                navigationCallback.invoke()
            }
            btnCancel.setOnClickListener { dismiss() }
        }
        return builder.create()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "UploadDialogFragment"
    }
}