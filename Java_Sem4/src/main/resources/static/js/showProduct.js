		//search text
		function searchTable() {
		    var input = document.getElementById("searchInput");
		    var filter = input.value.toLowerCase();
		    var table = document.querySelector(".table tbody");
		    var rows = table.getElementsByTagName("tr");
		    for (var i = 0; i < rows.length; i++) {
		        var productNameCell = rows[i].getElementsByTagName("td")[1];
		        var categoryCell = rows[i].getElementsByTagName("td")[4]; 
		        var brandCell = rows[i].getElementsByTagName("td")[5]; 
		        var unitCell = rows[i].getElementsByTagName("td")[6]; 
		
		        var productName = productNameCell ? (productNameCell.textContent || productNameCell.innerText) : "";
		        var categoryName = categoryCell ? (categoryCell.textContent || categoryCell.innerText) : "";
		        var brandName = brandCell ? (brandCell.textContent || brandCell.innerText) : "";
		        var unitName = unitCell ? (unitCell.textContent || unitCell.innerText) : "";
		        if (productName || categoryName || brandName || unitName) {
		            if (productName.toLowerCase().indexOf(filter) > -1 || 
		                categoryName.toLowerCase().indexOf(filter) > -1 || 
		                brandName.toLowerCase().indexOf(filter) > -1 || 
		                unitName.toLowerCase().indexOf(filter) > -1) {
		                rows[i].style.display = "";
		            } else {
		                rows[i].style.display = "none";
		            }
		        } else {
		            rows[i].style.display = "none";
		        }    
		    }
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
		  