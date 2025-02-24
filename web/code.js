/* jshint esversion: 6 */
/* This comment is for JSHint, a static code analysis tool that checks for potential errors and code quality issues. It ensures that the code is compatible with ECMAScript 6 (ES6). */

/* Function to trigger a search action */
function triggerSearch() {
    const query = document.getElementById('searchbox').value;
    const selectedAlgorithm = getSelectedAlgorithm(); // Retrieve the selected algorithm

    if (!selectedAlgorithm) {
        alert("Select a ranking algorithm!");
        return;
    }

    fetch(`/search?q=${query}&algorithm=${selectedAlgorithm}`)
        .then(response => response.text())
        .then(data => {
            if (data === "404") {
                document.getElementById("responsesize").innerHTML = 
                    "<p>No web page contains the query word.</p>";
                document.getElementById("urllist").innerHTML = "";
            } else {
                const results = JSON.parse(data).map(page =>
                    `<li><a href="${page.url}">${page.title}</a></li>`
                ).join("\n");

                document.getElementById("responsesize").innerHTML = 
                    `<p>${JSON.parse(data).length} websites retrieved</p>`;
                document.getElementById("urllist").innerHTML = `<ul>${results}</ul>`;
            }
        });
}

// Function to get the selected radio button value
function getSelectedAlgorithm() {
    const radios = document.getElementsByName('rankingAlgorithm');
    for (let radio of radios) {
        if (radio.checked) {
            return radio.value; // Return the value of the selected radio button
        }
    }
    return null; // Return null if no button is selected
}

// Event listener for 'searchbutton' interaction
document.getElementById('searchbutton').onclick = triggerSearch;
document.getElementById('searchbox').addEventListener('keydown', (event) => {
    if (event.key === "Enter") { // Check if the 'Enter' key was pressed
        triggerSearch(); // Trigger the search action
    }
});

// JavaScript to generate the falling snowflakes
const matrixContainer = document.querySelector('.matrix');
function createSnowFlakes() {
    const flake = document.createElement('span');
    const char = "*";
    flake.innerText = char;

    const flakeSize = Math.random() * 1.5 + 0.5;
    flake.style.fontSize = `${flakeSize}em`;
    flake.style.left = Math.random() * 100 + 'vw';
    flake.style.animationDuration = Math.random() * 3 + 2 + 's';
    flake.style.animationDelay = Math.random() * 5 + 's';

    matrixContainer.appendChild(flake);

    setTimeout(() => {
        flake.remove();
    }, 10000);
}
// Generate flakes continuously
setInterval(createSnowFlakes, 50);
