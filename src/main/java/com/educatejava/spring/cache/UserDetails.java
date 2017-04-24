package com.educatejava.spring.cache;

public class UserDetails {
	private String userId;
	private String firstName;

	public UserDetails() {
	}

	public UserDetails(String userId, String firstName) {
		this.userId = userId;
		this.firstName = firstName;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "UserDetails [userId=" + userId + ", firstName=" + firstName + "]";
	}
	
	
}