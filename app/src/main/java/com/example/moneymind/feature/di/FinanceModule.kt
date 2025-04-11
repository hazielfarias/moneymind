package com.example.moneymind.feature.di

import android.content.Context
import androidx.room.Room
import com.example.moneymind.feature.cases.expense.AddExpenseUseCase
import com.example.moneymind.feature.cases.expense.DeleteExpenseUseCase
import com.example.moneymind.feature.cases.expense.GetExpensesUseCase
import com.example.moneymind.feature.local.AppDatabase
import com.example.moneymind.feature.local.dao.FinanceDao
import com.example.moneymind.feature.repository.FinanceRepository
import com.example.moneymind.feature.repository.FinanceRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FinanceModule {


    // room
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "money-mind.db"
        ).fallbackToDestructiveMigration().build()
    }

    // dao
    @Provides
    fun provideFinanceDao(database: AppDatabase): FinanceDao {
        return database.financeDao()
    }

    // repository
    @Provides
    @Singleton
    fun provideFinanceRepository(
        financeDao: FinanceDao,
    ): FinanceRepository {
        return FinanceRepositoryImpl(financeDao)
    }

    // cases

    @Provides
    fun provideGetExpensesUseCase(repository: FinanceRepository): GetExpensesUseCase {
        return GetExpensesUseCase(repository)
    }

    @Provides
    fun provideAddExpenseUseCase(repository: FinanceRepository): AddExpenseUseCase {
        return AddExpenseUseCase(repository)
    }

    @Provides
    fun provideDeleteExpenseUseCase(repository: FinanceRepository): DeleteExpenseUseCase {
        return DeleteExpenseUseCase(repository)
    }

}