package com.example.jbchhymnbook.hymn.domain.usecase

import com.example.jbchhymnbook.core.domain.Result
import com.example.jbchhymnbook.hymn.domain.model.HymnGroup
import com.example.jbchhymnbook.hymn.domain.repository.HymnRepository

class GetHymnGroupsUseCase(
    private val hymnRepository: HymnRepository
) {
    suspend operator fun invoke(): Result<List<HymnGroup>> {
        return hymnRepository.getHymnGroups()
    }
}

