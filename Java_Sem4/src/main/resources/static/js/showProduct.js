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
		  
		  function confirmDeletePs() {
		     return confirm("Are you sure you want to delete this product spe?");
		    }
			function confirmDeletePi() {
			   return confirm("Are you sure you want to delete this product img?");
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
		document.addEventListener('DOMContentLoaded', function() {
		    const urlParams = new URLSearchParams(window.location.search);
		    const activeTab = urlParams.get('activeTab');

		    if (activeTab === 'productSpecifications') {
		        const tab = new bootstrap.Tab(document.querySelector('#productSpecifications-tab'));
		        tab.show();
		    }
		});
		// xóa nhieuf ảnh
		function toggleSelectAll(selectAllCheckbox) {
		    const checkboxes = document.querySelectorAll('.productCheckbox');
		    checkboxes.forEach(checkbox => {
		        checkbox.checked = selectAllCheckbox.checked;
		    });
		}
		function deleteSelectedImg() {
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
				function toggleDeleteButton() {
				    const checkboxes = document.querySelectorAll('.productCheckbox');
				    const deleteBtn = document.getElementById('deleteSelectedBtn');
				    
				    // Kiểm tra nếu có ít nhất một checkbox được chọn
				    const isAnyChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);
				    
				    // Hiển thị hoặc ẩn nút xóa
				    if (isAnyChecked) {
				        deleteBtn.style.display = 'inline-block'; // Hiển thị nút xóa
				    } else {
				        deleteBtn.style.display = 'none'; // Ẩn nút xóa
				    }
				}

				// Hàm kiểm tra và thay đổi trạng thái "Select all"
				function toggleSelectAll(selectAllCheckbox) {
				    const checkboxes = document.querySelectorAll('.productCheckbox');
				    checkboxes.forEach(checkbox => {
				        checkbox.checked = selectAllCheckbox.checked;
				    });
				    toggleDeleteButton(); // Cập nhật nút xóa khi "Select all" thay đổi
				}






		  