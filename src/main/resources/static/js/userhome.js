/*/document.addEventListener("DOMContentLoaded", async function() {
	try {
		const accessToken = localStorage.getItem('accessToken');
		if (!accessToken) {
			throw new Error('No access token found');
		}

		const response = await fetch('/api/users/profile', {
			method: 'GET',
			headers: {
				'Authorization': 'Bearer ' + accessToken
			}
		});

		if (response.ok) {
			const data = await response.json(); // Parse JSON response

			// Display user profile data on the page
			document.getElementById("username").textContent = data.username;
			document.getElementById("email").textContent = data.email;
			// Other data fields as needed
		} else {
			const errorText = await response.text();
			throw new Error(errorText);
		}
	} catch (error) {
		alert("Failed to fetch profile: " + error.message); // Display an error message if fetching profile fails
	}
});
*/
document.addEventListener("DOMContentLoaded", async function() {
	const accessToken = localStorage.getItem('accessToken');

	if (!accessToken) {
		alert("You are not authorized! Please log in.");
		window.location.href = "/login";
		return;
	}

	try {
		const response = await fetch('/api/users/profile', {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${accessToken}`,
				'Content-Type': 'application/json'
			}
		});

		if (response.ok) {
			const profileData = await response.json();
			displayUserProfile(profileData);
		} else if (response.status === 401 || response.status === 403) {
			alert("Unauthorized access. Please log in again.");
			localStorage.removeItem('accessToken');
			localStorage.removeItem('refreshToken');
			window.location.href = "/login";
		} else {
			const errorText = await response.text();
			throw new Error(errorText);
		}
	} catch (error) {
		alert("Profile request failed: " + error.message);
	}
});

function displayUserProfile(profileData) {
	document.getElementById("userProfileName").textContent = profileData.username;
	document.getElementById("profileUsername").textContent = profileData.username;
	document.getElementById("profileFirstName").textContent = profileData.firstName;
	document.getElementById("profileLastName").textContent = profileData.lastName;
	document.getElementById("profileEmail").textContent = profileData.email;
	document.getElementById("profilePhoneNumber").textContent = profileData.phoneNumber || 'N/A';
	document.getElementById("profileAddress").textContent = profileData.address || 'N/A';
	if (profileData.id) {
		localStorage.setItem("userId", profileData.id);
	}
	// Update welcome name
	document.getElementById("welcomeName").textContent = profileData.firstName || profileData.username;

	// Update the profile image
	const profilePicUrl = profileData.profileImageUrl || '/api/placeholder/100/100';
	const profileImages = document.querySelectorAll('.profile-image');
	profileImages.forEach(img => {
		img.src = profilePicUrl;
		img.alt = `${profileData.username}'s profile picture`;
	});
}