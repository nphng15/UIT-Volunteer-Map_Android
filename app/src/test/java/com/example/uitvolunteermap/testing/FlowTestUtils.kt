package com.example.uitvolunteermap.testing

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> TestScope.collectFlow(flow: Flow<T>, sink: MutableList<T>) = backgroundScope.launch(
    UnconfinedTestDispatcher(testScheduler)
) {
    flow.collect { sink += it }
}
