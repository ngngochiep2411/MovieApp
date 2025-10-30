package com.example.movieapp.ui.listvideo

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.movieapp.databinding.LayoutMovieBottomsheetBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class MovieDetailBottomSheet : BottomSheetDialogFragment() {

    private var _binding: LayoutMovieBottomsheetBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LayoutMovieBottomsheetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Lấy data từ arguments
        val title = arguments?.getString("title")
        val description = arguments?.getString("description")

        dialog?.setOnShowListener { dialog ->
            val bottomSheetDialog = dialog as BottomSheetDialog
            val bottomSheet =
                bottomSheetDialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.layoutParams?.height =
                Resources.getSystem().displayMetrics.heightPixels - Resources.getSystem().displayMetrics.heightPixels / 4
            bottomSheet?.requestLayout()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(title: String, description: String): MovieDetailBottomSheet {
            val fragment = MovieDetailBottomSheet()
            val args = Bundle()
            args.putString("title", title)
            args.putString("description", description)
            fragment.arguments = args
            return fragment
        }
    }
}