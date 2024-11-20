		//search text
		function searchTable() {
		    var searchTerm = $('#searchInput').val().toLowerCase();
		    var rows = $('table tbody tr');

		    rows.each(function() {
		        var rowText = $(this).text().toLowerCase();
		        if (rowText.indexOf(searchTerm) === -1) {
		            $(this).hide();
		        } else {
		            $(this).show();
		        }
		    });
		}

		//delete thông báo product
		function confirmDelete() {
		   return confirm("Are you sure you want to delete this product?");
		  }
		//click vào xem chi tiết sản phẩm
  	    function goToDetail(id) {
  	          window.location.href = '/admin/product/showProductDetail?id=' + id;
  	      }
		  //phân trang
	  	function loadPage(pageNumber) {
	  	    $.ajax({
	  	        type: "GET",
	  	        url: "/admin/product/showProduct",
	  	        data: {
	  	            cp: pageNumber
	  	        },
	  	        success: function(response) {
	  	            $('#pageContent').html(response); 
	  	
	  	            updatePagination(response.pv);
	  	        },
	  	        error: function(e) {
	  	            console.log("Error: ", e);
	  	        }
	  	    });
	  	}
		//xóa all sản phẩm
		function toggleSelectAll(selectAllCheckbox) {
		    const checkboxes = document.querySelectorAll('.productCheckbox');
		    checkboxes.forEach(checkbox => {
		        checkbox.checked = selectAllCheckbox.checked;
		    });
		}
		function deleteSelectedProducts() {
		    const selectedProductIds = [];
		    document.querySelectorAll('.productCheckbox:checked').forEach(checkbox => {
		        selectedProductIds.push(parseInt(checkbox.value));
		    });

		    if (selectedProductIds.length > 0) {
		        if (confirm('Are you sure you want to delete the selected products?')) {
		            fetch('/admin/product/deleteSelected', {
		                method: 'POST',
		                headers: {
		                    'Content-Type': 'application/json'
		                },
		                body: JSON.stringify(selectedProductIds)
		            })
		            .then(response => {
		                if (response.ok) {
		                    return response.text();
		                } else {
		                    throw new Error('Failed to delete products.');
		                }
		            })
		            .then(data => {
		                console.log(data);
		                window.location.reload();
		            })
		            .catch(error => {
		                console.error('Error:', error);
		                alert('An error occurred while deleting products: ' + error.message);
		            });
		        }
		    } else {
		        alert('Please select at least one product to delete.');
		    }
		}  
		function toggleSelectAll(selectAllCheckbox) {
		            const checkboxes = document.querySelectorAll('.productCheckbox');
		            checkboxes.forEach((checkbox) => {
		                checkbox.checked = selectAllCheckbox.checked;
		            });
		            toggleDeleteButton(); // Gọi hàm để kiểm tra việc ẩn/hiện nút "Delete Selected"
		        }

		        // Kiểm tra trạng thái checkbox và ẩn/hiện nút "Delete Selected"
		        function toggleDeleteButton() {
		            const checkboxes = document.querySelectorAll('.productCheckbox');
		            const deleteButton = document.getElementById('deleteSelectedButton');
		            let isAnyChecked = false;

		            // Kiểm tra nếu có checkbox nào được chọn
		            checkboxes.forEach((checkbox) => {
		                if (checkbox.checked) {
		                    isAnyChecked = true;
		                }
		            });

		            // Nếu có ít nhất một checkbox được chọn thì hiển thị nút, ngược lại thì ẩn nút
		            deleteButton.style.display = isAnyChecked ? 'block' : 'none';
		        }
		  