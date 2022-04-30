package com.velvet.kamikazelock.di

import androidx.room.Room
import com.velvet.kamikazelock.bg.AppForegroundFlow
import com.velvet.kamikazelock.bg.NotificationManager
import com.velvet.kamikazelock.bg.PermissionChecker
import com.velvet.kamikazelock.data.AppRepository
import com.velvet.kamikazelock.data.cache.app.AppCache
import com.velvet.kamikazelock.data.cache.app.ClientAppCache
import com.velvet.kamikazelock.data.cache.app.RepositoryAppCache
import com.velvet.kamikazelock.data.cache.overlay.OverlayCache
import com.velvet.kamikazelock.data.cache.overlay.ClientOverlayCache
import com.velvet.kamikazelock.data.cache.overlay.ServiceOverlayCache
import com.velvet.kamikazelock.data.room.AppDatabase
import com.velvet.kamikazelock.ui.main.MainViewModel
import com.velvet.kamikazelock.ui.overlay.OverlayViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.binds

import org.koin.dsl.module

val appModule = module {
    viewModel {
        MainViewModel(repository = get(), appCache = get(), permissionChecker = get())
    }

    viewModel { (appName : String) ->
        OverlayViewModel(appName = appName, clientCache = get())
    }

    single {
        OverlayCache()
    }.binds(arrayOf(ServiceOverlayCache::class, ClientOverlayCache::class))

    single {
        AppCache()
    }.binds(arrayOf(ClientAppCache::class, RepositoryAppCache::class))

    single {
        AppRepository(androidContext().packageManager, lockedAppsDao = get(), appCache = get())
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DB_NAME).build().appDao()
    }

    single {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DB_NAME).build().passwordDao()
    }

    single {
        AppForegroundFlow(context = androidContext(), permissionChecker = get())
    }

    //TODO check performance factory vs single, pros and cons

    factory {
        PermissionChecker(androidContext())
    }

    single {
        NotificationManager(androidContext())
    }


}