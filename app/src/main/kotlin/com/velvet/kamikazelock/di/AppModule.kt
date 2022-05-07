package com.velvet.kamikazelock.di

import androidx.room.Room
import com.velvet.kamikazelock.data.*
import com.velvet.kamikazelock.data.cache.app.AppCache
import com.velvet.kamikazelock.data.cache.app.AppCacheContract
import com.velvet.kamikazelock.data.cache.overlay.OverlayCache
import com.velvet.kamikazelock.data.cache.overlay.OverlayCacheContract
import com.velvet.kamikazelock.data.room.AppDatabase
import com.velvet.kamikazelock.ui.main.MainViewModel
import com.velvet.kamikazelock.ui.overlay.OverlayViewModel
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
        AppRepository(androidContext().packageManager, lockedAppsDao = get(), appCache = get())
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