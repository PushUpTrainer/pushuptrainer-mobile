package com.example.pushuptrainer_mobile

import com.example.pushuptrainer_mobile.di.sharedModules
import kotlin.test.Test
import kotlin.test.assertNotNull
import org.koin.dsl.koinApplication

class SharedDiTest {
    @Test
    fun sharedGraphResolvesGreeting() {
        val application = koinApplication {
            modules(sharedModules)
        }

        assertNotNull(application.koin.get<Greeting>())
        application.close()
    }
}
