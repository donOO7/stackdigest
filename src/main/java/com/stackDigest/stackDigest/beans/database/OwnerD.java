package com.stackDigest.stackDigest.beans.database;

import javax.persistence.*;

@Entity
@Inheritance(strategy = InheritanceType.JOINED)
//@MappedSuperclass
public class OwnerD {

	@Id
	private int assetId;
	@Column
	private String profileImage;
	@Column
	private String displayName;
	@Column
	private String ownerLink;

	public OwnerD() {
	}

	public int getAssetId() {
		return assetId;
	}

	public void setAssetId(int assetId) {
		this.assetId = assetId;
	}

	public String getProfileImage() {
		return profileImage;
	}

	public void setProfileImage(String profileImage) {
		this.profileImage = profileImage;
	}

	public String getDisplayName() {    
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getOwnerLink() {
		return ownerLink;
	}

	public void setOwnerLink(String ownerLink) {
		this.ownerLink = ownerLink;
	}

	@Override
	public String toString() {
		return "Owner{" +
				"assetId=" + assetId +
				", ssprofileImage='" + profileImage + '\'' +
				", ssdisplayName='" + displayName + '\'' +
				", ownerLink='" + ownerLink + '\'' +
				'}';
	}
}
