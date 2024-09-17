document.getElementById("signupForm").addEventListener("submit", function(event) {
	event.preventDefault();

	const firstName = document.getElementById('firstName').value;
	const lastName = document.getElementById('lastName').value;
	const username = document.getElementById('username').value;
	const email = document.getElementById('email').value;
	const password = document.getElementById('password').value;
	const confirmPassword = document.getElementById('confirmPassword').value;

	// Confirm password check
	if (password !== confirmPassword) {
		alert('Passwords do not match!');
		return;
	}

	const formData = new FormData();
	formData.append('firstName', firstName);
	formData.append('lastName', lastName);
	formData.append('username', username);
	formData.append('email', email);
	formData.append('password', password);

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
