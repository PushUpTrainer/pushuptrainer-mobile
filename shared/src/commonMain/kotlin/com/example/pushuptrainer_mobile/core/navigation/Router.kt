package com.example.pushuptrainer_mobile.core.navigation

import androidx.annotation.MainThread
import androidx.navigation3.runtime.NavKey

interface Router {
    @MainThread
    fun open(from: NavKey, destination: NavKey): Boolean

    @MainThread
    fun back(from: NavKey): Boolean
}

internal interface RouterController {
    @MainThread
    fun attach(backStack: MutableList<NavKey>, canNavigate: () -> Boolean)

    @MainThread
    fun detach()
}

internal class StackRouter : Router, RouterController {
    private class Binding(
        val backStack: MutableList<NavKey>,
        val canNavigate: () -> Boolean,
    )

    private var binding: Binding? = null

    override fun attach(backStack: MutableList<NavKey>, canNavigate: () -> Boolean) {
        check(binding == null)
        binding = Binding(backStack, canNavigate)
    }

    override fun detach() {
        binding = null
    }

    override fun open(from: NavKey, destination: NavKey): Boolean {
        val backStack = activeStack(from) ?: return false
        if (destination in backStack) return false
        backStack.add(destination)
        return true
    }

    override fun back(from: NavKey): Boolean {
        val backStack = activeStack(from) ?: return false
        if (backStack.size <= 1) return false
        backStack.removeAt(backStack.lastIndex)
        return true
    }

    private fun activeStack(from: NavKey): MutableList<NavKey>? {
        val current = binding ?: return null
        return current.backStack.takeIf { current.canNavigate() && it.lastOrNull() == from }
    }
}
