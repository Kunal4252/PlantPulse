// Function to get the access token (now handled by fetchWithToken)

document.addEventListener('DOMContentLoaded', function() {
	if (!checkAuthentication()) return;
	const searchForm = document.getElementById('searchForm');
	/*const searchInput = document.getElementById('searchInput');*/

	searchForm.addEventListener('submit', function(e) {
		e.preventDefault(); // Prevent the form from submitting normally
		searchPosts();
	});

	// Load posts on page load
	loadPosts();
});
// Function to format date
function formatDate(dateString) {
	const options = { year: 'numeric', month: 'long', day: 'numeric', hour: '2-digit', minute: '2-digit' };
	return new Date(dateString).toLocaleDateString('en-US', options);
}

function createAnswerElement(answer, postId) {
	return `
        <div class="answer" data-answer-id="${answer.id}">
            <img src="${answer.profileImageUrl || '/api/placeholder/40/40'}" alt="${answer.userName}" class="user-avatar" style="width: 30px; height: 30px;">
            <strong>${answer.userName}:</strong> ${answer.content}
            <small class="d-block mt-1">Posted on ${formatDate(answer.createdDate)}</small>
            <button class="btn btn-sm btn-outline-success greenify-btn answer-like-btn" onclick="toggleAnswerLike(${postId}, ${answer.id})" title="Greenify this answer">
                <i class="fas fa-leaf"></i> Greenify
                <span class="like-count">${answer.likeCount || 0}</span>
            </button>
        </div>
    `;
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
            <button class="btn btn-sm btn-outline-success greenify-btn" onclick="togglePostLike(${post.postId})" title="Greenify this post">
                <i class="fas fa-leaf"></i> Greenify
                <span class="like-count">${post.likeCount || 0}</span>
            </button>
            <div id="answerForm-${post.postId}" style="display: none;" class="mt-3">
                <textarea class="form-control mb-2" id="answerContent-${post.postId}" rows="2" placeholder="Your answer..."></textarea>
                <button class="btn btn-sm btn-success" onclick="submitAnswer(${post.postId})">Submit Answer</button>
            </div>
            <div id="answers-${post.postId}" class="answers mt-3" style="display: none;">
                ${post.answers.map(answer => createAnswerElement(answer, post.postId)).join('')}
            </div>
        </div>
    `;
	return postElement;
}

// Function to display posts
function displayPosts(posts) {
	const forumPosts = document.getElementById('forumPosts');
	forumPosts.innerHTML = ''; // Clear the existing posts
	if (posts.length === 0) {
		forumPosts.innerHTML = '<p>No posts found matching your search.</p>';
		return;
	}
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

async function togglePostLike(postId) {
	await toggleLike(postId, 'post');
}

// Function to toggle answer like
async function toggleAnswerLike(postId, answerId) {
	await toggleLike(answerId, 'answer', postId);
}

// Generic function to toggle like for both posts and answers
// Generic function to toggle like for both posts and answers
async function toggleLike(id, type, postId = null) {
	try {
		// Validate type
		if (!['post', 'answer'].includes(type)) {
			throw new Error(`Invalid type: ${type}`);
		}

		// Construct the selector string based on type
		const selector = type === 'post'
			? `[onclick="togglePostLike(${id})"]`
			: `[onclick="toggleAnswerLike(${postId}, ${id})"]`;

		console.log(`Selector used: ${selector}`);

		// Select the like button based on the constructed selector
		const likeButton = document.querySelector(selector);
		if (!likeButton) {
			console.error(`Like button not found for ${type} with id ${id}`);
			return;
		}

		const likeCount = likeButton.querySelector('.like-count');
		if (!likeCount) {
			console.error('Like count element not found.');
			return;
		}

		const isLiked = likeButton.classList.contains('liked');
		const method = isLiked ? 'DELETE' : 'POST';
		const endpoint = type === 'post'
			? `/api/posts/${id}/like`
			: `/api/posts/${postId}/answers/${id}/like`;
		const response = await fetchWithToken(endpoint, { method });

		if (!response.ok) {
			throw new Error(`Failed to ${isLiked ? 'unlike' : 'like'} ${type}. Status: ${response.status}`);
		}

		// Toggle the liked state
		likeButton.classList.toggle('liked');

		// Update the like count
		const currentCount = parseInt(likeCount.textContent, 10);
		likeCount.textContent = isLiked ? currentCount - 1 : currentCount + 1;

		// Update the button title
		likeButton.title = isLiked ? `Greenify this ${type}` : `Un-greenify this ${type}`;

	} catch (error) {
		console.error(`Error toggling ${type} like:`, error);
		alert(`Failed to update ${type} like status. Please try again later.`);
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
		await loadPosts(); // Load all posts if search input is empty
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
		displayPosts(posts);
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

		// Set initial state of like buttons for posts and answers
		posts.forEach(post => {
			const postLikeButton = document.querySelector(`[onclick="togglePostLike(${post.postId})"]`);
			if (postLikeButton && post.likedByCurrentUser) {
				postLikeButton.classList.add('liked');
				postLikeButton.title = 'Un-greenify this post';
			}

			post.answers.forEach(answer => {
				const answerLikeButton = document.querySelector(`[onclick="toggleAnswerLike(${post.postId}, ${answer.id})"]`);
				if (answerLikeButton && answer.likedByCurrentUser) {
					answerLikeButton.classList.add('liked');
					answerLikeButton.title = 'Un-greenify this answer';
				}
			});
		});
	} catch (error) {
		console.error('Error loading posts:', error);
		alert('Failed to load posts. Please try again later.');
	}
}

// Load posts on page load
document.addEventListener('DOMContentLoaded', loadPosts);

const logoutBtn = document.getElementById("logoutBtn");
if (logoutBtn) {
	logoutBtn.addEventListener("click", function(event) {
		event.preventDefault();
		logout(); // Call the logout function from auth.js
	});
}

function checkAuthentication() {
	const accessToken = getAccessToken(); // Use getAccessToken from auth.js
	if (!accessToken) {
		window.location.href = '/signIn';
		return false;
	}
	return true;
}
