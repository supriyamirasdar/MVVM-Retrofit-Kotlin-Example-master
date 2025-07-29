package com.lifestyle.retail.networking;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.landmarkgroup.smartkiosk.R;
import com.lifestyle.retail.StylusApp;
import com.lifestyle.retail.utils.GeneralConstant;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class NetworkModule {
    private static NetworkModule sApiClient;

    static {
        try {
            sApiClient = new NetworkModule();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private ApiService mApiInterface;
    private Retrofit retrofit;

    public Retrofit getRetrofit() {
        return retrofit;
    }

    private NetworkModule() throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
            KeyManagementException, IOException {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
       // httpClient.sslSocketFactory(getSSLConfig().getSocketFactory());
        //httpClient.sslSocketFactory()
        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new JsonExclusionStrategy())
                .addDeserializationExclusionStrategy(new JsonExclusionStrategy())
                .setLenient()
                .create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient();
        httpClient.connectTimeout(GeneralConstant.TIME_OUT, TimeUnit.SECONDS);
        httpClient.readTimeout(GeneralConstant.TIME_OUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(GeneralConstant.TIME_OUT, TimeUnit.SECONDS);
        httpClient.addNetworkInterceptor(new AuthorizationInterceptor());
        httpClient.addNetworkInterceptor(logging);
        //httpClient.hostnameVerifier(new DefaultHostnameVerifier());
        httpClient.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                return true;
            }
        });
        /*httpClient.hostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true;
                *//*HostnameVerifier hv =
                        HttpsURLConnection.getDefaultHostnameVerifier();
                return hv.verify("ipage.com", session);*//*

                Certificate[] certs;
                try {
                    certs = session.getPeerCertificates();
                } catch (SSLException e) {
                    return false;
                }
                X509Certificate x509 = (X509Certificate) certs[0];
                // We can be case-insensitive when comparing the host we used to
                // establish the socket to the hostname in the certificate.
                String hostName = hostname.trim().toLowerCase(Locale.ENGLISH);
                // Verify the first CN provided. Other CNs are ignored. Firefox, wget,
                // curl, and Sun Java work this way.
                String firstCn = getFirstCn(x509);
                if (matches(hostName, firstCn)) {
                    return true;
                }
                for (String cn : getDNSSubjectAlts(x509)) {
                    if (matches(hostName, cn)) {
                        return true;
                    }
                }
                return false;
            }
        });*/
        String BASE_URL = getServerAddress();
        Log.d("Abrar", "Base Url: " + BASE_URL);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build();
        mApiInterface = retrofit.create(ApiService.class);
    }

    private String getFirstCn(X509Certificate cert) {
        String subjectPrincipal = cert.getSubjectX500Principal().toString();
        for (String token : subjectPrincipal.split(",")) {
            int x = token.indexOf("CN=");
            if (x >= 0) {
                return token.substring(x + 3);
            }
        }
        return null;
    }
    public <T> Observable<T> background(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public <T> T create(Class<T> aClass) {
        return retrofit.create(aClass);
    }

    public static NetworkModule getInstance() {
        try {
            /*if (PreferenceUtils.get_IS_LOGIN() != null &&
                    PreferenceUtils.get_IS_LOGIN().equalsIgnoreCase("false"))*/
                sApiClient = new NetworkModule();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return sApiClient;
    }

    public ApiService getApiInterface() {
        return mApiInterface;
    }


    public String getServerAddress() {
        String serverAdress = "";
        try {
            serverAdress = StylusApp.getInstance().getResources().getString(R.string.server_address_internet);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return serverAdress;
    }

    private static SSLContext getSSLConfig() throws CertificateException, IOException,
            KeyStoreException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        InputStream cer = StylusApp.mInstance.getResources().getAssets().open(StylusApp.mInstance.getResources().getString(R.string.uarCertificate));
        ca = cf.generateCertificate(cer);

        // Creating a KeyStore containing our trusted CAs
        String keyStoreType = KeyStore.getDefaultType();
        KeyStore keyStore = KeyStore.getInstance(keyStoreType);
        keyStore.load(null, null);
        keyStore.setCertificateEntry("ca", ca);

        // Creating a TrustManager that trusts the CAs in our KeyStore.
        String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
        tmf.init(keyStore);

        // Creating an SSLSocketFactory that uses our TrustManager
        SSLContext sslContext = SSLContext.getInstance("TLS");
        sslContext.init(null, tmf.getTrustManagers(), null);
        return sslContext;
    }
}