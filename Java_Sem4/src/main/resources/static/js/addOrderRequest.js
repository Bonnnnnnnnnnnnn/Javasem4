let products = [];

// Lấy dữ liệu sản phẩm từ server
fetch('/businessManager/getProducts')
    .then(response => {
        if (!response.ok) {
            throw new Error('Network response was not ok');
        }
        return response.json();
    })
    .then(data => {
        if (Array.isArray(data)) {
            products = data;
            console.log("Loaded Products:", products);
            document.getElementById('addDetailButton').style.display = 'inline-block'; 
        } else {
            console.error("Server response is not an array:", data);
            alert("Failed to load product data.");
        }
    })
    .catch(error => {
        console.error("Error fetching products:", error);
        alert("Failed to load product data.");
    });

let detailIndex = 0;

function addDetail() {
    const detailsDiv = document.getElementById('details');
    detailIndex++;

    const newDetail = document.createElement('div');
    newDetail.className = 'detail-entry';
    newDetail.innerHTML = `
        <div class="form-row">
            <div class="form-group col-md-6">
                <label for="productSearch${detailIndex}">Product Name</label>
                <input type="text" id="productSearch${detailIndex}" class="form-control" placeholder="Search for a product..." oninput="filterProducts(${detailIndex})">
                <ul id="productList${detailIndex}" class="product-list" style="display: none;"></ul>
                <input type="hidden" id="id_product${detailIndex}" name="id_product" class="form-control" required>
                <span id="selectedProductId${detailIndex}" class="product-id"></span>
            </div>
            <div class="form-group col-md-6">
                <label for="quantity_requested${detailIndex}">Quantity</label>
                <input type="number" id="quantity_requested${detailIndex}" name="quantity_requested" class="form-control" placeholder="Enter Quantity" required>
            </div>
        </div>
        <input type="hidden" id="status${detailIndex}" name="status" value="Pending">
		<button type="button" class="btn btn-danger remove-detail-btn" onclick="removeDetail(this)">Remove</button>
    `;
    detailsDiv.appendChild(newDetail);
}

// Lọc sản phẩm dựa trên văn bản tìm kiếm
function filterProducts(detailIndex) {
    const searchInput = document.getElementById(`productSearch${detailIndex}`);
    const productList = document.getElementById(`productList${detailIndex}`);
    const filterText = searchInput.value.toLowerCase();

    // Hiển thị sản phẩm nào có tên chứa văn bản tìm kiếm
    const filteredProducts = products.filter(product =>
        product.product_name.toLowerCase().includes(filterText)
    );

    // Xóa danh sách hiện tại
    productList.innerHTML = '';

    if (filteredProducts.length > 0) {
        productList.style.display = 'block';
        filteredProducts.forEach(product => {
            const li = document.createElement('li');
            li.textContent = product.product_name;
            li.onclick = () => selectProduct(product, detailIndex);
            productList.appendChild(li);
        });
    } else {
        productList.style.display = 'none';
    }
}

// Hàm chọn sản phẩm và gán giá trị vào trường id_product
function selectProduct(product, detailIndex) {
    // Gán tên sản phẩm vào ô tìm kiếm
    document.getElementById(`productSearch${detailIndex}`).value = product.product_name;
    
    // Gán ID sản phẩm vào input hidden và hiển thị ID đã chọn
    document.getElementById(`id_product${detailIndex}`).value = product.id;


    // Ẩn danh sách sản phẩm sau khi chọn xong
    document.getElementById(`productList${detailIndex}`).style.display = 'none';
}

// Hàm xóa một dòng chi tiết đơn hàng
function removeDetail(button) {
    button.closest('.detail-entry').remove();
}