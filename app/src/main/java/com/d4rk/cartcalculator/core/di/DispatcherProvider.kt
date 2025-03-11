package com.d4rk.cartcalculator.core.di

import kotlinx.coroutines.CoroutineDispatcher

// TODO: Lib
interface DispatcherProvider {
    val main : CoroutineDispatcher
    val io : CoroutineDispatcher
    val default : CoroutineDispatcher
    val unconfined : CoroutineDispatcher
}