let imageCount = 1;

        document.getElementById('addImage').addEventListener('click', () => {
            if (imageCount < 5) {
                imageCount++;
                const newInput = document.createElement('div');
                newInput.classList.add('card', 'mb-3');
                newInput.innerHTML = `
                    <div class="card-body">
                        <div class="mb-3">
                            <label for="image${imageCount}" class="form-label">Plant Image ${imageCount}:</label>
                            <input type="file" class="form-control" id="image${imageCount}" name="image" accept="image/*" required>
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
                document.getElementById('imageInputs').appendChild(newInput);
            } else {
                alert('Maximum of 5 images allowed');
            }
        });

        document.getElementById('plantForm').addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const formData = new FormData();

            for (let i = 1; i <= imageCount; i++) {
                const imageFile = document.getElementById(`image${i}`).files[0];
                const organ = document.getElementById(`organ${i}`).value;
                
                if (imageFile && organ) {
                    formData.append('images', imageFile);
                    formData.append('organs', organ);
                }
            }

            const token = localStorage.getItem('accessToken');

            try {
                const response = await fetch('http://localhost:8082/api/plants/identify', {
                    method: 'POST',
                    headers: {
                        'Authorization': `Bearer ${token}`
                    },
                    body: formData
                });

                if (!response.ok) {
                    throw new Error(`HTTP error! status: ${response.status}`);
                }

                const data = await response.json();
                displayResults(data);
            } catch (error) {
                console.error('Error:', error);
                document.getElementById('result').innerHTML = `
                    <div class="alert alert-danger" role="alert">
                        An error occurred. Please try again. Error: ${error.message}
                    </div>
                `;
            }
        });

        function displayResults(results) {
            const resultDiv = document.getElementById('result');
            resultDiv.innerHTML = '';

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
                            <a href="https://www.gbif.org/species/${plant.gbifId}" target="_blank" class="btn btn-outline-primary me-2">View on GBIF</a>
                            <a href="http://powo.science.kew.org/taxon/${plant.powoId}" target="_blank" class="btn btn-outline-secondary">View on POWO</a>
                        </div>
                    </div>
                    <span class="result-tag ${index === 0 ? 'best-match' : index === 1 ? 'second-best' : 'd-none'}">
                        ${index === 0 ? 'Best Match' : index === 1 ? 'Second Best' : ''}
                    </span>
                `;
                resultDiv.appendChild(card);
            });
        }