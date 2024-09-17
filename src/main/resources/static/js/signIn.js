document.getElementById('signinForm').addEventListener('submit', async function(e) {
	e.preventDefault();

	// `this` refers to the form element here
	const formData = new FormData(this);

	try {
		const response = await fetch('/api/users/login', {
			method: 'POST',
			body: formData
		});

		if (response.ok) {
			const data = await response.json(); // Parse JSON response

			// Save tokens to local storage
			localStorage.setItem('accessToken', data.accessToken);
			localStorage.setItem('refreshToken', data.refreshToken);

			// Redirect to user home page
			window.location.href = "/userhome";
		} else {
			const errorText = await response.text();
			throw new Error(errorText);
		}
	} catch (error) {
		alert("Sign in failed: " + error.message); // Display an error message if login fails
	}
});