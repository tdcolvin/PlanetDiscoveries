package com.apptaura.planetdiscoveries.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID

@Entity(tableName = "planets")
data class Planet (
    @PrimaryKey @ColumnInfo(name = "entryid")
    var id: String = UUID.randomUUID().toString(),

    var name: String = "",

    var distanceLy: Float = 1.0F,

    @Ignore
    var discovered: Date = Date(),
)