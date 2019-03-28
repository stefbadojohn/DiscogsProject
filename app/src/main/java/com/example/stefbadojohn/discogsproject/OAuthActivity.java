package com.example.stefbadojohn.discogsproject;

import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.Toast;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;

public class OAuthActivity extends AppCompatActivity {

    private ProgressBar spinner;
    private WebView webView;

    private UserSessionInterface userSession = UserSessionInterface.instance;
    private NetworkInterface network = NetworkInterface.instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_oauth);

        spinner = findViewById(R.id.progressBar);
        webView = findViewById(R.id.oauthWebview);

        spinner.setVisibility(View.VISIBLE);

        if (!userSession.isLoggedIn()) {
            requestToken();
        } else {
            finish();
        }
    }

    private void requestToken() {
        Observable<String> obsTokenRequest = network.requestToken();

        obsTokenRequest.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
            }

            @Override
            public void onNext(String s) {
                Log.d("RequestToken onNext", s);

                String body[] = s.split("&");
                //Log.d("TokenRequest body", body[0] + "\n" + body[1] + "\n" + body[2]);
                if (body[0].contains("oauth_token") && body[1].contains("token_secret")) {
                    String tempToken = body[0].substring(body[0].indexOf("=") + 1);
                    String tokenSecret = body[1].substring(body[1].indexOf("=") + 1);
                    Log.d("RequestToken",
                            "tempToken: " + tempToken +
                                    "\n tokenSecret: " + tokenSecret);
                    userSession.saveUserToken(tempToken, tokenSecret);
                    openOAuthPage();
                }
            }

            @Override
            public void onError(Throwable e) {
                NetworkUtils.networkExceptionHandle(e);
            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void openOAuthPage() {
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if(!url.contains("discogsproject")) {
                    webView.setVisibility(View.VISIBLE);
                    spinner.setVisibility(View.GONE);
                    Log.d("auth page", url);
                }
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                if (url.contains("discogsproject")) {
                    Log.d("callback", url);
                    webView.setVisibility(View.GONE);
                    spinner.setVisibility(View.VISIBLE);
                    checkResponse(url);
                } else {
                    spinner.setVisibility(View.VISIBLE);
                }
            }

        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(DiscogsClient.AUTHORIZE_URL + "?oauth_token=" + userSession.getUserToken());
    }

    private void checkResponse(String url) {
        if (!url.contains("denied")) {
            String data[] = url.split("&");
            String verifier[] = data[1].split("=");

            requestAccessToken(verifier[1]);
        } else {
            Toast.makeText(OAuthActivity.this,
                    "User denied authorization!",
                    Toast.LENGTH_SHORT).show();
            userSession.logout();
            spinner.setVisibility(View.GONE);
            finish();
        }
    }

    private void requestAccessToken(String verifier) {
        Observable<String> obsAccessToken = network.getAccessToken(verifier);

        obsAccessToken.subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                if (s != null) {
                    String data[] = s.split("&");
                    userSession.saveUserToken(
                            data[0].substring(data[0].indexOf("=") + 1),
                            data[1].substring(data[1].indexOf("=") + 1)
                    );

                    spinner.setVisibility(View.GONE);

                    finish();

                }
            }

            @Override
            public void onError(Throwable e) {
                Toast.makeText(OAuthActivity.this,
                        NetworkUtils.networkExceptionHandle(e),
                        Toast.LENGTH_SHORT).show();

                spinner.setVisibility(View.GONE);

                finish();
            }

            @Override
            public void onComplete() {

            }
        });
    }
}
