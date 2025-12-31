package com.example.jbchhymnbook

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform