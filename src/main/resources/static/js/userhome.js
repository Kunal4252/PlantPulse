// Ensure auth.js is included before this script

function checkAuthentication() {
	const accessToken = getAccessToken(); // Use getAccessToken from auth.js
	if (!accessToken) {
		window.location.href = '/signIn';
		return false;
	}
	return true;
}
document.addEventListener("DOMContentLoaded", async function() {
	if (!checkAuthentication()) {
		return; // Stop execution if not authenticated
	}
	try {
		// Use fetchWithToken from auth.js to handle token management
		const response = await fetchWithToken('/api/users/profile', {
			method: 'GET',
			headers: {
				'Content-Type': 'application/json'
			}
		});

		if (response.ok) {
			const profileData = await response.json();


			const userId = profileData.id;

			// Save userId to localStorage
			localStorage.setItem('userId', userId);
			displayUserProfile(profileData);
		} else if (response.status === 401 || response.status === 403) {
			// Unauthorized or Forbidden access

			clearTokens(); // Clear tokens using clearTokens from auth.js
			window.location.href = "/signIn"; // Redirect to login page
		} else {
			const errorText = await response.text();
			throw new Error(errorText);
		}
	} catch (error) {
		alert("Profile request failed: " + error.message);
	}
});

// Function to display user profile information
function displayUserProfile(profileData) {
	document.getElementById("username").textContent = profileData.username;

	// Update profile image
	const profilePicUrl = profileData.profileImageUrl || '/api/placeholder/100/100';
	const profileImage = document.querySelector('.profile-picture');
	if (profileImage) {
		profileImage.src = profilePicUrl;
		profileImage.alt = `${profileData.username}'s profile picture`;
	}
}


const logoutBtn = document.getElementById("logoutBtn");
if (logoutBtn) {
	logoutBtn.addEventListener("click", function(event) {
		event.preventDefault();
		logout(); // Call the logout function from auth.js
	});
}

