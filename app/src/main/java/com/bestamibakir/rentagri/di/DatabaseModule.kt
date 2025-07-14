package com.bestamibakir.rentagri.di

import android.content.Context
import androidx.room.Room
import com.bestamibakir.rentagri.data.database.MarketItemDao
import com.bestamibakir.rentagri.data.database.ListingDao
import com.bestamibakir.rentagri.data.database.RentAgriDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideRentAgriDatabase(@ApplicationContext context: Context): RentAgriDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            RentAgriDatabase::class.java,
            RentAgriDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideMarketItemDao(database: RentAgriDatabase): MarketItemDao {
        return database.marketItemDao()
    }

    @Provides
    fun provideListingDao(database: RentAgriDatabase): ListingDao {
        return database.listingDao()
    }
} 