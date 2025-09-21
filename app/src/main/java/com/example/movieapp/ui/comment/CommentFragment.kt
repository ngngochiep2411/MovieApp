package com.example.movieapp.ui.comment


import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.movieapp.Constant
import com.example.movieapp.R
import com.example.movieapp.databinding.FragmentCommentBinding
import com.example.movieapp.model.CommentData
import com.example.movieapp.model.ReplyData
import com.example.movieapp.model.User
import com.example.movieapp.ui.authen.LoginActivity
import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.logic.impl.AddCommentReducer
import com.example.movieapp.ui.comment.logic.impl.ExpandReducer
import com.example.movieapp.ui.comment.logic.impl.LoadLv1Reducer
import com.example.movieapp.ui.comment.logic.impl.ReplyReducer
import com.example.movieapp.ui.comment.logic.impl.StartExpandReducer
import com.example.movieapp.ui.comment.logic.impl.StartLoadLv1Reducer
import com.example.movieapp.ui.comment.logic.impl.convertComment
import com.example.movieapp.ui.comment.ui.CommentAdapter
import com.example.movieapp.ui.comment.ui.CommentItem
import com.example.movieapp.ui.detailmovie.DetailMovieActivity
import com.example.movieapp.util.Extension.setRoundedStartIconUrl
import com.example.movieapp.util.SharedViewModel
import com.example.movieapp.util.Utils
import com.example.movieapp.widgets.ReplyDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import gun0912.tedimagepicker.builder.TedImagePicker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


@AndroidEntryPoint
class CommentFragment : Fragment() {

    private var user: User? = null
    private var videoName: String? = ""
    private lateinit var binding: FragmentCommentBinding
    private lateinit var commentAdapter: CommentAdapter
    private val viewModel: CommentViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var dialog: BottomSheetDialog
    private var scrollPosition = 0
    var isClickExpand = false

    companion object {

        fun newInstance(videoName: String?): CommentFragment {
            val bundle = Bundle()
            val fragment = CommentFragment()
            bundle.putString("videoName", videoName)
            fragment.arguments = bundle
            return fragment
        }

        fun newInstance(): CommentFragment {
            val bundle = Bundle()
            val fragment = CommentFragment()
            fragment.arguments = bundle
            return fragment
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentCommentBinding.inflate(layoutInflater, container, false)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        commentAdapter = CommentAdapter(
            reduceBlock = reducerBlock(),
            reply = ::reply,
            loadMoreReply = ::getReply,
            loadMore = ::getComment,
            onImageClick = ::onImageClick
        )
        binding.rvComment.adapter = commentAdapter
        commentAdapter.stateRestorationPolicy =
            RecyclerView.Adapter.StateRestorationPolicy.PREVENT_WHEN_EMPTY
        initObserver()
        setOnClick()

    }

    fun showReplyDialog(
        userName: String = "",
        isComment: Boolean = false,
        isReply: Boolean = false,
        commentItem: CommentItem? = null,
        uri: Uri? = null
    ) {
        val dialog = ReplyDialog(
            requireContext(), userName, callback = { content: String, uri: Uri? ->
                isClickExpand = false
                if (isComment) {
                    scrollPosition = 0
                    lifecycleScope.launch {
                        viewModel.comment(
                            CommentData(
                                content = content,
                                userId = viewModel.userDetail.value?.id,
                                videoName = videoName.toString()
                            ), toMultiPart(uri)
                        ).collect {
                            commentAdapter.reduceBlock.invoke(AddCommentReducer(it.data))
                        }
                    }
                }

                if (isReply) {
                    lifecycleScope.launch {
                        val replyData = ReplyData(
                            content = content,
                            comment_id = if (commentItem is CommentItem.Level1) commentItem.id
                            else (commentItem as CommentItem.Level2).parentId,
                            reply_user_id = commentItem.userId,
                            user_id = viewModel.userDetail.value?.id
                        )
                        viewModel.repComment(
                            replyData, toMultiPart(uri)
                        ).collect {
                            commentAdapter.reduceBlock.invoke(
                                ReplyReducer(
                                    commentItem, it.data
                                )
                            )
                        }
                    }
                }
            }, uri = uri
        )
        dialog.show()
    }

    private fun scrollToPosition(position: Int) {
        binding.rvComment.post {
            (binding.rvComment.layoutManager as LinearLayoutManager).scrollToPosition(position)

        }

    }

    fun toMultiPart(uri: Uri?): MultipartBody.Part? {
        if (uri != null) {
            return requireContext().contentResolver.openInputStream(uri)?.use { inputStream ->
                val bytes = inputStream.readBytes()
                MultipartBody.Part.createFormData(
                    "image",
                    "upload_${System.currentTimeMillis()}.jpg",
                    bytes.toRequestBody("image/*".toMediaType())
                )
            }
        }
        return null
    }

    private fun fetchData() {
        viewModel.getComment(videoName)
    }

    private fun getComment(commentItem: CommentItem) {
        viewModel.getMoreComment(videoName = videoName)
    }

    private fun onImageClick(commentItem: CommentItem, position: Int) {
        val dialog = Dialog(requireContext(), android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        dialog.setContentView(R.layout.dialog_viewer_image)

        val back = dialog.findViewById<ImageView>(R.id.back)
        val image = dialog.findViewById<ImageView>(R.id.image)
        val content = dialog.findViewById<TextView>(R.id.content)
        val tvLike = dialog.findViewById<TextView>(R.id.tvLike)

        back.setOnClickListener {
            dialog.dismiss()
        }
        val url = if (commentItem is CommentItem.Level1) Constant.BASE_URL + "/" + commentItem.image
        else Constant.BASE_URL + "/" + (commentItem as CommentItem.Level2).image
        val loadUrl =
            if (commentItem is CommentItem.Level1) commentItem.avatar_url else (commentItem as CommentItem.Level2).avatar_url
        val userName =
            if (commentItem is CommentItem.Level1) commentItem.userName else (commentItem as CommentItem.Level2).userName
        val comment =
            if (commentItem is CommentItem.Level1) commentItem.content else (commentItem as CommentItem.Level2).content
        Utils.loadImage(requireContext(), url, image)
        content.setRoundedStartIconUrl(
            loadUrl,
            (content.textSize + 10).toInt(),
            userName.toString(),
            comment.toString()
        )
        dialog.window?.decorView?.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    or View.SYSTEM_UI_FLAG_FULLSCREEN)
        dialog.show()
    }

    private fun getReply(commentItem: CommentItem) {
        isClickExpand = true
        commentAdapter.reduceBlock.invoke(StartExpandReducer(commentItem as CommentItem.Folding))
        lifecycleScope.launch {
            viewModel.getReply(
                comment_id = commentItem.parentId, video_id = videoName, page = commentItem.page + 1
            ).collect { response ->
                val folding = commentAdapter.currentList.find {
                    (it is CommentItem.Folding) && it.parentId == commentItem.parentId
                } as CommentItem.Folding


                commentAdapter.reduceBlock.invoke(
                    ExpandReducer(
                        folding, response.data, response.pagination
                    )
                )
            }
        }

    }

    private fun reply(commentItem: CommentItem, position: Int) {
        scrollPosition = position + 1
        showReplyDialog(
            userName = commentItem.userName.toString(), commentItem = commentItem, isReply = true
        )
    }

    private fun reducerBlock(): Reducer.() -> Unit {

        return {
            val reducer = this
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    val newList = withContext(Dispatchers.IO) {
                        reduce.invoke(commentAdapter.currentList)
                    }
                    commentAdapter.submitList(newList) {
                        if (reducer is AddCommentReducer || reducer is ReplyReducer) {
                            scrollToPosition(position = scrollPosition)
                        }
                    }

                }
            }
        }
    }

    private fun setOnClick() {


        binding.rlComment.setOnClickListener {
            if (user == null) {
                showBottomSheetLogin()
            } else {
                if (commentAdapter.currentList.size == 1 && commentAdapter.currentList[0] is CommentItem.FirstLoading) {
                    Toast.makeText(
                        context, "Hãy đợi bình luận được tải xong", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    showReplyDialog(
                        isComment = true
                    )
                }

            }
        }

        binding.emoji.setOnClickListener {

        }

        binding.pickImage.setOnClickListener {
            if (user == null) {
                showBottomSheetLogin()
            } else {
                TedImagePicker.with(requireContext()).start { uri ->
                    showReplyDialog(
                        isComment = true, uri = uri
                    )
                }
            }
        }
    }

    private fun showBottomSheetLogin() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.layout_dialog_required_login)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.findViewById<TextView>(R.id.cancel).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<TextView>(R.id.login).setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            pauseVideo()
        }
        dialog.show()
    }

    private fun pauseVideo() {
        val activity = activity as DetailMovieActivity
        activity.pauseVideo()
    }

    private fun initObserver() {
        lifecycleScope.launch {
            launch {
                viewModel.comments.collect {
                    if (it != null) {
                        val newList = it.convertComment(viewModel.nextPage)
                        commentAdapter.submitList(newList)

                    }
                }
            }
            launch {
                sharedViewModel.login.collect {
                    if (it) {
                        dialog.dismiss()
                    }
                }
            }

            launch {
                viewModel.userDetail.collect { userDetail ->
                    user = userDetail
                    Utils.loadImage(
                        requireContext(),
                        "${Constant.BASE_IMAGE_URL}${userDetail?.avatarUrl}",
                        binding.avatar
                    )
                }
            }

            launch {
                viewModel.moreComments.collect {
                    if (!it.isNullOrEmpty()) {
                        commentAdapter.reduceBlock.invoke(StartLoadLv1Reducer())
                        commentAdapter.reduceBlock.invoke(
                            LoadLv1Reducer(
                                it, viewModel.userDetail.value?.id, viewModel.nextPage
                            )
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }


    fun updateData(slug: String?) {
        this.videoName = slug
        if (videoName != null) {
            fetchData()
        }
    }
}


