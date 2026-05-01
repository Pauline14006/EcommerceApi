/**
 * app.js – EcommerceAPI Frontend
 *
 * Task 5: Replaces all hard-coded static arrays with live Fetch API calls to the
 * Spring Boot backend. Every function uses async/await and wraps network calls in
 * try...catch blocks. response.ok is checked manually because the Fetch API only
 * rejects the Promise on network failures (e.g. no internet) – HTTP error codes
 * like 404 or 500 still resolve, so we must inspect the status ourselves.
 *
 * @author P.M A. Gallamora
 * @author P.G C. Torres
 */

// ─── Configuration ────────────────────────────────────────────────────────────
const BASE_URL = 'http://localhost:8080/api/v1/products';

// ─── DOM References ───────────────────────────────────────────────────────────
const productGrid   = document.getElementById('productGrid');
const filterType    = document.getElementById('filterType');
const filterValue   = document.getElementById('filterValue');
const btnFilter     = document.getElementById('btnFilter');
const btnClear      = document.getElementById('btnClear');
const btnAdd        = document.getElementById('btnAdd');
const formMsg       = document.getElementById('formMsg');
const resultCount   = document.getElementById('resultCount');
const headerSearch  = document.getElementById('headerSearch');
const btnHdrSearch  = document.getElementById('btnHeaderSearch');

// ─── Utility Helpers ──────────────────────────────────────────────────────────

function showLoading() {
    productGrid.innerHTML = `<p class="state-msg">⏳ Loading products...</p>`;
    if (resultCount) resultCount.innerHTML = 'Loading...';
}

function showEmpty() {
    productGrid.innerHTML = `<p class="state-msg">📦 No products found.</p>`;
    if (resultCount) resultCount.innerHTML = '<span>0</span> products found';
}

function showError(message) {
    productGrid.innerHTML = `<p class="state-msg error">❌ ${message}</p>`;
}

// ─── Core Fetch Functions ─────────────────────────────────────────────────────

/**
 * Task 5 – fetchProducts()
 *
 * Fetches all products from the backend and renders them into the product grid.
 * Called automatically on page load and after any write operation to refresh the UI.
 *
 * Error handling strategy:
 *  1. try...catch wraps the entire async block.
 *  2. response.ok is checked manually – a 404 or 500 response resolves the Promise
 *     but has ok === false, so we throw a custom Error with the HTTP status.
 *  3. Specific error messages are logged to the console for debugging.
 *
 * @param {string|null} url - Optional override URL (used for filtered calls).
 */
async function fetchProducts(url = null) {
    showLoading();

    try {
        const endpoint = url || BASE_URL;
        const response = await fetch(endpoint);

        // Manual check – Fetch only rejects on network failure, NOT on 4xx/5xx
        if (!response.ok) {
            if (response.status === 404) {
                throw new Error('Resource not found (404).');
            }
            if (response.status >= 500) {
                throw new Error(`Server error (${response.status}). Please try again later.`);
            }
            throw new Error(`Unexpected error: HTTP ${response.status}`);
        }

        const products = await response.json();
        console.log('[fetchProducts] Received', products.length, 'product(s):', products);

        if (products.length === 0) {
            showEmpty();
            return;
        }

        if (resultCount) {
            resultCount.innerHTML = `<span>${products.length}</span> product${products.length !== 1 ? 's' : ''} found`;
        }

        renderProducts(products);

    } catch (error) {
        console.error('[fetchProducts] Error:', error.message);
        showError(error.message);
    }
}

/**
 * Sends a POST request to create a new product.
 *
 * @param {Object} productData  - The product fields.
 * @param {string} categoryName - The category name sent as a query parameter.
 */
async function createProduct(productData, categoryName) {
    try {
        const url = `${BASE_URL}?categoryName=${encodeURIComponent(categoryName)}`;
        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Accept': 'application/json'
            },
            body: JSON.stringify(productData)
        });

        if (!response.ok) {
            const errorBody = await response.json().catch(() => null);
            const msg = errorBody?.message || `Failed to create product (HTTP ${response.status})`;
            throw new Error(msg);
        }

        const created = await response.json();
        console.log('[createProduct] Created:', created);
        return created;

    } catch (error) {
        console.error('[createProduct] Error:', error.message);
        throw error;
    }
}

/**
 * Sends a DELETE request to remove a product by its ID.
 *
 * @param {number} id - The ID of the product to delete.
 */
async function deleteProduct(id) {
    try {
        const response = await fetch(`${BASE_URL}/${id}`, { method: 'DELETE' });

        if (!response.ok) {
            if (response.status === 404) {
                throw new Error(`Product with ID ${id} not found.`);
            }
            throw new Error(`Failed to delete product (HTTP ${response.status})`);
        }

        console.log(`[deleteProduct] Product ${id} deleted successfully.`);

    } catch (error) {
        console.error('[deleteProduct] Error:', error.message);
        throw error;
    }
}

// ─── Rendering ────────────────────────────────────────────────────────────────

/**
 * Converts an array of product objects into HTML cards and injects them into the DOM.
 *
 * @param {Array} products - Array of product objects returned from the API.
 */
function renderProducts(products) {
    productGrid.innerHTML = products.map(p => `
        <div class="product-card" data-id="${p.id}">
            ${p.imageUrl
                ? `<img class="product-img" src="${p.imageUrl}" alt="${p.name}" onerror="this.style.display='none'" />`
                : `<div class="product-img-placeholder">📦</div>`
            }
            <div class="product-body">
                <p class="product-category">${p.categoryName || 'Uncategorised'}</p>
                <p class="product-name">${p.name}</p>
                <p class="product-desc">${p.description || ''}</p>
                <p class="product-price">₱${p.price.toFixed(2)}</p>
                <p class="product-stock">${p.stockQuantity} units left</p>
            </div>
            <div class="product-footer">
                <button class="danger btn-delete" data-id="${p.id}">🗑 Delete</button>
            </div>
        </div>
    `).join('');

    // Attach delete handlers
    document.querySelectorAll('.btn-delete').forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.stopPropagation();
            const id = btn.dataset.id;
            if (!confirm(`Delete product #${id}?`)) return;
            try {
                await deleteProduct(id);
                fetchProducts();
            } catch (err) {
                alert(err.message);
            }
        });
    });
}

// ─── Event Listeners ──────────────────────────────────────────────────────────

// Filter button
btnFilter.addEventListener('click', () => {
    const type  = filterType.value.trim();
    const value = filterValue.value.trim();
    if (!type || !value) { fetchProducts(); return; }
    const url = `${BASE_URL}/filter?filterType=${encodeURIComponent(type)}&filterValue=${encodeURIComponent(value)}`;
    fetchProducts(url);
});

// Clear button
btnClear.addEventListener('click', () => {
    filterType.value  = '';
    filterValue.value = '';
    // Reset active category button
    document.querySelectorAll('.cat-btn').forEach(b => b.classList.remove('active'));
    document.querySelector('.cat-btn[data-cat=""]').classList.add('active');
    fetchProducts();
});

// Header search bar
function doHeaderSearch() {
    const keyword = headerSearch.value.trim();
    if (!keyword) { fetchProducts(); return; }
    const url = `${BASE_URL}/filter?filterType=name&filterValue=${encodeURIComponent(keyword)}`;
    fetchProducts(url);
}
btnHdrSearch.addEventListener('click', doHeaderSearch);
headerSearch.addEventListener('keydown', (e) => { if (e.key === 'Enter') doHeaderSearch(); });

// Category nav buttons
document.querySelectorAll('.cat-btn').forEach(btn => {
    btn.addEventListener('click', () => {
        document.querySelectorAll('.cat-btn').forEach(b => b.classList.remove('active'));
        btn.classList.add('active');
        const cat = btn.dataset.cat;
        if (!cat) { fetchProducts(); return; }
        const url = `${BASE_URL}/filter?filterType=category&filterValue=${encodeURIComponent(cat)}`;
        fetchProducts(url);
    });
});

// Add Product button
btnAdd.addEventListener('click', async () => {
    const name     = document.getElementById('newName').value.trim();
    const category = document.getElementById('newCategory').value.trim();
    const price    = parseFloat(document.getElementById('newPrice').value);
    const stock    = parseInt(document.getElementById('newStock').value) || 0;
    const desc     = document.getElementById('newDesc').value.trim();
    const imageUrl = document.getElementById('newImage').value.trim();

    // Client-side validation
    if (!name || name.length < 2) { showFormMsg('Product name must be at least 2 characters.', false); return; }
    if (!category)                 { showFormMsg('Category is required.', false); return; }
    if (!price || price <= 0)      { showFormMsg('Price must be a positive number.', false); return; }

    try {
        await createProduct(
            { name, description: desc, price, stockQuantity: stock, imageUrl: imageUrl || null },
            category
        );
        showFormMsg(`✅ "${name}" added!`, true);
        clearForm();
        fetchProducts();
    } catch (err) {
        showFormMsg(`❌ ${err.message}`, false);
    }
});

// ─── Form Helpers ─────────────────────────────────────────────────────────────

function showFormMsg(msg, success) {
    formMsg.textContent = msg;
    formMsg.className   = 'form-msg ' + (success ? 'ok' : 'err');
    setTimeout(() => { formMsg.textContent = ''; formMsg.className = 'form-msg'; }, 4000);
}

function clearForm() {
    ['newName', 'newCategory', 'newPrice', 'newStock', 'newDesc', 'newImage']
        .forEach(id => { document.getElementById(id).value = ''; });
}

// ─── Initialisation ───────────────────────────────────────────────────────────

// Task 5: Call fetchProducts() on page load
fetchProducts();
