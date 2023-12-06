package com.timejar.app.api.supabase

import android.app.Application
import com.timejar.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue

class Supabase: Application() {
    override fun onCreate() {
        super.onCreate()
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_KEY

        val client = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
        }
    }
}