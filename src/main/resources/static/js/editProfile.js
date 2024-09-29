// Ensure auth.js is included before this script

// Add CSS for image sizing and centering
const style = document.createElement('style');
style.textContent = `
    .profile-image-container {
        width: 200px;
        height: 200px;
        margin: 0 auto;
        position: relative;
        overflow: hidden;
        border-radius: 50%;
    }
    #currentProfilePicture {
        width: 100%;
        height: 100%;
        object-fit: cover;
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
    }
`;
document.head.appendChild(style);

const PLACEHOLDER_IMAGE_URL = 'https://res.cloudinary.com/dwk6jdkay/image/upload/v1727561897/placeholder_zif0lg.svg';

document.addEventListener("DOMContentLoaded", function() {
	if (!checkAuthentication()) return;
	const profileForm = document.getElementById("profileForm");
	const currentProfilePicture = document.getElementById('currentProfilePicture');
	const removeImageBtn = document.getElementById('removeImageBtn');
	const profileImageInput = document.getElementById('profileImageInput');

	// Wrap image in container div
	wrapImageInContainer(currentProfilePicture);

	// Load current user profile data
	loadUserProfile();

	// Handle form submission
	profileForm.addEventListener("submit", function(event) {
		event.preventDefault();
		updateProfile();
	});

	const logoutBtn = document.getElementById("logoutBtn");
	if (logoutBtn) {
		logoutBtn.addEventListener("click", function(event) {
			event.preventDefault();
			logout(); // Call the logout function from auth.js
		});
	}

	// Handle remove image button click
	removeImageBtn.addEventListener("click", function(event) {
		event.preventDefault();
		removeProfileImage();
	});

	// Show/hide remove button based on file input and current image
	profileImageInput.addEventListener("change", function() {
		updateRemoveButtonVisibility();
	});
});

function wrapImageInContainer(imgElement) {
	if (!imgElement.parentElement.classList.contains('profile-image-container')) {
		const container = document.createElement('div');
		container.className = 'profile-image-container';
		imgElement.parentNode.insertBefore(container, imgElement);
		container.appendChild(imgElement);
	}
}

async function loadUserProfile() {
	try {
		const response = await fetchWithToken('/api/users/profile', {
			method: 'GET'
		});

		if (response.ok) {
			const profileData = await response.json();
			populateForm(profileData);
		} else {
			throw new Error("Failed to load profile data");
		}
	} catch (error) {
		console.error("Error loading profile:", error);
		alert("Error loading profile: " + error.message);
	}
}

function populateForm(profileData) {
	document.getElementById("firstName").value = profileData.firstName || '';
	document.getElementById("lastName").value = profileData.lastName || '';
	document.getElementById("username").value = profileData.username || '';
	document.getElementById("email").value = profileData.email || '';
	document.getElementById("phoneNumber").value = profileData.phoneNumber || '';
	document.getElementById("address").value = profileData.address || '';

	const currentProfilePicture = document.getElementById("currentProfilePicture");

	if (profileData.profileImageUrl) {
		currentProfilePicture.src = profileData.profileImageUrl;
	} else {
		currentProfilePicture.src = PLACEHOLDER_IMAGE_URL;
	}

	updateRemoveButtonVisibility();

	// Reset the file input
	document.getElementById("profileImageInput").value = "";
}

function updateRemoveButtonVisibility() {
	const currentProfilePicture = document.getElementById("currentProfilePicture");
	const removeImageBtn = document.getElementById("removeImageBtn");
	const profileImageInput = document.getElementById("profileImageInput");

	removeImageBtn.style.display =
		(currentProfilePicture.src !== PLACEHOLDER_IMAGE_URL || profileImageInput.files.length > 0)
			? "block"
			: "none";
}

async function updateProfile() {
	const formData = new FormData(document.getElementById("profileForm"));

	// Append profile image if selected
	const profileImageInput = document.getElementById("profileImageInput");
	if (profileImageInput.files.length > 0) {
		formData.append("profileImage", profileImageInput.files[0]);
	}

	try {
		const response = await fetchWithToken('/api/users/profile', {
			method: 'PUT',
			body: formData
		});

		if (response.ok) {
			const contentType = response.headers.get("content-type");
			if (contentType && contentType.indexOf("application/json") !== -1) {
				const updatedProfile = await response.json();
				populateForm(updatedProfile);
			} else {
				// If the response is not JSON, just reload the profile
				await loadUserProfile();
			}
			alert("Profile updated successfully!");
		} else {
			const errorText = await response.text();
			throw new Error(errorText || "Failed to update profile");
		}
	} catch (error) {
		console.error("Error updating profile:", error);
		alert("Error updating profile: " + error.message);
	}
}

async function removeProfileImage() {
	try {
		const response = await fetchWithToken('/api/users/profile/image', {
			method: 'DELETE'
		});

		if (response.ok) {
			const currentProfilePicture = document.getElementById("currentProfilePicture");
			currentProfilePicture.src = PLACEHOLDER_IMAGE_URL;
			updateRemoveButtonVisibility();
			document.getElementById("profileImageInput").value = "";
			alert("Profile image removed successfully!");
		} else {
			throw new Error("Failed to remove profile image");
		}
	} catch (error) {
		console.error("Error removing profile image:", error);
		alert("Error removing profile image: " + error.message);
	}
}

function checkAuthentication() {
	const accessToken = getAccessToken(); // Use getAccessToken from auth.js
	if (!accessToken) {
		window.location.href = '/signIn';
		return false;
	}
	return true;
}