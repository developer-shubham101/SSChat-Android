package in.newdevpoint.sschat.webService;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIClient {
//	public static final String IMAGE_URL = "http://103.207.168.164:8005/storage/";
	private static final String BASE_URL = "http://testapi.newdevpoint.in/";
	private static Retrofit retrofit = null;

	public static Retrofit getClient() {
		if (retrofit == null) {
			OkHttpClient.Builder httpBuilder = new OkHttpClient.Builder();
			httpBuilder.connectTimeout(60, TimeUnit.SECONDS);
			httpBuilder.readTimeout(10, TimeUnit.MINUTES);
			httpBuilder.writeTimeout(10, TimeUnit.MINUTES);
			httpBuilder.retryOnConnectionFailure(true);
//            httpBuilder.addInterceptor(new CustomInterceptor(""));

			OkHttpClient okHttpClient = httpBuilder.build();

			//init retrofit
			retrofit = new Retrofit.Builder()
					.client(okHttpClient)
					.baseUrl(BASE_URL)
//                    .addConverterFactory(ScalarsConverterFactory.signUp())
					.addConverterFactory(GsonConverterFactory.create())

					.build();
		}
		return retrofit;
	}

}