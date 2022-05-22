package com.velvet.applocker.di

import androidx.room.Room
import com.velvet.applocker.data.*
import com.velvet.applocker.data.cache.app.AppCache
import com.velvet.applocker.data.cache.app.AppCacheContract
import com.velvet.applocker.data.cache.overlay.OverlayCache
import com.velvet.applocker.data.cache.overlay.OverlayCacheContract
import com.velvet.applocker.data.room.AppDatabase
import com.velvet.applocker.ui.main.MainViewModel
import com.velvet.applocker.ui.overlay.OverlayViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.binds

import org.koin.dsl.module

val appModule = module {
    viewModel {
        MainViewModel(appRepository = get(), appCache = get(), permissionChecker = get(), passwordRepository = get())
    }

    viewModel { OverlayViewModel(clientCache = get()) }

    single {
        OverlayCache()
    }.binds(arrayOf(
        OverlayCacheContract.ActivityCache::class,
        OverlayCacheContract.UiCache::class,
        OverlayCacheContract.RepositoryCache::class))

    single {
        AppCache()
    }.binds(arrayOf(
        AppCacheContract.UiCache::class,
        AppCacheContract.RepositoryCache::class))

    single {
        AppRepository(appName = androidContext().packageName, androidContext().packageManager, lockedAppsDao = get(), appCache = get())
    }

    single {
        PasswordRepository(passwordDao = get(), overlayCache = get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DB_NAME).build().appDao()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DB_NAME).build().passwordDao()
    }

    single {
        CurrentAppChecker(context = androidContext(), permissionChecker = get())
    }

    factory {
        PermissionChecker(androidContext())
    }

    single {
        NotificationManager(androidContext())
    }


}