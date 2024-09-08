let currentPage = 1;
const postsPerPage = 3;
let allPosts = [];

// Function to get the access token from localStorage
function getAccessToken() {
	return localStorage.getItem('accessToken');
}

// Function to fetch posts from the API
async function fetchPosts() {
	const accessToken = getAccessToken();
	if (!accessToken) {
		console.error('No access token found. User might not be authenticated.');
		return;
	}

	try {
		const response = await fetch('/api/posts', {
			method: 'GET',
			headers: {
				'Authorization': `Bearer ${accessToken}`,
				'Content-Type': 'application/json'
			}
		});

		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}

		const data = await response.json();
		allPosts = data;
		displayPosts();
	} catch (error) {
		console.error('Error fetching posts:', error);
		document.getElementById('postsContainer').innerHTML = `<p class="text-danger">Error loading posts. Please try again later.</p>`;
	}
}

function displayPosts() {
	const postsContainer = document.getElementById('postsContainer');
	postsContainer.innerHTML = '';

	const startIndex = (currentPage - 1) * postsPerPage;
	const endIndex = startIndex + postsPerPage;
	const paginatedPosts = allPosts.slice(startIndex, endIndex);

	paginatedPosts.forEach(post => {
		const postElement = document.createElement('div');
		postElement.className = 'post';
		postElement.innerHTML = `
            <h2 class="h4">${post.title}</h2>
            <p class="post-author mb-2">
                ${post.profileImageUrl ? `<img src="${post.profileImageUrl}" alt="${post.userName}'s profile" class="profile-image mr-2">` : ''}
                By ${post.userName || 'Unknown'}
            </p>
            <p class="post-content">${post.content}</p>
            <p class="text-muted small">Posted on: ${new Date(post.createdDate).toLocaleString()}</p>
            <div class="d-flex justify-content-between">
                <button class="btn btn-outline-primary btn-sm" onclick="fetchAndDisplayAnswers(${post.id})">Show Answers</button>
                <button class="btn btn-outline-success btn-sm" onclick="showAnswerInput(${post.id})">Give Answer</button>
            </div>
            <div id="answers-${post.id}" class="answers mt-3"></div>
            <div id="answerInput-${post.id}" class="mt-3" style="display: none;">
                <div class="input-group">
                    <input type="text" id="newAnswer-${post.id}" class="form-control" placeholder="Your answer">
                    <button class="btn btn-primary" onclick="submitAnswer(${post.id})">Submit</button>
                </div>
            </div>
        `;
		postsContainer.appendChild(postElement);
	});

	updatePagination();
}


function updatePagination() {
	const totalPages = Math.ceil(allPosts.length / postsPerPage);
	document.getElementById('currentPage').textContent = `Page ${currentPage} of ${totalPages}`;
	document.getElementById('prevPage').disabled = currentPage === 1;
	document.getElementById('nextPage').disabled = currentPage === totalPages;
}

function toggleAnswers(postId) {
	const answersDiv = document.getElementById(`answers-${postId}`);
	const post = allPosts.find(p => p.id === postId);

	if (answersDiv.style.display === 'none' || answersDiv.style.display === '') {
		answersDiv.style.display = 'block';
		answersDiv.innerHTML = post.answers && post.answers.length > 0
			? post.answers.map(answer => `
                <div class="answer">
                    <p>${answer.content}</p>
                    <p class="text-muted small">Answered by ${answer.userName || 'Unknown'} on ${new Date(answer.createdDate).toLocaleString()}</p>
                    ${answer.profileImageUrl ? `<img src="${answer.profileImageUrl}" alt="${answer.userName}'s profile" class="profile-image">` : ''}
                </div>
            `).join('')
			: '<p class="text-muted">No answers yet.</p>';
	} else {
		answersDiv.style.display = 'none';
	}
}
async function fetchAndDisplayAnswers(postId) {
	const answersDiv = document.getElementById(`answers-${postId}`);

	if (answersDiv.style.display === 'none' || answersDiv.style.display === '') {
		answersDiv.style.display = 'block';
		answersDiv.innerHTML = '<p>Loading answers...</p>';

		const accessToken = getAccessToken();
		if (!accessToken) {
			console.error('No access token found. User might not be authenticated.');
			return;
		}

		try {
			const response = await fetch(`/api/posts/${postId}/answers`, {
				method: 'GET',
				headers: {
					'Authorization': `Bearer ${accessToken}`,
					'Content-Type': 'application/json'
				}
			});

			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}

			const answers = await response.json();

			answersDiv.innerHTML = answers.length > 0
				? answers.map(answer => `
                    <div class="answer">
                        <p>${answer.content}</p>
                        <p class="text-muted small">
                            ${answer.profileImageUrl ? `<img src="${answer.profileImageUrl}" alt="${answer.username}'s profile" class="profile-image mr-2">` : ''}
                            Answered by ${answer.username || 'Unknown'} on ${new Date(answer.createdDate).toLocaleString()}
                        </p>
                    </div>
                `).join('')
				: '<p class="text-muted">No answers yet.</p>';
		} catch (error) {
			console.error('Error fetching answers:', error);
			answersDiv.innerHTML = '<p class="text-danger">Error loading answers. Please try again later.</p>';
		}
	} else {
		answersDiv.style.display = 'none';
	}
}

function showAnswerInput(postId) {
	const answerInput = document.getElementById(`answerInput-${postId}`);
	answerInput.style.display = 'block';
}

async function submitAnswer(postId) {
	const newAnswerInput = document.getElementById(`newAnswer-${postId}`);
	const answer = newAnswerInput.value.trim();

	if (answer) {
		const accessToken = getAccessToken();
		if (!accessToken) {
			console.error('No access token found. User might not be authenticated.');
			return;
		}

		try {
			const response = await fetch(`/api/posts/${postId}/answers`, {
				method: 'POST',
				headers: {
					'Authorization': `Bearer ${accessToken}`,
					'Content-Type': 'application/json'
				},
				body: JSON.stringify({ content: answer })
			});

			if (!response.ok) {
				throw new Error(`HTTP error! status: ${response.status}`);
			}

			const newAnswer = await response.json();

			// Clear input and hide answer input
			newAnswerInput.value = '';
			document.getElementById(`answerInput-${postId}`).style.display = 'none';

			// Refresh the answers display
			await fetchAndDisplayAnswers(postId);
		} catch (error) {
			console.error('Error submitting answer:', error);
			alert('Error submitting answer. Please try again later.');
		}
	}
}

function searchPosts() {
	const searchTerm = document.getElementById('searchInput').value.toLowerCase();
	const filteredPosts = allPosts.filter(post => post.title.toLowerCase().includes(searchTerm));
	currentPage = 1;
	displayFilteredPosts(filteredPosts);
}

function displayFilteredPosts(filteredPosts) {
	const postsContainer = document.getElementById('postsContainer');
	postsContainer.innerHTML = '';

	const startIndex = (currentPage - 1) * postsPerPage;
	const endIndex = startIndex + postsPerPage;
	const paginatedPosts = filteredPosts.slice(startIndex, endIndex);

	paginatedPosts.forEach(post => {
		const postElement = document.createElement('div');
		postElement.className = 'post';
		postElement.innerHTML = `
            <h2 class="h4">${post.title}</h2>
            <p class="post-author mb-2">By ${post.userName || 'Unknown'}</p>
            <p class="post-content">${post.content}</p>
            <p class="text-muted small">Posted on: ${new Date(post.createdDate).toLocaleString()}</p>
            <div class="d-flex justify-content-between">
                <button class="btn btn-outline-primary btn-sm" onclick="toggleAnswers(${post.id})">Show Answers</button>
                <button class="btn btn-outline-success btn-sm" onclick="showAnswerInput(${post.id})">Give Answer</button>
            </div>
            <div id="answers-${post.id}" class="answers mt-3"></div>
            <div id="answerInput-${post.id}" class="mt-3" style="display: none;">
                <div class="input-group">
                    <input type="text" id="newAnswer-${post.id}" class="form-control" placeholder="Your answer">
                    <button class="btn btn-primary" onclick="submitAnswer(${post.id})">Submit</button>
                </div>
            </div>
        `;
		postsContainer.appendChild(postElement);
	});

	updatePagination();
}

// Event Listeners
document.getElementById('prevPage').addEventListener('click', () => {
	if (currentPage > 1) {
		currentPage--;
		displayPosts();
	}
});

document.getElementById('nextPage').addEventListener('click', () => {
	if (currentPage < Math.ceil(allPosts.length / postsPerPage)) {
		currentPage++;
		displayPosts();
	}
});

document.getElementById('searchButton').addEventListener('click', searchPosts);

// Initial fetch and display
fetchPosts();