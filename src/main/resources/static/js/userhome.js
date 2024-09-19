// Ensure auth.js is included before this script

document.addEventListener("DOMContentLoaded", async function() {
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
			alert("Unauthorized access. Please log in again.");
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

// Add event listener for logout button
document.getElementById("logoutBtn").addEventListener("click", function(event) {
	event.preventDefault();
	clearTokens(); // Clear tokens using clearTokens from auth.js
	window.location.href = "/signIn"; // Redirect to login page
});