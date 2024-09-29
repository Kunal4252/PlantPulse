let imageCount = 1;

document.addEventListener('DOMContentLoaded', function() {
	if (!checkAuthentication()) {
		return; // Stop execution if not authenticated
	}
	const plantForm = document.getElementById('plantForm');
	const resultDiv = document.getElementById('result');
	const refreshButton = document.getElementById('refreshButton');
	const addImageButton = document.getElementById('addImage');
	const imageInputsDiv = document.getElementById('imageInputs');
	const loadingSpinner = document.getElementById('loadingSpinner');

	const logoutBtn = document.getElementById("logoutBtn");
	if (logoutBtn) {
		logoutBtn.addEventListener("click", function(event) {
			event.preventDefault();
			logout(); // Call the logout function from auth.js
		});
	}


	// Load persistent results if available
	const savedResults = localStorage.getItem('plantIdentificationResults');
	if (savedResults) {
		resultDiv.innerHTML = savedResults;
		refreshButton.style.display = 'inline-block';
	}

	addImageButton.addEventListener('click', () => {
		if (imageCount < 5) {
			imageCount++;
			const newInput = document.createElement('div');
			newInput.classList.add('card', 'mb-3');
			newInput.innerHTML = `
                <div class="card-body">
                    <div class="d-flex justify-content-between align-items-center mb-2">
                        <h5 class="card-title mb-0">Plant Image ${imageCount}</h5>
                        <button type="button" class="btn-close remove-image" aria-label="Close"></button>
                    </div>
                    <div class="mb-3">
                        <label for="image${imageCount}" class="form-label">Plant Image ${imageCount}:</label>
                        <input type="file" class="form-control" id="image${imageCount}" name="image" accept="image/*" required>
                        <img id="preview${imageCount}" class="image-preview mt-2 d-none" alt="Image preview">
                    </div>
                    <div class="mb-3">
                        <label for="organ${imageCount}" class="form-label">Plant Organ:</label>
                        <select class="form-select" id="organ${imageCount}" name="organ" required>
                            <option value="">Select plant part</option>
                            <option value="flower">Flower</option>
                            <option value="leaf">Leaf</option>
                            <option value="fruit">Fruit</option>
                            <option value="bark">Bark</option>
                            <option value="habit">Habit</option>
                            <option value="other">Other</option>
                        </select>
                    </div>
                </div>
            `;
			imageInputsDiv.appendChild(newInput);
			setupImagePreview(imageCount);
			setupRemoveButton(newInput);
		} else {
			alert('Maximum of 5 images allowed');
		}
	});

	function setupRemoveButton(inputElement) {
		const removeButton = inputElement.querySelector('.remove-image');
		removeButton.addEventListener('click', () => {
			inputElement.remove();
			imageCount--;
			updateImageNumbers();
		});
	}

	function updateImageNumbers() {
		const imageForms = imageInputsDiv.querySelectorAll('.card');
		imageForms.forEach((form, index) => {
			const newIndex = index + 1;

			const cardTitle = form.querySelector('.card-title');
			if (cardTitle) {
				cardTitle.textContent = `Plant Image ${newIndex}`;
			}

			const imageLabel = form.querySelector('label[for^="image"]');
			if (imageLabel) {
				imageLabel.textContent = `Plant Image ${newIndex}:`;
			}

			const imageInput = form.querySelector('input[id^="image"]');
			if (imageInput) {
				imageInput.id = `image${newIndex}`;
			}

			const previewImage = form.querySelector('img[id^="preview"]');
			if (previewImage) {
				previewImage.id = `preview${newIndex}`;
			}

			const organLabel = form.querySelector('label[for^="organ"]');
			if (organLabel) {
				organLabel.setAttribute('for', `organ${newIndex}`);
			}

			const organSelect = form.querySelector('select[id^="organ"]');
			if (organSelect) {
				organSelect.id = `organ${newIndex}`;
			}
		});
	}

	plantForm.addEventListener('submit', async (e) => {
		e.preventDefault();

		const formData = new FormData();

		const imageForms = imageInputsDiv.querySelectorAll('.card');
		imageForms.forEach((form, index) => {
			const imageFile = form.querySelector('input[type="file"]').files[0];
			const organ = form.querySelector('select').value;

			if (imageFile && organ) {
				formData.append('images', imageFile);
				formData.append('organs', organ);
			}
		});

		try {
			loadingSpinner.style.display = 'block';
			resultDiv.innerHTML = '';

			const response = await fetchWithToken('/api/plants/identify', {
				method: 'POST',
				body: formData
			});

			const contentType = response.headers.get("content-type");
			let data;

			if (contentType && contentType.indexOf("application/json") !== -1) {
				data = await response.json();
			} else {
				data = await response.text();
			}

			if (!response.ok) {
				throw new Error(typeof data === 'string' ? data : data.message || `HTTP error! status: ${response.status}`);
			}

			displayResults(data);

			// Save results to localStorage
			localStorage.setItem('plantIdentificationResults', resultDiv.innerHTML);

			// Show refresh button
			refreshButton.style.display = 'inline-block';
		} catch (error) {
			console.error('Error:', error);
			displayError(error.message);
		} finally {
			loadingSpinner.style.display = 'none';
		}
	});
	refreshButton.addEventListener('click', function() {
		// Clear form
		plantForm.reset();

		// Clear results
		resultDiv.innerHTML = '';

		// Hide refresh button
		refreshButton.style.display = 'none';

		// Clear localStorage
		localStorage.removeItem('plantIdentificationResults');

		// Reset image inputs to initial state
		imageInputsDiv.innerHTML = `
            <div class="card mb-3">
                <div class="card-body">
                    <div class="mb-3">
                        <label for="image1" class="form-label">Plant Image 1:</label>
                        <input type="file" class="form-control" id="image1" name="image" accept="image/*" required>
                        <img id="preview1" class="image-preview mt-2 d-none" alt="Image preview">
                    </div>
                    <div class="mb-3">
                        <label for="organ1" class="form-label">Plant Organ:</label>
                        <select class="form-select" id="organ1" name="organ" required>
                            <option value="">Select plant part</option>
                            <option value="flower">Flower</option>
                            <option value="leaf">Leaf</option>
                            <option value="fruit">Fruit</option>
                            <option value="bark">Bark</option>
                            <option value="habit">Habit</option>
                            <option value="other">Other</option>
                        </select>
                    </div>
                </div>
            </div>
        `;

		// Reset imageCount
		imageCount = 1;

		// Setup image preview for the first input
		setupImagePreview(1);
	});

	// Setup image preview for the first input
	setupImagePreview(1);
});


// Function to display results
function displayResults(results) {
	const resultDiv = document.getElementById('result');
	resultDiv.innerHTML = '';

	if (Array.isArray(results) && results.length > 0) {
		results.forEach((plant, index) => {
			const card = document.createElement('div');
			card.classList.add('card', 'mb-3', 'position-relative');
			card.innerHTML = `
                <div class="card-header">
                    <h5 class="card-title mb-0">Plant ${index + 1}: ${plant.scientificName}</h5>
                </div>
                <div class="card-body">
                    <p class="card-text"><strong>Common Names:</strong> ${plant.commonNames}</p>
                    <p class="card-text"><strong>Family:</strong> ${plant.family}</p>
                    <p class="card-text"><strong>Genus:</strong> ${plant.genus}</p>
                    <div class="mt-3">
                        <a href="https://www.gbif.org/species/${plant.gbifId}" target="_blank" class="btn btn-outline-primary me-2">
                            <i class="fas fa-external-link-alt me-2"></i>View on GBIF
                        </a>
                        <a href="http://powo.science.kew.org/taxon/${plant.powoId}" target="_blank" class="btn btn-outline-secondary">
                            <i class="fas fa-external-link-alt me-2"></i>View on POWO
                        </a>
                    </div>
                </div>
                <span class="result-tag ${index === 0 ? 'best-match' : index === 1 ? 'second-best' : 'd-none'}">
                    ${index === 0 ? 'Best Match' : index === 1 ? 'Second Best' : ''}
                </span>
            `;
			resultDiv.appendChild(card);
		});
	} else {
		displayError("No plants were identified. Please try with a clearer image.");
	}
}

// New function to display errors
function displayError(message) {
	const resultDiv = document.getElementById('result');
	resultDiv.innerHTML = `
        <div class="alert alert-danger" role="alert">
            <h4 class="alert-heading">Error</h4>
            <p>${message}</p>
            <hr>
            <p class="mb-0">Please try again with a different image or check your internet connection.</p>
        </div>
    `;
}

// Function to setup image preview
function setupImagePreview(index) {
	const input = document.getElementById(`image${index}`);
	const preview = document.getElementById(`preview${index}`);

	input.addEventListener('change', function() {
		if (this.files && this.files[0]) {
			const reader = new FileReader();
			reader.onload = function(e) {
				preview.src = e.target.result;
				preview.classList.remove('d-none');
			};
			reader.readAsDataURL(this.files[0]);
		}
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