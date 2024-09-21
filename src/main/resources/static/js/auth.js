// auth.js

function getAccessToken() {
	return localStorage.getItem('accessToken');
}

function getRefreshToken() {
	return localStorage.getItem('refreshToken');
}

function saveAccessToken(accessToken) {
	localStorage.setItem('accessToken', accessToken);
}

function saveRefreshToken(refreshToken) {
	localStorage.setItem('refreshToken', refreshToken);
}

function clearTokens() {
	localStorage.removeItem('accessToken');
	localStorage.removeItem('refreshToken');
}

async function refreshAccessToken() {
	const refreshToken = getRefreshToken();

	if (!refreshToken) {
		console.error('No refresh token available.');
		return { success: false, message: 'No refresh token available.' };
	}

	try {
		const response = await fetch('api/users/refresh', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ refreshToken })
		});

		const data = await response.json();

		if (response.ok) {
			saveAccessToken(data.accessToken);
			return { success: true, accessToken: data.accessToken, message: data.message };
		} else {
			console.error('Failed to refresh token:', data.message);
			clearTokens();
			return { success: false, message: data.message };
		}
	} catch (error) {
		console.error('Error refreshing token:', error);
		clearTokens();
		return { success: false, message: 'Network error occurred while refreshing token.' };
	}
}

async function fetchWithToken(url, options = {}) {
	let accessToken = getAccessToken();

	if (!options.headers) {
		options.headers = {};
	}

	options.headers['Authorization'] = `Bearer ${accessToken}`;

	let response = await fetch(url, options);

	if (response.status === 401) {
		console.log('Access token expired, trying to refresh...');

		const refreshResult = await refreshAccessToken();

		if (refreshResult.success) {
			options.headers['Authorization'] = `Bearer ${refreshResult.accessToken}`;
			response = await fetch(url, options);
		} else {
			console.error('Token refresh failed:', refreshResult.message);
			window.location.href = '/signIn';
		}
	}

	return response;
}

async function logout() {
	const refreshToken = getRefreshToken();

	if (!refreshToken) {
		console.error('No refresh token available for logout.');
		clearTokens();
		window.location.href = '/signIn';
		return;
	}

	try {
		const response = await fetch('api/users/logout', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify({ refreshToken })
		});

		if (response.ok) {
			console.log('Logout successful');
		} else {
			console.error('Logout failed:', await response.text());
		}
	} catch (error) {
		console.error('Error during logout:', error);
	} finally {
		clearTokens();
		window.location.href = '/signIn';
	}
}