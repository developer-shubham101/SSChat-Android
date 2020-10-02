package in.newdevpoint.sschat.model;

import java.util.Map;

public class FSUsersModel {

	private String name = "";
	private String email = "";
	private String id;
	private String profile_image = "";
//    private boolean isOnline  = false;


	public FSUsersModel() {
	}

	public FSUsersModel(Map<String, Object> usersData) {

		email = (String) usersData.get("email");
		name = (String) usersData.get("email");
		id = (String) usersData.get("userID");
		profile_image = (String) usersData.get("profile_image");

	}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getProfile_image() {
        return profile_image;
    }

    public void setProfile_image(String profile_image) {
        this.profile_image = profile_image;
    }
}
