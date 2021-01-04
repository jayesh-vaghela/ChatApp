package com.example.chatapp.Fragments;


import com.example.chatapp.Notifications.MyResponse;
import com.example.chatapp.Notifications.Sender;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
  @Headers (
        {
            "Content-Type:application/json",
            "Authorization:key=AAAA_UZVptg:APA91bEJhMrUSGqcYOq8ekYgl4fW98AWFQN8tUVkO_a-5mUiXwGxO8lGFEkjcnd3HJFzTEBN1jYJPCqkwz5VCV5oIgscbGXzkGe8am0IGxnuED1Afu8PHQVHlQnMkAVDzpkoruHLlb-G"

        }
    )
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body Sender body);

}