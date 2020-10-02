package in.newdevpoint.sschat.webService;


import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Query;


public interface APIInterface {

	@GET("v1/employee/32889")
	Call<ResponseBody> tempurl();
/*
    @POST("search-places")
    Call<ResponseBody> signUp(@Header("Content-Type") String contentType, @Body RequestBody requestBody);*/

   /* @GET("delete-notification/")
    Call<ResponseBody> getNotificationDelete(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);
*/

	@POST("search-places")
	Call<ResponseBody> getPlacesList(@Query("page") int page, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

	@POST("login")
	Call<ResponseBody> login(@Header("Content-Type") String contentType, @Body RequestBody requestBody);

	@POST("signup")
	Call<ResponseBody> signup(@Header("Content-Type") String contentType, @Body RequestBody requestBody);


	@POST("otp-send")
	Call<ResponseBody> sendOtp(@Header("Content-Type") String contentType, @Body RequestBody requestBody);

	@POST("otp-check")
	Call<ResponseBody> otpCheck(@Header("Content-Type") String contentType, @Body RequestBody requestBody);

	@POST("reset-password")
	Call<ResponseBody> resetPassword(@Header("Content-Type") String contentType, @Body RequestBody requestBody);

	@GET("list-places")
	Call<ResponseBody> listPlaces(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

	@GET("list-season")
	Call<ResponseBody> listSeason(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

	@POST("destination-list-by-place")
	Call<ResponseBody> destinationListData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("hotel-list-by-place")
	Call<ResponseBody> hotelListData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);


	@POST("food-list-by-place")
	Call<ResponseBody> foodListData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("destination-likes")
	Call<ResponseBody> destinationLikeList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("destination-comments-list")
	Call<ResponseBody> commentList(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("destination-comments")
	Call<ResponseBody> addCommentData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("like-destination")
	Call<ResponseBody> addLikeOnDestinationData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("place-details")
	Call<ResponseBody> getPlaceDetailData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("like-place")
	Call<ResponseBody> addLikeOnPlace(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);


	@POST("list-advise")
	Call<ResponseBody> adviseList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("add-advise")
	Call<ResponseBody> uploadAdvise(//@Header("Content-Type") String contentType,
									@Header("Authorization") String authorization,
									@Body MultipartBody requestBody);

	@POST("place-follows")
	Call<ResponseBody> placeFollowers(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);


	@POST("save-place-destination")
	Call<ResponseBody> saveDestinationData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("save-place-food")
	Call<ResponseBody> saveFoodData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("save-place-hotels")
	Call<ResponseBody> saveHotelData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("user-tag-list")
	Call<ResponseBody> getTagUserListData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("single-destination")
	Call<ResponseBody> getSingleSharedDestinationData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("single-food")
	Call<ResponseBody> getSingleSharedFoodData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("single-hotel")
	Call<ResponseBody> getSingleSharedHotelData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("destination-tag")
	Call<ResponseBody> sendDestinationTagUserListData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("food-tag")
	Call<ResponseBody> sendFoodTagUserListData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("hotel-tag")
	Call<ResponseBody> sendHotelTagUserListData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("traveler-details")
	Call<ResponseBody> getTravelDetailData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("user-post-list")
	Call<ResponseBody> userPostList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@GET("my-post-list")
	Call<ResponseBody> myPostList(@Query("page") int page, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

	@GET("post-list")
	Call<ResponseBody> allPostList(@Query("page") int page, @Header("Content-Type") String contentType, @Header("Authorization") String authorization);

	@POST("post-like")
	Call<ResponseBody> addLikeOnPost(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("post-comment")
	Call<ResponseBody> addCommentOnTravelerPostData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);


	@POST("post-comment-list")
	Call<ResponseBody> commentListDataForTravelerPost(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("post-save")
	Call<ResponseBody> travelerPostSaveData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("single-post")
	Call<ResponseBody> travelerSinglePostShareData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("post-tag")
	Call<ResponseBody> sendTravelerPostTagUserListData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@GET("report-types")
	Call<ResponseBody> allReportList(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

	@POST("post-report")
	Call<ResponseBody> sendReportOnTravlerPost(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);


	@POST("delete-post")
	Call<ResponseBody> deleteTravlerPost(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("user-follower-list")
	Call<ResponseBody> placeFollowers(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("user-following-list")
	Call<ResponseBody> userFollowingList(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("add-post")
	Call<ResponseBody> uploadPostData(@Header("Authorization") String authorization, @Body MultipartBody requestBody);

	@GET("user-profile")
	Call<ResponseBody> userProfileData(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);

	@POST("user-update-profile")
	Call<ResponseBody> updateUserProfileData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);


	@POST("user-update-profile")
	Call<ResponseBody> getUpdateUserProfileData(@Header("Authorization") String authorization, @Body MultipartBody requestBody);

	@POST("update-post")
	Call<ResponseBody> editPost(@Header("Authorization") String authorization, @Body MultipartBody requestBody);

	@POST("delete-post-image")
	Call<ResponseBody> deleteMediaFromPostData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("follow-place")
	Call<ResponseBody> followPlaceData(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("post-tags-list")
	Call<ResponseBody> tagPostList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("food-tags-list")
	Call<ResponseBody> tagFoodList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("destination-tags-list")
	Call<ResponseBody> tagDestinationList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("hotel-tags-list")
	Call<ResponseBody> tagHotelList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);


	@POST("save-post-list")
	Call<ResponseBody> savePostList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("save-hotels-list")
	Call<ResponseBody> saveHotelList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("save-food-list")
	Call<ResponseBody> saveFoodList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("save-place-destination-list")
	Call<ResponseBody> saveDestinationList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("list-user-advise")
	Call<ResponseBody> TravelerAdviseList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("delete-advise")
	Call<ResponseBody> deleteTravelerAdvise(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("travel-plan-list")
	Call<ResponseBody> travelPlanList(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);


	@POST("check-in-user-list-travel")
	Call<ResponseBody> checkInList(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("add-travel-plan")
	Call<ResponseBody> addTravelPlan(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("travelers-list")
	Call<ResponseBody> travelersList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("user-follow")
	Call<ResponseBody> addFollowActionOnTravler(@Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@POST("place-follow-list")
	Call<ResponseBody> userFollowPlaceList(@Query("page") int page, @Header("Content-Type") String contentType, @Body RequestBody requestBody, @Header("Authorization") String authorization);

	@GET("logout")
	Call<ResponseBody> logout(@Header("Content-Type") String contentType, @Header("Authorization") String authorization);


	@GET("latest")
	Call<ResponseBody> latest(@Query("access_key") String access_key, @Query("base") String base);



	@GET("convert")
	Call<ResponseBody> convert(@Query("access_key") String access_key, @Query("from") String from, @Query("to") String to, @Query("amount") double amount);


}


