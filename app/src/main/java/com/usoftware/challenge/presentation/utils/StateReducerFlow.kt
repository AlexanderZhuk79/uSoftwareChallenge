package com.usoftware.challenge.presentation.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalForInheritanceCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.Channel.Factory.BUFFERED
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.SharingStarted.Companion.Eagerly
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.stateIn

@OptIn(ExperimentalForInheritanceCoroutinesApi::class)
interface StateReducerFlow<STATE, EVENT> : StateFlow<STATE> {
    fun handleEvent(event: EVENT)
}

fun <STATE, EVENT> ViewModel.StateReducerFlow(
    initialState: STATE,
    reduceState: (STATE, EVENT) -> STATE,
): StateReducerFlow<STATE, EVENT> = StateReducerFlow(initialState, reduceState, viewModelScope)

fun <STATE, EVENT> StateReducerFlow(
    initialState: STATE,
    reduceState: (STATE, EVENT) -> STATE,
    scope: CoroutineScope
): StateReducerFlow<STATE, EVENT> = StateReducerFlowImpl(initialState, reduceState, scope)

private class StateReducerFlowImpl<STATE, EVENT>(
    initialState: STATE,
    reduceState: (STATE, EVENT) -> STATE,
    scope: CoroutineScope
) : StateReducerFlow<STATE, EVENT> {

    private val events = Channel<EVENT>(BUFFERED)

    private val stateFlow = events
        .receiveAsFlow()
        .runningFold(initialState, reduceState)
        .stateIn(scope, Eagerly, initialState)

    override val replayCache get() = stateFlow.replayCache

    override val value get() = stateFlow.value

    override suspend fun collect(collector: FlowCollector<STATE>): Nothing {
        stateFlow.collect(collector)
    }

    override fun handleEvent(event: EVENT) {
        val delivered = events.trySend(event).isSuccess
        if (!delivered) {
            error("Missed event $event! You are doing something wrong during state transformation.")
        }
    }
}