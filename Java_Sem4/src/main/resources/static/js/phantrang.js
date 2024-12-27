let currentPage = 0;
const productsPerPage = 3;
const productId = /*[[${product.id}]]*/ 0;

function loadProductSpecifications(page) {
    console.log('Product ID:', productId); // Kiểm tra giá trị productId
    $.ajax({
        url: '/admin/product/getProductSpecifications',
        type: 'GET',
        data: {
            page: page,
            productId: productId
        },
		
        success: function(data) {
            console.log('Specifications Data:', data); // Log toàn bộ dữ liệu để kiểm tra // Kiểm tra dữ liệu trả về
            let tbody = '';
            data.forEach((item, index) => {
                tbody += `<tr class="text-center">
                    <td>${page * productsPerPage + index + 1}</td>
                    <td>${item.name_spe}</td>
                    <td>${item.des_spe}</td>
                    <td>
                        <a href="/admin/product/showUpdatePs(id=${item.id})" class="btn btn-warning">Update</a>
                        <a href="javascript:void(0);" onclick="return confirmDeletePs(${item.id});" class="btn btn-danger">Delete</a>
                    </td>
                </tr>`;
            });
            if (tbody === '') {
                tbody = `<tr class="text-center"><td colspan="4">No specifications found</td></tr>`;
            }
            $('#specifications-body').html(tbody);
        },
        error: function(xhr, status, error) {
            console.error('Error loading specifications:', error);
        }
    });
}

function loadPagination(totalSpecifications, currentPage) {
    const totalPages = Math.ceil(totalSpecifications / productsPerPage);
    let paginationHtml = '';

    for (let i = 0; i < totalPages; i++) {
        paginationHtml += `<a href="#" class="page-link ${i === currentPage ? 'active' : ''}" 
                           data-page="${i}" 
                           style="margin: 0 5px; text-decoration: none; padding: 5px 10px; border: 1px solid #007bff; color: #007bff; border-radius: 5px;">${i + 1}</a>`;
    }

    $('#pagination').html(paginationHtml);

    $('#pagination').off('click', '.page-link').on('click', '.page-link', function(event) {
        event.preventDefault();
        const page = $(this).data('page');
        currentPage = page;
        loadProductSpecifications(currentPage);
    });
}

$(document).ready(function() {
    loadProductSpecifications(currentPage);
});
