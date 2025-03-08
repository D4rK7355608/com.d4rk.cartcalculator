package com.d4rk.cartcalculator.core.domain.usecases

interface Repository<T , R> {
    suspend operator fun invoke(param : T) : R
}