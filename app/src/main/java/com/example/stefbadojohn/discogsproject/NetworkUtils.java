package com.example.stefbadojohn.discogsproject;

import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;

import retrofit2.HttpException;

public class NetworkUtils {
    enum RequestType {
        TEMP_TOKEN, ACCESS_TOKEN, AUTHORIZED
    }

    public static String createOAuthHeader(
            RequestType type, String key, String secret, String oauthToken,
            String oauthTokenSecret, String usersVerifier, String callbackUrl
    ) {
        Long ts = System.currentTimeMillis() / 1000;
        String tsString = ts.toString();

        String callback = "";
        String token = "";
        String verifier = "";
        String tokenSecret = "";
        String timestamp = "";

        switch (type) {
            case TEMP_TOKEN:
                callback = "oauth_callback=\"" + callbackUrl + "\"";
                timestamp = "oauth_timestamp=\"" + tsString + "\", ";
                break;
            case ACCESS_TOKEN:
                token = "oauth_token=\"" + oauthToken + "\", ";
                verifier = "oauth_verifier=\"" + usersVerifier + "\"";
                tokenSecret = oauthTokenSecret;
                timestamp = "oauth_timestamp=\"" + tsString + "\", ";
                break;
            case AUTHORIZED:
                token = "oauth_token=\"" + oauthToken + "\", ";
                tokenSecret = oauthTokenSecret;
                timestamp = "oauth_timestamp=\"" + tsString + "\"";
                break;
        }

        String oauthHeader = "OAuth " +
                "oauth_consumer_key=\"" + key + "\", " +
                "oauth_nonce=\"" + tsString + "\", " +
                token +
                "oauth_signature=\"" + secret + "&" + tokenSecret + "\", " +
                "oauth_signature_method=\"PLAINTEXT\", " +
                timestamp +
                verifier +
                callback;

        Log.d("OAuth Header", "" + oauthHeader);

        return oauthHeader;
    }

    public static void loadImageByInternetUrl(String imgUrl, ImageView imageView) {
        Picasso.get()
                .load(imgUrl)
                .resize(250, 250)
                .centerCrop()
                .placeholder(R.mipmap.no_cover)
                .error(R.mipmap.no_cover)
                .into(imageView);
    }

    public static String networkExceptionHandle(Throwable e) {
        if (e instanceof HttpException) {
            if (((HttpException) e).response().code() == 404) {
                return "Not found!";
            }
            return e.getMessage();
        } else if (e instanceof IOException) {
            if (e.getMessage().contains("resolve")) {
                return "Unable to contact Discogs!";
            } else {
                Log.e("NetUtils: IOException", e.getMessage());
                return "IO Error!";
            }
        }
        return "Error";
    }
}
