package com.example.jbchhymnbook.hymn.domain.repository

import com.example.jbchhymnbook.core.domain.Result
import com.example.jbchhymnbook.hymn.domain.model.Hymn
import com.example.jbchhymnbook.hymn.domain.model.HymnGroup

interface HymnRepository {
    suspend fun getHymnGroups(): Result<List<HymnGroup>>
    suspend fun getHymnsByGroup(groupId: String): Result<List<Hymn>>
    suspend fun getHymnById(id: String): Result<Hymn>
    suspend fun getMusicXml(hymnId: String): Result<String>
}

