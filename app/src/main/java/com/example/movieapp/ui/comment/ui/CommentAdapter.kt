package com.example.movieapp.ui.comment.ui

import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.INVISIBLE
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.VISIBLE
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.movieapp.R
import com.example.movieapp.databinding.ItemCommentFoldingBinding
import com.example.movieapp.databinding.ItemCommentLevel1Binding
import com.example.movieapp.databinding.ItemCommentLevel2Binding
import com.example.movieapp.databinding.ItemCommentLoadingBinding
import com.example.movieapp.databinding.LayoutCommentEmptyBinding
import com.example.movieapp.databinding.LayoutLoadingFullBinding
import com.example.movieapp.ui.comment.logic.Reducer
import com.example.movieapp.ui.comment.logic.impl.ExpandReducer
import com.example.movieapp.ui.comment.logic.impl.ExpandReplyLoadedReducer
import com.example.movieapp.ui.comment.logic.impl.FoldReducer
import com.example.movieapp.ui.comment.ui.CommentItem.Folding.State

class CommentAdapter(
    val reduceBlock: Reducer.() -> Unit,
    private val reply: (item: CommentItem) -> Unit,
    private val loadMore: (item: CommentItem) -> Unit,
    private val loadMoreReply: (item: CommentItem) -> Unit,
    userId: Int?,

    ) : ListAdapter<CommentItem, VH>(object : DiffUtil.ItemCallback<CommentItem>() {

    override fun areItemsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
        return oldItem.id == newItem.id
    }


    override fun areContentsTheSame(oldItem: CommentItem, newItem: CommentItem): Boolean {
        return oldItem == newItem
    }
}) {

    init {
        submitList(
            listOf(
                CommentItem.FirstLoading(page = 1)
            )
        )
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_LEVEL1 -> Level1VH(
                ItemCommentLevel1Binding.inflate(
                    inflater, parent, false
                ), reduceBlock, reply
            )

            TYPE_LEVEL2 -> Level2VH(
                ItemCommentLevel2Binding.inflate(
                    inflater, parent, false
                ), reduceBlock, reply
            )

            TYPE_LOADING -> LoadingVH(
                ItemCommentLoadingBinding.inflate(
                    inflater, parent, false
                ), reduceBlock, loadMore
            )

            TYPE_FIRST_LOADING -> FirstLoadingVH(
                LayoutLoadingFullBinding.inflate(
                    inflater, parent, false
                ), reduceBlock
            )

            TYPE_EMPTY -> EmptyVH(
                LayoutCommentEmptyBinding.inflate(
                    inflater, parent, false
                ), reduceBlock
            )

            else -> FoldingVH(
                ItemCommentFoldingBinding.inflate(
                    inflater, parent, false
                ), reduceBlock, loadMoreReply

            )
        }
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.onBind(getItem(position))
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is CommentItem.Level1 -> TYPE_LEVEL1
            is CommentItem.Level2 -> TYPE_LEVEL2
            is CommentItem.Loading -> TYPE_LOADING
            is CommentItem.FirstLoading -> TYPE_FIRST_LOADING
            is CommentItem.Empty -> TYPE_EMPTY
            else -> TYPE_FOLDING
        }
    }

    companion object {
        private const val TYPE_LEVEL1 = 0
        private const val TYPE_LEVEL2 = 1
        private const val TYPE_FOLDING = 2
        private const val TYPE_LOADING = 3
        private const val TYPE_FIRST_LOADING = 4
        private const val TYPE_EMPTY = 5
    }
}

abstract class VH(itemView: View, protected val reduceBlock: Reducer.() -> Unit) :
    ViewHolder(itemView) {
    abstract fun onBind(item: CommentItem)
}

class Level1VH(
    val binding: ItemCommentLevel1Binding,
    reduceBlock: Reducer.() -> Unit,
    val callBack: (CommentItem) -> Unit
) : VH(binding.root, reduceBlock) {

    override fun onBind(item: CommentItem) {
        val data = item as CommentItem.Level1
        Glide.with(binding.root.context).load(data.avatar_url).error(R.drawable.avatar_anonymous)
            .into(binding.avatar)
        binding.unLike.setImageResource(if (data.unLike) R.drawable.ic_unlike_selected else R.drawable.ic_unlike)
        binding.imgLike.setImageResource(if (data.like) R.drawable.ic_favorite_selected else R.drawable.ic_favorite)
        binding.tvLike.text = data.likeCount.toString()
        binding.tvLike.visibility = if (data.likeCount == 0) INVISIBLE else VISIBLE
        binding.username.text = data.userName
        binding.content.text = data.content
        binding.lnLike.setOnClickListener {
            data.like = !data.like
            if (data.like) {
                binding.imgLike.setImageResource(R.drawable.ic_favorite_selected)
                data.likeCount++
            } else {
                binding.imgLike.setImageResource(R.drawable.ic_favorite)
                data.likeCount--
            }
            if (data.unLike) {
                binding.unLike.setImageResource(R.drawable.ic_unlike)
                data.unLike = false
            }
            binding.tvLike.visibility = if (data.likeCount == 0) INVISIBLE else VISIBLE
            binding.tvLike.text = data.likeCount.toString()
        }
        binding.lnUnlike.setOnClickListener {
            data.unLike = !data.unLike
            if (data.unLike) {
                binding.unLike.setImageResource(R.drawable.ic_unlike_selected)
            } else {
                binding.unLike.setImageResource(R.drawable.ic_unlike)
            }
            if (data.like) {
                data.like = false
                binding.imgLike.setImageResource(R.drawable.ic_favorite)
                data.likeCount--
                binding.tvLike.visibility = if (data.likeCount == 0) INVISIBLE else VISIBLE
                binding.tvLike.text = data.likeCount.toString()
            }

        }
        binding.root.setOnClickListener {
//            reduceBlock.invoke(ReplyReducer(item, itemView.context))
            callBack(item)
        }
        binding.reply.setOnClickListener {
            callBack(item)
        }
    }
}


class Level2VH(
    val binding: ItemCommentLevel2Binding,
    reduceBlock: Reducer.() -> Unit,
    val callBack: (CommentItem) -> Unit
) : VH(binding.root, reduceBlock) {
    override fun onBind(item: CommentItem) {
        val data = item as CommentItem.Level2
        Glide.with(binding.root.context).load(data.avatar_url).error(R.drawable.avatar_anonymous)
            .into(binding.avatar)

        binding.unLike.setImageResource(if (data.unLike) R.drawable.ic_unlike_selected else R.drawable.ic_unlike)
        binding.imgLike.setImageResource(if (data.like) R.drawable.ic_favorite_selected else R.drawable.ic_favorite)
        binding.tvLike.text = data.likeCount.toString()
        binding.tvLike.visibility = if (data.likeCount == 0) INVISIBLE else VISIBLE

        binding.username.text =
            if (data.userReply != null) "${data.userName}  ▸  ${data.userReply}" else "${data.userName}"
        binding.content.text = data.content
        binding.lnLike.setOnClickListener {
            data.like = !data.like
            if (data.like) {
                binding.imgLike.setImageResource(R.drawable.ic_favorite_selected)
                data.likeCount++
            } else {
                binding.imgLike.setImageResource(R.drawable.ic_favorite)
                data.likeCount--
            }
            if (data.unLike) {
                binding.unLike.setImageResource(R.drawable.ic_unlike)
                data.unLike = false
            }
            binding.tvLike.visibility = if (data.likeCount == 0) INVISIBLE else VISIBLE
            binding.tvLike.text = data.likeCount.toString()
        }
        binding.lnUnlike.setOnClickListener {
            data.unLike = !data.unLike
            if (data.unLike) {
                binding.unLike.setImageResource(R.drawable.ic_unlike_selected)
            } else {
                binding.unLike.setImageResource(R.drawable.ic_unlike)
            }
            if (data.like) {
                data.like = false
                binding.imgLike.setImageResource(R.drawable.ic_favorite)
                data.likeCount--
                binding.tvLike.visibility = if (data.likeCount == 0) INVISIBLE else VISIBLE
                binding.tvLike.text = data.likeCount.toString()
            }

        }
        binding.root.setOnClickListener {
//            reduceBlock.invoke(ReplyReducer(item, itemView.context))
            callBack(item)
        }
        binding.reply.setOnClickListener {
            callBack(item)
        }
    }
}

class FoldingVH(
    val binding: ItemCommentFoldingBinding,
    reduceBlock: Reducer.() -> Unit,
    val loadMoreReply: (item: CommentItem) -> Unit
) :
    VH(binding.root, reduceBlock) {
    override fun onBind(item: CommentItem) {

//        binding.lnExpand.visibility =
//            if (folding.state == CommentItem.Folding.State.LOADED_ALL) GONE else VISIBLE
//        binding.lnCollapse.visibility = if (folding.page == 1) GONE else VISIBLE
//
//        binding.lnExpand.setOnClickListener {
//            reduceBlock.invoke(StartExpandReducer(folding))
//            reduceBlock.invoke(ExpandReducer(folding))
//        }
//
//        binding.lnCollapse.setOnClickListener {
//            reduceBlock.invoke(FoldReducer(folding))
//        }
        val folding = item as CommentItem.Folding
        binding.expand.text = folding.text

        when (folding.state) {
            State.LOADING -> {
                binding.lnCollapse.visibility = GONE
                binding.lnExpand.visibility = GONE
                binding.loadingView.visibility = VISIBLE
                binding.view.visibility = GONE
            }

            State.LOADED_ALL -> {
                binding.lnExpand.visibility = GONE
                binding.view.visibility = VISIBLE
                binding.loadingView.visibility = GONE
                binding.lnCollapse.visibility = VISIBLE
            }

            State.IDLE -> {
                binding.lnCollapse.visibility = GONE
                binding.lnExpand.visibility = VISIBLE
                binding.loadingView.visibility = GONE
                binding.view.visibility = VISIBLE
            }

            State.COLLAPSE -> {
                binding.lnCollapse.visibility = GONE
                binding.lnExpand.visibility = VISIBLE
                binding.loadingView.visibility = GONE
                binding.view.visibility = VISIBLE
            }
        }

        binding.lnExpand.setOnClickListener {
            if (folding.state == State.COLLAPSE) {
                reduceBlock.invoke(ExpandReplyLoadedReducer(folding))
            } else {
                loadMoreReply(item)
            }
        }

        binding.lnCollapse.setOnClickListener {
            reduceBlock.invoke(FoldReducer(item))
        }
    }
}

class LoadingVH(
    val binding: ItemCommentLoadingBinding,
    reduceBlock: Reducer.() -> Unit,
    private val loadMore: (item: CommentItem) -> Unit,
) : VH(binding.root, reduceBlock) {
    //    private val state: TextView = itemView.findViewById(R.id.state)
    override fun onBind(item: CommentItem) {
//        state.text = item.content
//        if ((item as CommentItem.Loading).state == CommentItem.Loading.State.IDLE) {
//            reduceBlock.invoke(StartLoadLv1Reducer())
//            reduceBlock.invoke(LoadLv1Reducer())
//        }
        loadMore(item)
    }
}

class FirstLoadingVH(val binding: LayoutLoadingFullBinding, reduceBlock: Reducer.() -> Unit) :
    VH(binding.root, reduceBlock) {
    override fun onBind(item: CommentItem) {
    }
}

class EmptyVH(val binding: LayoutCommentEmptyBinding, reduceBlock: Reducer.() -> Unit) :
    VH(binding.root, reduceBlock) {
    override fun onBind(item: CommentItem) {
    }
}