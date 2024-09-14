// auth.js

// Function to get the access token from localStorage
function getAccessToken() {
	return localStorage.getItem('accessToken');
}

// Function to get the refresh token from localStorage
function getRefreshToken() {
	return localStorage.getItem('refreshToken');
}

// Function to save tokens in localStorage
function saveTokens(accessToken, refreshToken) {
	localStorage.setItem('accessToken', accessToken);
	localStorage.setItem('refreshToken', refreshToken);
}

// Function to remove tokens from localStorage (e.g., for logout)
function clearTokens() {
	localStorage.removeItem('accessToken');
	localStorage.removeItem('refreshToken');
}

// Function to refresh the access token
async function refreshAccessToken() {
	const refreshToken = getRefreshToken();

	if (!refreshToken) {
		console.error('No refresh token available.');
		return null;
	}

	const response = await fetch('api/users/refresh', {
		method: 'POST',
		headers: {
			'Content-Type': 'application/json'
		},
		body: JSON.stringify({ refreshToken })
	});

	if (response.ok) {
		const data = await response.json();
		saveTokens(data.accessToken, data.refreshToken);
		return data.accessToken;
	} else {
		console.error('Failed to refresh token. Redirecting to login...');
		clearTokens();
		window.location.href = '/login'; // Redirect to login page
		return null;
	}
}

// Function to perform a fetch with token management (retry after token refresh)
async function fetchWithToken(url, options = {}) {
	let token = getAccessToken();

	if (!options.headers) {
		options.headers = {};
	}

	// Attach the Authorization header with the access token
	options.headers['Authorization'] = `Bearer ${token}`;

	let response = await fetch(url, options);

	// If access token is expired (401), refresh token and retry
	if (response.status === 401) {
		console.log('Access token expired, trying to refresh...');

		// Refresh access token
		token = await refreshAccessToken();

		if (token) {
			// Retry the original request with the new token
			options.headers['Authorization'] = `Bearer ${token}`;
			response = await fetch(url, options);
		}
	}

	return response;
}

// Logout function: clear tokens and redirect to login
function logout() {
	clearTokens();
	window.location.href = '/login';
}


