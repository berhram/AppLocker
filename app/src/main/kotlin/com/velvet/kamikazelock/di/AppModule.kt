package com.velvet.kamikazelock.di

import androidx.room.Room
import com.velvet.kamikazelock.bg.AppForegroundFlow
import com.velvet.kamikazelock.bg.NotificationManager
import com.velvet.kamikazelock.bg.PermissionChecker
import com.velvet.kamikazelock.data.infra.ValidationStatus
import com.velvet.kamikazelock.data.AppRepository
import com.velvet.kamikazelock.data.room.AppDatabase
import com.velvet.kamikazelock.ui.main.MainViewModel
import com.velvet.kamikazelock.ui.overlay.OverlayViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel

import org.koin.dsl.module

val appModule = module {
    viewModel {
        MainViewModel(repository = get())
    }

    viewModel { (appName : String) ->
        OverlayViewModel(appName = appName, successFlow = get(), passwordFlow = get())
    }

    factory {  }

    //TODO cache

    single {
        MutableSharedFlow<ValidationStatus>()
    }

    single {
        MutableSharedFlow<String>()
    }

    factory {
        AppRepository(androidContext().packageManager, lockedAppsDao = get())
    }

    factory {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DB_NAME).build().appDao()
    }

    factory {
        Room.databaseBuilder(androidContext(), AppDatabase::class.java, AppDatabase.DB_NAME).build().passwordDao()
    }

    factory {
        AppForegroundFlow(context = androidContext(), permissionChecker = get())
    }

    //TODO check performance factory vs single, pros and cons

    factory {
        PermissionChecker(androidContext())
    }

    factory {
        NotificationManager(androidContext())
    }


}