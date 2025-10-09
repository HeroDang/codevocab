package com.group20.codevocab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int?,
    @ColumnInfo(name = "name")
    val name: String?,
    @ColumnInfo(name = "parent_id")
    val parentId: Int? = null,
    @ColumnInfo(name = "description")
    val description: String?,
    @ColumnInfo(name = "category")
    val category: String?
)
