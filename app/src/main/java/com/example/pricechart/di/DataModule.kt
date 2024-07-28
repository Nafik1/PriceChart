package com.example.pricechart.di

import com.example.pricechart.data.network.ApiFactory
import com.example.pricechart.data.network.ApiService
import com.example.pricechart.data.repository.PriceChartRepository
import com.example.pricechart.data.repository.PriceChartRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Singleton
    @Binds
    fun bindRepository(impl: PriceChartRepositoryImpl): PriceChartRepository

    companion object {

        @Singleton
        @Provides
        fun provideApiService(): ApiService {
            return ApiFactory.apiService
        }
    }
}