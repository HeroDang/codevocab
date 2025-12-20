package com.group20.codevocab.model.mapper

import com.group20.codevocab.data.remote.dto.ModuleDetailDto
import com.group20.codevocab.model.ModuleDetailItem
import com.group20.codevocab.model.SubModuleItem

fun ModuleDetailDto.toModuleDetailItem(): ModuleDetailItem {
    return ModuleDetailItem(
        id = id,
        title = name,
        description = description,
        children = children.map {
            SubModuleItem(
                id = it.id,
                name = it.name,
                description = it.description
            )
        }
    )
}