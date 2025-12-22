package com.group20.codevocab.model.mapper

import com.group20.codevocab.data.remote.dto.WordDto
import com.group20.codevocab.model.WordItem

fun WordDto.toWordItem(): WordItem = WordItem(
    id = id,
    textEn = textEn.orEmpty(),
    meaningVi = meaningVi.orEmpty(),
    ipa = ipa,
    partOfSpeech = partOfSpeech,
    exampleSentence = exampleSentence,
    audioUrl = audioUrl
)
