package com.example.pushuptrainer_mobile.core.navigation

import androidx.annotation.MainThread
import androidx.navigation3.runtime.NavKey

interface Router {
    @MainThread
    fun isCurrent(from: NavigationEntry): Boolean

    @MainThread
    fun open(from: NavigationEntry, destination: Destination): Boolean

    @MainThread
    fun back(from: NavigationEntry): Boolean

    @MainThread
    fun replace(from: NavigationEntry, destination: Destination): Boolean

    @MainThread
    fun returnTo(from: NavigationEntry, destination: Destination): Boolean
}

internal interface RouterController : Router {
    @MainThread
    fun attach(backStack: MutableList<NavKey>, canNavigate: () -> Boolean): () -> Unit
}

internal class StackRouter : RouterController {
    private class Binding(
        val backStack: MutableList<NavKey>,
        val canNavigate: () -> Boolean,
    )

    private var binding: Binding? = null

    override fun attach(backStack: MutableList<NavKey>, canNavigate: () -> Boolean): () -> Unit {
        val attached = Binding(backStack, canNavigate)
        binding = attached
        return { if (binding === attached) binding = null }
    }

    override fun isCurrent(from: NavigationEntry): Boolean = activeStack(from) != null

    override fun open(from: NavigationEntry, destination: Destination): Boolean {
        val stack = activeStack(from) ?: return false
        if (stack.any { (it as NavigationEntry).destination == destination }) return false
        stack.add(destination.newEntry())
        return true
    }

    override fun back(from: NavigationEntry): Boolean {
        val stack = activeStack(from) ?: return false
        if (stack.size == 1) return false
        stack.removeAt(stack.lastIndex)
        return true
    }

    override fun replace(from: NavigationEntry, destination: Destination): Boolean {
        val stack = activeStack(from) ?: return false
        if (stack.size == 1 || stack.any { (it as NavigationEntry).destination == destination }) return false
        stack[stack.lastIndex] = destination.newEntry()
        return true
    }

    override fun returnTo(from: NavigationEntry, destination: Destination): Boolean {
        val stack = activeStack(from) ?: return false
        val index = stack.indexOfLast { (it as NavigationEntry).destination == destination }
        if (index < 0 || index == stack.lastIndex) return false
        stack.subList(index + 1, stack.size).clear()
        return true
    }

    private fun activeStack(from: NavigationEntry): MutableList<NavKey>? {
        val current = binding ?: return null
        return current.backStack.takeIf { current.canNavigate() && it.lastOrNull() == from }
    }
}
