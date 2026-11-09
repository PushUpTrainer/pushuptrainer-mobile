package com.example.pushuptrainer_mobile.core.navigation

import androidx.navigation3.runtime.NavKey
import com.example.pushuptrainer_mobile.di.sharedModules
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotSame
import kotlin.test.assertSame
import kotlin.test.assertTrue
import org.koin.dsl.koinApplication

class NavigationTest {
    private data object Start : NavKey

    private data class Detail(val id: String) : NavKey

    @Test
    fun onlyTheActiveRootAndEntryCanChangeTheStack() {
        val application = koinApplication { modules(sharedModules) }
        try {
            val scope = application.koin.createScope<NavigationScope>("first-root")
            val router = scope.get<Router>()
            val controller = scope.get<RouterController>()
            assertSame<Any>(router, controller)

            val stack = mutableListOf<NavKey>(Start)
            val detail = Detail("workout-1")
            var resumed = true
            assertFalse(router.open(Start, detail))
            controller.attach(stack) { resumed }

            assertFalse(router.back(Start))
            assertTrue(router.open(Start, detail))
            assertFalse(router.open(Start, Detail("workout-2")))
            assertFalse(router.open(detail, detail))
            assertFalse(router.open(detail, Start))
            assertEquals(listOf<NavKey>(Start, detail), stack)

            resumed = false
            assertFalse(router.back(detail))
            resumed = true
            assertEquals(detail, stack.last())
            assertTrue(router.back(detail))
            assertEquals(listOf<NavKey>(Start), stack)

            controller.detach()
            scope.close()

            val newScope = application.koin.createScope<NavigationScope>("second-root")
            val newRouter = newScope.get<Router>()
            val newController = newScope.get<RouterController>()
            val newStack = mutableListOf<NavKey>(Start)
            newController.attach(newStack) { true }

            assertNotSame(router, newRouter)
            assertFalse(router.open(Start, detail))
            assertEquals(listOf<NavKey>(Start), newStack)
            assertTrue(newRouter.open(Start, detail))
            assertEquals(listOf<NavKey>(Start, detail), newStack)

            newController.detach()
            newScope.close()
        } finally {
            application.close()
        }
    }
}
