// plant-search-api.js

const API_BASE_URL = 'http://localhost:8082/api/plants'; // Replace with your actual API base URL

async function searchPlants(query = '') {
	try {
		const response = await fetchWithToken(`${API_BASE_URL}/search?query=${encodeURIComponent(query)}`);
		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}
		const plants = await response.json();
		return plants;
	} catch (error) {
		console.error('Error fetching plants:', error);
		return [];
	}
}

async function getPlantDetails(name) {
	try {
		const response = await fetchWithToken(`${API_BASE_URL}/${encodeURIComponent(name)}`);
		if (!response.ok) {
			throw new Error(`HTTP error! status: ${response.status}`);
		}
		const plantDetails = await response.json();
		return plantDetails;
	} catch (error) {
		console.error('Error fetching plant details:', error);
		return null;
	}
}

function displayPlantResults(plants) {
	const resultsContainer = document.getElementById('plantResults');
	resultsContainer.innerHTML = ''; // Clear previous results

	if (plants.length === 0) {
		resultsContainer.innerHTML = '<p class="text-center">No plants found. Try a different search term.</p>';
		return;
	}

	plants.forEach(plant => {
		const card = document.createElement('div');
		card.className = 'col-md-4 mb-4';
		card.innerHTML = `
            <div class="card h-100">
                <div class="card-body">
                    <h5 class="card-title">${plant.name}</h5>
                    <p class="card-text">Family: ${plant.family}</p>
                    <p class="card-text">Genus: ${plant.genus}</p>
                    <p class="card-text">Alternative Names: ${plant.alternativeNames || 'N/A'}</p>
                    <button onclick="showPlantDetails('${plant.name}')" class="btn btn-success">View Details</button>
                </div>
            </div>
        `;
		resultsContainer.appendChild(card);
	});
}

async function showPlantDetails(name) {
	const plantDetails = await getPlantDetails(name);
	if (plantDetails) {
		const detailsHtml = `
            <div class="container">
                <div class="row mb-3">
                    <div class="col-md-12 text-center">
                        <h2 class="text-success">${plantDetails.name}</h2>
                        <p class="text-muted"><em>${plantDetails.scientificName}</em></p>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <h4 class="text-success">Basic Info</h4>
                        <ul class="list-group mb-4">
                            <li class="list-group-item"><strong>Family:</strong> ${plantDetails.family}</li>
                            <li class="list-group-item"><strong>Genus:</strong> ${plantDetails.genus}</li>
                            <li class="list-group-item"><strong>Alternative Names:</strong> ${plantDetails.alternativeNames || 'N/A'}</li>
                            <li class="list-group-item"><strong>Description:</strong> ${plantDetails.description}</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h4 class="text-success">Growing Information</h4>
                        <ul class="list-group mb-4">
                            <li class="list-group-item"><strong>Sunlight:</strong> ${plantDetails.sunlight}</li>
                            <li class="list-group-item"><strong>Soil:</strong> ${plantDetails.soil}</li>
                            <li class="list-group-item"><strong>Water:</strong> ${plantDetails.wateringConditions}</li>
                            <li class="list-group-item"><strong>Temperature:</strong> ${plantDetails.temperature}</li>
                        </ul>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <h4 class="text-success">Planting</h4>
                        <ul class="list-group mb-4">
                            <li class="list-group-item"><strong>Sowing Depth:</strong> ${plantDetails.sowingDepth}</li>
                            <li class="list-group-item"><strong>Spacing Between Plants:</strong> ${plantDetails.spacingBetweenPlants}</li>
                            <li class="list-group-item"><strong>Spacing Between Rows:</strong> ${plantDetails.spacingBetweenRows}</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h4 class="text-success">Care</h4>
                        <ul class="list-group mb-4">
                            <li class="list-group-item"><strong>Watering Instructions:</strong> ${plantDetails.wateringInstructions}</li>
                            <li class="list-group-item"><strong>Fertilizing Instructions:</strong> ${plantDetails.fertilizingInstructions}</li>
                            <li class="list-group-item"><strong>Pruning Instructions:</strong> ${plantDetails.pruningInstructions}</li>
                        </ul>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <h4 class="text-success">Harvest</h4>
                        <ul class="list-group mb-4">
                            <li class="list-group-item"><strong>Days to Maturity:</strong> ${plantDetails.daysToMaturity}</li>
                            <li class="list-group-item"><strong>Harvest Season:</strong> ${plantDetails.harvestSeason}</li>
                            <li class="list-group-item"><strong>Harvest Method:</strong> ${plantDetails.harvestMethod}</li>
                        </ul>
                    </div>
                    <div class="col-md-6">
                        <h4 class="text-success">Pests and Diseases</h4>
                        <ul class="list-group mb-4">
                            <li class="list-group-item"><strong>Pests:</strong> ${plantDetails.pests}</li>
                            <li class="list-group-item"><strong>Diseases:</strong> ${plantDetails.diseases}</li>
                        </ul>
                    </div>
                </div>
                <div class="row">
                    <div class="col-md-6">
                        <h4 class="text-success">Additional Info</h4>
                        <ul class="list-group mb-4">
                            <li class="list-group-item"><strong>Companion Plants:</strong> ${plantDetails.companionPlants}</li>
                            <li class="list-group-item"><strong>Notes:</strong> ${plantDetails.notes}</li>
                        </ul>
                    </div>
                </div>
            </div>
        `;

		document.getElementById('plantDetailsContent').innerHTML = detailsHtml;

		// Show the modal
		const plantDetailsModal = new bootstrap.Modal(document.getElementById('plantDetailsModal'));
		plantDetailsModal.show();
	} else {
		alert('Unable to fetch plant details. Please try again.');
	}
}

function displayWelcomeMessage() {
	const resultsContainer = document.getElementById('plantResults');
	resultsContainer.innerHTML = `
        <div class="col-12 text-center">
            <h2>Welcome to GardenGenius!</h2>
            <p class="lead">Discover a world of plants and gardening wisdom.</p>
            <p>Use the search bar above to find information about your favorite plants or discover new ones.</p>
            <div class="mt-4">
                <h3>Popular Searches:</h3>
                <button class="btn btn-outline-success m-2" onclick="quickSearch('Tomato')">Tomato</button>
                <button class="btn btn-outline-success m-2" onclick="quickSearch('Rose')">Rose</button>
                <button class="btn btn-outline-success m-2" onclick="quickSearch('Basil')">Basil</button>
            </div>
        </div>
    `;
}

// Function to handle the search form submission
function handleSearch(event) {
	event.preventDefault();
	const searchTerm = document.getElementById('searchInput').value;
	if (searchTerm.trim() !== '') {
		searchPlants(searchTerm).then(displayPlantResults);
	} else {
		displayWelcomeMessage();
	}
}

// Function for quick search buttons
function quickSearch(term) {
	document.getElementById('searchInput').value = term;
	searchPlants(term).then(displayPlantResults);
}

// Function to check if user is authenticated and redirect if not
function checkAuthentication() {
	const accessToken = getAccessToken();
	if (!accessToken) {
		window.location.href = '/signIn';
	}
}

// Add event listeners and initialize the page
document.addEventListener('DOMContentLoaded', () => {
	checkAuthentication();

	const searchForm = document.getElementById('searchForm');
	if (searchForm) {
		searchForm.addEventListener('submit', handleSearch);
	}

	// Display welcome message on initial page load
	displayWelcomeMessage();

	// Add logout button functionality
	const logoutButton = document.getElementById('logoutButton');
	if (logoutButton) {
		logoutButton.addEventListener('click', logout);
	}
});