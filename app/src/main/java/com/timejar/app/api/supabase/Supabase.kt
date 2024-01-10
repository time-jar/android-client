package com.timejar.app.api.supabase

import android.app.Application
import android.util.Log
import android.widget.Toast
import com.timejar.app.BuildConfig
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.gotrue.Auth
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.functions.Functions
import io.github.jan.supabase.functions.functions
import io.github.jan.supabase.gotrue.AuthConfig
import io.github.jan.supabase.gotrue.SessionStatus
import io.github.jan.supabase.gotrue.auth
import io.github.jan.supabase.gotrue.providers.builtin.Email
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Order
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Serializable
data class User(
    val id: String,
    val first_name: String,
    val last_name: String,
    @Serializable(with = DateSerializer::class)
    val date_of_birth: Date,
    val sex: Int,
)

@Serializable
data class UserAppUsage(
    val id: Long,
    val created_at: String,
    val app_name: Long,
    val user_id: String,
    val acceptance: Long?,
    val should_be_blocked: Boolean?,
    val action: Int?,
    val location: Int,
    val weekday: Int,
    val time_of_day: String,
    val app_usage_time: Long?
)

@Serializable
data class ResponseData(
    val message: String,
    val shouldBlock: Boolean
)

object DateSerializer : KSerializer<Date> {
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("Date", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Date) {
        val stringFormat = dateFormat.format(value)
        encoder.encodeString(stringFormat)
    }

    override fun deserialize(decoder: Decoder): Date {
        return dateFormat.parse(decoder.decodeString())!!
    }
}

class Supabase : Application() {
    companion object {
        fun getAppActivityEvents(
            onSuccess: (List<UserAppUsage>) -> Unit,
            onFailure: (Throwable) -> Unit
        ) {
            coroutineScope.launch {
                try {
                    val id = client.auth.retrieveUserForCurrentSession().id

                    val response = client.postgrest.from("user_app_usage")
                        .select {
                            order(column = "created_at", order = Order.DESCENDING)
                            limit(10)
                            filter {
                                eq("user_id", id)
                            }
                        }.decodeList<UserAppUsage>()

                    onSuccess(response)
                } catch (e: Exception) {
                    onFailure(Exception("getAppActivityEvents failed: ${e.message}"))
                }
            }
        }

        lateinit var client: SupabaseClient
            private set

        private val coroutineScope = CoroutineScope(Dispatchers.IO)

        private var lastRefreshSession: Long = 0

        private fun refreshSessionIfAllowed() {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastRefreshSession > 30 * 60 * 1000) { // 30 minutes
                lastRefreshSession = currentTime

                CoroutineScope(Dispatchers.Main).launch {
                    val result = client.auth.refreshCurrentSession()
                    Log.d("isLoggedIn", "refreshing sessionStatus result: ${result.toString()}")
                }

                val sessionStatus = client.auth.sessionStatus.value
                Log.d("isLoggedIn after refresh", "sessionStatus: $sessionStatus")
            }
        }

        suspend fun signUp(sexString: String, first_name: String, last_name: String, date_of_birth: Date, userEmail: String, userPassword: String,
                           onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) = withContext(Dispatchers.Main.immediate) {
            try {
                client.auth.signUpWith(Email) {
                    email = userEmail
                    password = userPassword
                }

                val id = client.auth.retrieveUserForCurrentSession().id
                val sex = if (sexString.lowercase(Locale.ROOT) === "male") 1 else 2 // male or female

                val userInfo = User(
                    id,
                    first_name,
                    last_name,
                    date_of_birth,
                    sex
                )
                client.postgrest.from("users").insert(userInfo)

                onSuccess()
            } catch (e: Exception) {
                onFailure(Exception("SignUp failed: ${e.message}"))
            }
        }

        suspend fun login(userEmail: String, userPassword: String,
                  onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) = withContext(Dispatchers.Main.immediate) {
            try {
                client.auth.signInWith(Email, config = {
                    email = userEmail
                    password = userPassword
                })

                onSuccess()
            } catch (e: Exception) {
                onFailure(Exception("login failed: ${e.message}"))
            }
        }

        suspend fun signOut(onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) = withContext(Dispatchers.Main.immediate){
            try {
                client.auth.signOut()

                onSuccess()
            } catch (e: Exception) {
                onFailure(Exception("Sign out failed: ${e.message}"))
            }
        }

        suspend fun initialAppActivity(packageName: String, eventTime: Long, location: Int,
                                       onSuccess: (shouldBlock: Boolean) -> Unit, onFailure: (Throwable) -> Unit) {
                try {
                    val user = client.auth.retrieveUserForCurrentSession()

                    val response = client.functions.invoke(
                        function = "initial-app-activity",
                        body = buildJsonObject {
                            put("userId", user.id)
                            put("packageName", packageName)
                            put("eventTime", eventTime)
                            put("locationId", location)
                        },
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "application/json")
                        }
                    )

                    if (response.status != HttpStatusCode.OK) {
                        throw Exception("HttpStatusCode NOT OK: ${response.status}")
                    }

                    val rawResponse = response.bodyAsText()
                    val parsedResponse =  Json.decodeFromString<ResponseData>(rawResponse)

                    Log.d("initialAppActivity","parsedResponse: $parsedResponse")

                    onSuccess(parsedResponse.shouldBlock)
                } catch (e: Exception) {
                    onFailure(Exception("initialAppActivity failed: ${e.message}"))
                }
        }

        fun endAppActivity(acceptance: Int, shouldBeBlocked: Int, action: Int, eventTime: Long,
                           onSuccess: () -> Unit, onFailure: (Throwable) -> Unit) {
            coroutineScope.launch {
                try {
                    val user = client.auth.retrieveUserForCurrentSession()

                    client.functions.invoke(
                        function = "end-app-activity",
                        body = buildJsonObject {
                            put("userId", user.id)
                            put("acceptance", acceptance)
                            put("shouldBeBlocked", shouldBeBlocked)
                            put("action", action)
                            put("eventTime", eventTime)
                        },
                        headers = Headers.build {
                            append(HttpHeaders.ContentType, "application/json")
                        }
                    )

                    onSuccess()
                } catch (e: Exception) {
                    onFailure(Exception("endAppActivity failed: ${e.message}"))
                }
            }
        }

        fun isLoggedIn(): Boolean {
            val user = client.auth.currentUserOrNull()

            val sessionStatus = client.auth.sessionStatus.value
            Log.d("isLoggedIn", "sessionStatus: $sessionStatus")

            refreshSessionIfAllowed()

            return user != null
        }
    }

    override fun onCreate() {
        super.onCreate()
        val supabaseUrl = BuildConfig.SUPABASE_URL
        val supabaseKey = BuildConfig.SUPABASE_KEY

        client = createSupabaseClient(supabaseUrl, supabaseKey) {
            install(Auth)
            install(Postgrest)
            install(Functions)
        }

        val testSupabaseUrl = client.supabaseUrl
        val testSupabaseKey = client.supabaseKey

        Log.i("Supabase onCreate", "$testSupabaseUrl, $testSupabaseKey")
    }
}
