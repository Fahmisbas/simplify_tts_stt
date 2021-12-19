package com.revelatestudio.simplifyspeechnotes.data.local

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Parcelize
@Entity(tableName = "note_table")
data class Note(
    @ColumnInfo(name = "title") val title : String,
    @ColumnInfo(name = "content") val content : String,
    @ColumnInfo(name = "date") val date : String
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    var id : Int = 0
}