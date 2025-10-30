package com.example.movieapp.util

import com.amrdeveloper.reactbutton.Reaction
import com.example.movieapp.R

object FbReactions {
    val defaultReact = Reaction(
        ReactConstants.LIKE,
        ReactConstants.DEFAULT,
        ReactConstants.GRAY,
        R.drawable.ic_gray_like
    )

    val reactions = listOf(
        Reaction(ReactConstants.LIKE, ReactConstants.BLUE, R.drawable.ic_like),
        Reaction(ReactConstants.LOVE, ReactConstants.RED_LOVE, R.drawable.ic_heart),
        Reaction(ReactConstants.SMILE, ReactConstants.YELLOW_WOW, R.drawable.ic_happy),
        Reaction(ReactConstants.WOW, ReactConstants.YELLOW_WOW, R.drawable.ic_surprise),
        Reaction(ReactConstants.SAD, ReactConstants.YELLOW_HAHA, R.drawable.ic_sad),
        Reaction(ReactConstants.ANGRY, ReactConstants.RED_ANGRY, R.drawable.ic_angry),
    )
}