package com.group20.codevocab.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.group20.codevocab.data.remote.dto.ModuleDto

@Entity(tableName = "modules")
data class ModuleEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,
    
    @ColumnInfo(name = "name")
    val name: String,
    
    @ColumnInfo(name = "description")
    val description: String?,
    
    @ColumnInfo(name = "module_type")
    val moduleType: String,
    
    @ColumnInfo(name = "is_public")
    val isPublic: Boolean,
    
    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "is_deleted")
    val isDeleted: Boolean = false
)

fun ModuleDto.toEntity(): ModuleEntity {
    return ModuleEntity(
        id = id,
        name = name,
        description = description,
        moduleType = module_type,
        isPublic = is_public,
        createdAt = created_at,
        isDeleted = false
    )
}

fun ModuleEntity.toDto(): ModuleDto {
    return ModuleDto(
        id = id,
        name = name,
        description = description,
        module_type = moduleType,
        is_public = isPublic,
        created_at = createdAt
    )
}
