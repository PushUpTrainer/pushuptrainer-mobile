package com.example.pushuptrainer_mobile

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform