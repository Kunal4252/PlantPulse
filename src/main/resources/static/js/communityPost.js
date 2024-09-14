// Function to get the access token (now handled by fetchWithToken)


// Function to format date
function formatDate(dateString) {
	const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
	return new Date(dateString).toLocaleDateString('en-US', options);
}

// Function to create a post element
function createPostElement(post) {
	const postElement = document.createElement('div');
	postElement.className = 'forum-post';
	postElement.innerHTML = `
        <div class="post-header d-flex align-items-center">
            <img src="${post.profileImageUrl || '/api/placeholder/40/40'}" alt="${post.userName}" class="user-avatar">
            <div>
                <h5 class="mb-0">${post.title}</h5>
                <small>Posted by ${post.userName} on ${formatDate(post.createdDate)}</small>
            </div>
        </div>
        <div class="post-content">
            <p>${post.content}</p>
        </div>
        <div class="post-footer">
            <button class="btn btn-sm btn-outline-secondary" onclick="toggleAnswers(${post.postId})">
                Show Answers (${post.answers.length})
            </button>
            <button class="btn btn-sm btn-outline-primary" onclick="toggleAnswerForm(${post.postId})">
                Give Answer
            </button>
            <div id="answerForm-${post.postId}" style="display: none;" class="mt-3">
                <textarea class="form-control mb-2" id="answerContent-${post.postId}" rows="2" placeholder="Your answer..."></textarea>
                <button class="btn btn-sm btn-success" onclick="submitAnswer(${post.postId})">Submit Answer</button>
            </div>
            <div id="answers-${post.postId}" class="answers mt-3" style="display: none;">
                ${post.answers.map(answer => `
                    <div class="answer">
                        <img src="${answer.profileImageUrl || '/api/placeholder/40/40'}" alt="${answer.userName}" class="user-avatar" style="width: 30px; height: 30px;">
                        <strong>${answer.userName}:</strong> ${answer.content}
                        <small class="d-block mt-1">Posted on ${formatDate(answer.createdDate)}</small>
                    </div>
                `).join('')}
            </div>
        </div>
    `;
	return postElement;
}

// Function to display posts
async function displayPosts(posts) {
	const forumPosts = document.getElementById('forumPosts');
	forumPosts.innerHTML = ''; // Clear the existing posts
	posts.forEach(post => {
		forumPosts.appendChild(createPostElement(post));
	});
}

// Function to toggle answers visibility
function toggleAnswers(postId) {
	const answersDiv = document.getElementById(`answers-${postId}`);
	if (answersDiv) {
		answersDiv.style.display = (answersDiv.style.display === 'none' || answersDiv.style.display === '') ? 'block' : 'none';
	} else {
		console.error('Answers container not found.');
	}
}

// Function to toggle answer form visibility
function toggleAnswerForm(postId) {
	const answerForm = document.getElementById(`answerForm-${postId}`);
	if (answerForm) {
		answerForm.style.display = (answerForm.style.display === 'none' || answerForm.style.display === '') ? 'block' : 'none';
	} else {
		console.error('Answer form not found.');
	}
}

// Function to submit an answer
async function submitAnswer(postId) {
	const answerInput = document.getElementById(`answerContent-${postId}`);
	if (!answerInput) {
		console.error('Answer content input not found.');
		return;
	}

	const content = answerInput.value.trim();
	const userId = localStorage.getItem('userId');

	if (!userId) {
		console.error('User ID not found in localStorage.');
		return;
	}

	const requestBody = {
		content: content,
		user: {
			id: userId
		}
	};

	try {
		const response = await fetchWithToken(`/api/posts/${postId}/answers`, {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json'
			},
			body: JSON.stringify(requestBody),
		});

		if (!response.ok) {
			throw new Error(`Failed to submit answer. Status: ${response.status}`);
		}

		answerInput.value = '';
		toggleAnswerForm(postId);

		// Update the answer count
		const showAnswersButton = document.querySelector(`button[onclick="toggleAnswers(${postId})"]`);
		if (showAnswersButton) {
			const currentCountMatch = showAnswersButton.textContent.match(/\d+/);
			if (currentCountMatch) {
				const currentCount = parseInt(currentCountMatch[0], 10);
				showAnswersButton.textContent = `Show Answers (${currentCount + 1})`;
			} else {
				console.error('Failed to parse answer count from button text.');
			}
		} else {
			console.error('Show answers button not found.');
		}

		// Reload the posts to show the new answer
		await loadPosts();
	} catch (error) {
		console.error('Error submitting answer:', error);
		alert('Failed to submit answer. Please try again later.');
	}
}

// Function to search posts
async function searchPosts() {
	const searchInput = document.getElementById('searchInput').value.trim();
	if (!searchInput) {
		// If search input is empty, you might want to show all posts or clear results
		await loadPosts(); // Load all posts
		return;
	}

	try {
		const response = await fetchWithToken(`/api/posts/search?title=${encodeURIComponent(searchInput)}`, {
			method: 'GET'
		});

		if (!response.ok) {
			throw new Error(`Failed to fetch posts. Status: ${response.status}`);
		}

		const posts = await response.json();
		displayPosts(posts); // Directly use the array of posts
	} catch (error) {
		console.error('Error searching posts:', error);
		alert('Failed to search posts. Please try again later.');
	}
}

// Function to load posts (without pagination)
async function loadPosts() {
	try {
		const response = await fetchWithToken('/api/posts', {
			method: 'GET'
		});

		if (!response.ok) {
			throw new Error('Failed to fetch posts');
		}

		const posts = await response.json();
		displayPosts(posts);
	} catch (error) {
		console.error('Error loading posts:', error);
		alert('Failed to load posts. Please try again later.');
	}
}

// Load posts on page load
document.addEventListener('DOMContentLoaded', loadPosts);
