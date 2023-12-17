package com.timejar.app.api.supabase

import android.app.Application
import androidx.compose.ui.text.toLowerCase
import com.timejar.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.GoTrue
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.gotrue
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.util.Date
import java.util.Locale

data class User(
    val id: String,
    val firstName: String,
    val lastName: String,
    val dateOfBirth: Date,
    val sex: Int,
)

class Supabase : Application() {
    companion object {
        lateinit var client: SupabaseClient
            private set

        private val coroutineScope = CoroutineScope(Dispatchers.IO)

        fun signUp(sexString: String,  first_name: String, last_name: String, date_of_birth: Date, userEmail: String, userPassword: String,
                   onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
            coroutineScope.launch {
                try {
                    client.gotrue.signUpWith(Email) {
                        email = userEmail
                        password = userPassword
                    }

                    val id = client.gotrue.retrieveUserForCurrentSession().id
                    val sex = if (sexString.lowercase(Locale.ROOT) === "male") 1 else 2 // male or female

                    val userInfo = User(
                        id,
                        first_name,
                        last_name,
                        date_of_birth,
                        sex
                    )
                    client.postgrest.from("users").insert(userInfo)

                    onSuccess() // Invoke the success callback
                } catch (e: Exception) {
                    onFailure(Exception("SignUp failed"))
                    onFailure(e)
                }
            }
        }

        fun login(userEmail: String, userPassword: String,
                  onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
            coroutineScope.launch {
                try {
                    client.gotrue.loginWith(Email) {
                        email = userEmail
                        password = userPassword
                    }

                    onSuccess() // Invoke the success callback
                } catch (e: Exception) {
                    onFailure(Exception("Login failed"))
                    onFailure(e)
                }
            }
        }

        fun signOut(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
            coroutineScope.launch {
                try {
                    client.gotrue.logout()

                    onSuccess() // Invoke the success callback
                } catch (e: Exception) {
                    onFailure(Exception("SignOut failed"))
                    onFailure(e)
                }
            }
        }

        fun initialAppActivity(packageName: String, eventTime: Long, location: String,
                               onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
            coroutineScope.launch {
                try {
                    val user = client.gotrue.retrieveUserForCurrentSession()

                    client.functions.invoke(
                        function = "initial-app-activity",
                        body = buildJsonObject {
                            put("userId", user.id)
                            put("packageName", packageName)
                            put("eventTime", eventTime)
                            put("location", location)
                        },
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "application/json")
                        }
                    )

                    onSuccess() // Invoke the success callback
                } catch (e: Exception) {
                    onFailure(Exception("initialAppActivity failed"))
                    onFailure(e)
                }
            }
        }

        fun endAppActivity(acceptance: Int, shouldBeBlocked: Boolean, action: Int, eventTime: Long,
                           onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
            coroutineScope.launch {
                try {
                    val user = client.gotrue.retrieveUserForCurrentSession()

                    client.functions.invoke(
                        function = "initial-app-activity",
                        body = buildJsonObject {
                            put("userId", user.id)
                            put("acceptance", acceptance)
                            put("should_be_blocked", shouldBeBlocked)
                            put("action", action)
                            put("eventTime", eventTime)
                        },
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "application/json")
                        }
                    )

                    onSuccess() // Invoke the success callback
                } catch (e: Exception) {
                    onFailure(Exception("endAppActivity failed"))
                    onFailure(e)
                }
            }
        }

    }

    override fun onCreate() {
        super.onCreate()
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_KEY

        client = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(GoTrue)
            install(Postgrest)
            install(Functions)
        }
    }
}
