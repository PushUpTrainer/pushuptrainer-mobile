package com.example.pushuptrainer_mobile.core.navigation

import androidx.navigation3.runtime.NavKey
import com.example.pushuptrainer_mobile.core.navigation.di.GlobalRouterContext
import com.example.pushuptrainer_mobile.di.sharedModules
import com.example.pushuptrainer_mobile.features.home.HomeRoute
import com.example.pushuptrainer_mobile.features.main.MainRoute
import com.example.pushuptrainer_mobile.features.main.di.MainRouterContext
import com.example.pushuptrainer_mobile.features.trainingresult.TrainingResultRoute
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.koin.core.qualifier.qualifier
import org.koin.dsl.koinApplication

class NavigationTest {
    @Test
    fun eachGraphGetsItsOwnSingletonRouter() {
        val application = koinApplication { modules(sharedModules) }
        try {
            val koin = application.koin
            val global = koin.get<RouterController>(qualifier<GlobalRouterContext>())
            val main = koin.get<RouterController>(qualifier<MainRouterContext>())

            assertSame<Router>(global, koin.get<Router>(qualifier<GlobalRouterContext>()))
            assertSame<Router>(main, koin.get<Router>(qualifier<MainRouterContext>()))
            assertNotSame(global, main)

            val outer = mutableListOf<NavKey>(MainRoute.newEntry())
            val home = HomeRoute.newEntry()
            val inner = mutableListOf<NavKey>(home)
            global.attach(outer) { true }
            main.attach(inner) { true }
            assertFalse(global.open(home, TrainingResultRoute("saved")))
            assertTrue(main.open(home, TrainingResultRoute("saved")))
            assertEquals(MainRoute, (outer.single() as NavigationEntry).destination)
            assertEquals(2, inner.size)
        } finally {
            application.close()
        }
    }

    @Test
    fun closedEntryCannotNavigateAfterTheSameResultIsReopened() {
        val router = StackRouter()
        val home = HomeRoute.newEntry()
        val stack = mutableListOf<NavKey>(home)
        val destination = TrainingResultRoute("saved")
        router.attach(stack) { true }

        assertTrue(router.open(home, destination))
        val first = stack.last() as NavigationEntry
        assertFalse(router.open(home, destination))
        assertFalse(router.open(first, destination))
        assertTrue(router.back(first))
        assertTrue(router.open(home, destination))

        val reopened = stack.last() as NavigationEntry
        assertEquals(first.destination, reopened.destination)
        assertNotEquals(first.id, reopened.id)
        assertFalse(router.back(first))
        assertTrue(router.returnTo(reopened, HomeRoute))
        assertFalse(router.back(home))
        assertEquals(listOf<NavKey>(home), stack)
    }

    @Test
    fun restoredEntryKeepsItsIdentityAndOldHostCannotDetachNewBinding() {
        val router = StackRouter()
        val home = HomeRoute.newEntry()
        val firstHost = mutableListOf<NavKey>(home)
        val detachOld = router.attach(firstHost) { true }
        assertTrue(router.open(home, TrainingResultRoute("saved")))
        val result = firstHost.last() as NavigationEntry
        var resumed = false
        val newHost = mutableListOf<NavKey>(home.copy(), result.copy())
        val detachNew = router.attach(newHost) { resumed }
        detachOld()
        assertFalse(router.back(result))
        resumed = true
        assertTrue(router.back(result))
        assertEquals(listOf<NavKey>(home), newHost)
        assertEquals(2, firstHost.size)
        detachNew()
        assertFalse(router.open(home, TrainingResultRoute("saved")))
    }
}
