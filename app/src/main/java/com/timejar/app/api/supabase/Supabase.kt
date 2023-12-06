package com.timejar.app.api.supabase

import android.app.Application
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue

class Supabase: Application() {
    override fun onCreate() {
        super.onCreate()
        val supabaseUrl = "YOUR_SUPABASE_URL" // Replace with your Supabase URL
        val supabaseKey = "YOUR_SUPABASE_ANON_KEY" // Replace with your Supabase anon key

        val client = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
        }
    }
}