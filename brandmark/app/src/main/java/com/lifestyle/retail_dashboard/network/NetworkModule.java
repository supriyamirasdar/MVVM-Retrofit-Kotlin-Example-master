package com.lifestyle.retail_dashboard.network;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.lifestyle.retail_dashboard.R;
import com.lifestyle.retail_dashboard.utils.Constant;

import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.lifestyle.retail_dashboard.base.BaseApplication.getBaseAppInstance;

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
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        httpClient.connectTimeout(Constant.TIME_OUT, TimeUnit.SECONDS);
        httpClient.readTimeout(Constant.TIME_OUT, TimeUnit.SECONDS);
        httpClient.writeTimeout(Constant.TIME_OUT, TimeUnit.SECONDS);
        httpClient.addNetworkInterceptor(new AuthorizationInterceptor());
        httpClient.addNetworkInterceptor(logging);

        httpClient.sslSocketFactory(getSSLConfig().getSocketFactory());

        Gson gson = new GsonBuilder()
                .addSerializationExclusionStrategy(new JsonExclusionStrategy())
                .addDeserializationExclusionStrategy(new JsonExclusionStrategy())
                .setLenient()
                .create();
        GsonConverterFactory gsonConverterFactory = GsonConverterFactory.create(gson);

        String BASE_URL = getServerAddress();

  /*      ConnectionSpec spec = new
                ConnectionSpec.Builder(ConnectionSpec.MODERN_TLS)
                .tlsVersions(TlsVersion.TLS_1_2)
                .cipherSuites(
                        CipherSuite.TLS_ECDHE_ECDSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_ECDHE_RSA_WITH_AES_128_GCM_SHA256,
                        CipherSuite.TLS_DHE_RSA_WITH_AES_128_GCM_SHA256)
                .build();


        httpClient.connectionSpecs(Collections.singletonList(spec));*/

        Log.d("Abrar", "Base Url: " + BASE_URL);
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(httpClient.build())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(gsonConverterFactory)
                .build();
        mApiInterface = retrofit.create(ApiService.class);
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
            sApiClient = new NetworkModule();
        } catch (Exception e) {
            Log.d("Abrar", "Exception: " + e.getLocalizedMessage());
            e.printStackTrace();
        }
        return sApiClient;
    }

    public ApiService getApiInterface() {
        return mApiInterface;
    }


    public String getServerAddress() {
        String serverAdress = getBaseAppInstance().getResources().getString(R.string.server_address_internet);
        ;
        /*try {
            if (PreferenceUtils.get_intranet_Contion_flag() != null) {
                if (PreferenceUtils.get_intranet_Contion_flag().equalsIgnoreCase("true")) {
                    //intranet
                    serverAdress =  StylusApp.getInstance().getResources().getString(R.string.server_address_intranet);
                } else {
                    if (PreferenceUtils.get_STORENETWORK().equalsIgnoreCase("true")) {
                        //intranet
                        serverAdress = StylusApp.getInstance().getResources().getString(R.string.server_address_intranet);
                    } else {
                        //internet
                        serverAdress = StylusApp.getInstance().getResources().getString(R.string.server_address_internet);
                    }
                }
            } else {
                //internet

            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        return serverAdress;
    }

    private static SSLContext getSSLConfig() throws CertificateException, KeyStoreException, IOException, NoSuchAlgorithmException, KeyManagementException {

        // Loading CAs from an InputStream
        CertificateFactory cf = null;
        cf = CertificateFactory.getInstance("X.509");

        Certificate ca;
        // I'm using Java7. If you used Java6 close it manually with finally.
        InputStream cer = getBaseAppInstance().getResources().getAssets().open(getBaseAppInstance().getResources().getString(R.string.uarCertificate));
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
        Log.d("Abrar", "SSL: " + sslContext);
        return sslContext;
    }
}