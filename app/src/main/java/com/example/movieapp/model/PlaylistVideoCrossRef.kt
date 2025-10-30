package com.example.movieapp.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "playlist_video_crossref",
    primaryKeys = ["playlistId", "slug"],
    foreignKeys = [
        ForeignKey(
            entity = PlayList::class,
            parentColumns = ["id"],
            childColumns = ["playlistId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Video::class,
            parentColumns = ["slug"],
            childColumns = ["slug"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("playlistId"),
        Index("slug")
    ]
)
data class PlaylistVideoCrossRef(
    val playlistId: Int, val slug: String
)
