package com.skbankofbareilly.mobile.network;

import android.content.Context;
import com.skbankofbareilly.mobile.security.TokenManager;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class TokenInterceptor implements Interceptor {

    private final Context context;

    public TokenInterceptor(Context context) {
        this.context = context.getApplicationContext();
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        String token = TokenManager.getInstance(context).getToken();

        if (token != null && !token.trim().isEmpty()) {
            Request requestWithToken = original.newBuilder()
                    .header("Authorization", "Bearer " + token)
                    .build();
            return chain.proceed(requestWithToken);
        }

        return chain.proceed(original);
    }
}
