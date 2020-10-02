package in.newdevpoint.sschat.webService;

import android.app.Application;

public class WebServiceRepository {

	private APIInterface apiInterface;
	private Application application;

	public WebServiceRepository(Application application) {
		this.application = application;
		apiInterface = APIClient.getClient().create(APIInterface.class);
	}
}
