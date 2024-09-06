document.getElementById("registerForm").addEventListener("submit", function(event) {
	event.preventDefault();

	const formData = new FormData(this);

	fetch('/api/users/register', {
		method: 'POST',
		body: formData
	})
		.then(response => {
			if (response.ok) {
				alert("Registration successful! Redirecting to login...");
				window.location.href = "/login"; // Redirect to the login page on success
			} else {
				return response.text().then(text => { throw new Error(text) });
			}
		})
		.catch(error => {
			alert("Registration failed: " + error.message); // Display an error message if registration fails
		});
});