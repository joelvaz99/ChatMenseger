package com.androiddevs.firebasenotifications

import com.androiddevs.firebasenotifications.Constants.Companion.CONTENT_TYPE
import com.androiddevs.firebasenotifications.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=AAAAnHLWacQ:APA91bHqw3_tPnJTTiKDloYducULekxfqFlfHtyOvgsDHSmCG7qB_YzJbz-08A4YL5CcmNZnHYXeGa6_ZFw_XJB1caSO2mvFfV8dpEZnbKg_2-8R97jRPw-LEHlIompbOwP-_sFC5I60", "Content-Type:application/json")
    @POST("fcm/send")
    suspend fun postNotification(
        @Body notification: PushNotification
    ): Response<ResponseBody>
}