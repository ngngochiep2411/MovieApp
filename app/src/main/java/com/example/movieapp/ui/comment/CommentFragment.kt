package com.example.movieapp.ui.comment


import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.movieapp.Constant
import com.example.movieapp.databinding.FragmentCommentBinding
import com.example.movieapp.model.CommentData
import com.example.movieapp.model.ReplyData
import com.example.movieapp.model.User
import com.example.movieapp.ui.auth.BottomSheetAuthFragment
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
import com.example.movieapp.database.DatabaseManager
import com.example.movieapp.util.SharedViewModel
import com.example.movieapp.util.Utils
import com.example.movieapp.widgets.ReplyDialog
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

@AndroidEntryPoint
class CommentFragment : Fragment() {

    private var user: User? = null
    private var videoName: String? = ""
    private lateinit var binding: FragmentCommentBinding
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var replyDialog: ReplyDialog
    private val viewModel: CommentViewModel by viewModels()
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var dialog: BottomSheetDialog

    companion object {

        fun newInstance(videoName: String?): CommentFragment {
            val bundle = Bundle()
            val fragment = CommentFragment()
            bundle.putString("videoName", videoName)
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

        videoName = arguments?.getString("videoName", null)
        if (videoName != null) {
            fetchData()
        }

        commentAdapter = CommentAdapter(
            reduceBlock = reducerBlock(),
            reply = ::reply,
            loadMore = ::getComment,
            loadMoreReply = ::getReply
        )
        binding.rvComment.adapter = commentAdapter
        initObserver()
        setOnClick()
        Log.d("testing", "videoName $videoName")

    }

    private fun fetchData() {
        viewModel.getComment(videoName)
    }

    private fun getComment(commentItem: CommentItem) {
        viewModel.getMoreComment(videoName = videoName)
    }

    private fun getReply(commentItem: CommentItem) {
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

    private fun reply(commentItem: CommentItem) {
        lifecycleScope.launch {
            val content = withContext(Dispatchers.Main) {
                suspendCoroutine { continuation ->
                    ReplyDialog(requireContext(), commentItem.userName) { result ->
                        continuation.resume(result)
                    }.show()
                }
            }
            val replyData = ReplyData(
                content = content,
                comment_id = if (commentItem is CommentItem.Level1) commentItem.id
                else (commentItem as CommentItem.Level2).parentId,
                reply_user_id = commentItem.userId,
                user_id = viewModel.userDetail.value?.id
            )
            viewModel.repComment(
                replyData
            ).collect {
                commentAdapter.reduceBlock.invoke(
                    ReplyReducer(
                        commentItem, requireContext(), CommentItem.Level2(
                            id = it.data.id,
                            time = it.data.created_at,
                            userId = it.data.user.id,
                            userReply = if (it.data.user.id == it.data.reply_user?.id) null else it.data.reply_user?.name,
                            content = it.data.content,
                            unLike = false,
                            like = false,
                            parentId = it.data.comment_id,
                            userName = it.data.user.name,
                            likeCount = 22,
                            avatar_url = it.data.user.avatar_url
                        )
                    )
                )
            }
        }


    }

    private fun reducerBlock(): Reducer.() -> Unit {
        return {
            lifecycleScope.launch {
                lifecycle.repeatOnLifecycle(Lifecycle.State.CREATED) {
                    val newList = withContext(Dispatchers.IO) {
                        reduce.invoke(commentAdapter.currentList)
                    }
                    commentAdapter.submitList(newList)
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
                    Toast.makeText(context, "Hãy đợi bình luận được tải xong", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    showCommentDialog()
                }

            }
        }
    }

    private fun showBottomSheetLogin() {
        val bottomSheetFragment = BottomSheetAuthFragment()
        bottomSheetFragment.show(
            requireActivity().supportFragmentManager, BottomSheetAuthFragment.TAG
        )
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
                                it, viewModel.userDetail.value?.id
                            )
                        )
                    }
                }
            }

        }


    }


    private fun showCommentDialog() {
        replyDialog = ReplyDialog(requireContext()) { content ->
            lifecycleScope.launch {
                viewModel.comment(
                    CommentData(
                        content = content,
                        userId = viewModel.userDetail.value?.id,
                        videoName = videoName!!
                    )
                ).collect {
                    if (it.success()) {
                        commentAdapter.reduceBlock.invoke(AddCommentReducer(it.data))
                        binding.rvComment.smoothScrollToPosition(0)
                    }
                }
            }
        }
        replyDialog.show()
    }


    override fun onResume() {
        super.onResume()
        binding.root.requestLayout()
    }

    fun updateVideoName(slug: String?) {
        if (videoName == null) {
            viewModel.getComment(slug)
        }
        this.videoName = slug
        Log.d("testing", "$videoName")
    }
}


