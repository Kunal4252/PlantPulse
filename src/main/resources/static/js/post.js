
// Function to get the access token (you need to implement this based on your authentication method)
function getAccessToken() {
	// For example, you might retrieve it from localStorage
	return localStorage.getItem('accessToken');
}

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
function displayPosts(posts) {
	const forumPosts = document.getElementById('forumPosts');
	posts.forEach(post => {
		forumPosts.appendChild(createPostElement(post));
	});
}

// Function to toggle answers visibility
function toggleAnswers(postId) {
	const answersDiv = document.getElementById(`answers-${postId}`);
	if (answersDiv.style.display === 'none') {
		answersDiv.style.display = 'block';
	} else {
		answersDiv.style.display = 'none';
	}
}

async function fetchPosts() {
	try {
		const response = await fetch('/api/posts', {
			headers: {
				'Authorization': `Bearer ${getAccessToken()}`
			}
		});
		if (!response.ok) {
			throw new Error('Failed to fetch posts');
		}
		const posts = await response.json();
		displayPosts(posts);  // Function that adds the posts to the DOM
	} catch (error) {
		console.error('Error fetching posts:', error);
		alert('Failed to load posts. Please try again later.');
	}
}

// Function to handle new post submission
async function handleNewPost(event) {
	event.preventDefault();
	const title = document.getElementById('postTitle').value;
	const content = document.getElementById('postContent').value;

	const userId = localStorage.getItem('userId');

	// Create the request body
	const requestBody = {
		title: title,
		content: content,
		user: {
			id: userId
		}
	};

	try {
		const response = await fetch('/api/posts', {
			method: 'POST',
			headers: {
				'Content-Type': 'application/json',
				'Authorization': `Bearer ${getAccessToken()}`
			},
			body: JSON.stringify(requestBody),
		});

		if (!response.ok) {
			throw new Error('Failed to create post');
		}

		const newPost = await response.json();

		// Clear the form
		document.getElementById('newPostForm').reset();

		// Display the new post
		const forumPosts = document.getElementById('forumPosts');
		forumPosts.insertBefore(createPostElement(newPost), forumPosts.firstChild);

	} catch (error) {
		console.error('Error creating new post:', error);
		alert('Failed to create post. Please try again later.');
	}
}

// Event Listeners
document.addEventListener('DOMContentLoaded', fetchPosts);
document.getElementById('newPostForm').addEventListener('submit', handleNewPost);
document.getElementById('loadMoreBtn').addEventListener('click', fetchPosts); // In a real app, this would load more posts with pagination
