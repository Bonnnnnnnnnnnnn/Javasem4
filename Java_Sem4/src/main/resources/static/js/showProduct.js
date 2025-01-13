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
// xóa nhieuf ảnh
function toggleSelectAll(selectAllCheckbox) {
    const checkboxes = document.querySelectorAll('.productCheckbox');
    checkboxes.forEach(checkbox => {
        checkbox.checked = selectAllCheckbox.checked;
    });
    
    toggleDeleteButton();
}

// Hàm xóa các sản phẩm đã chọn
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

// Hàm kiểm tra trạng thái của các checkbox và bật/tắt nút xóa
function toggleDeleteButton() {
    const checkboxes = document.querySelectorAll('.productCheckbox');
    const deleteBtn = document.getElementById('deleteSelectedBtn');

    const isAnyChecked = Array.from(checkboxes).some(checkbox => checkbox.checked);

    deleteBtn.disabled = !isAnyChecked;
}

// Hàm kiểm tra và thay đổi trạng thái "Select all"
function updateProductStatus(element) {
    const productId = element.getAttribute('data-id');
    const currentStatus = element.getAttribute('data-status');
    const statuses = ["Active", "InActive", "OutOfstock", "NewRelease"];

    let currentIndex = statuses.indexOf(currentStatus);
    let nextIndex = (currentIndex + 1) % statuses.length;
    let newStatus = statuses[nextIndex];

    // Hiển thị hộp thoại xác nhận
    const isConfirmed = confirm(`Are you sure you want to change the status to ${newStatus}?`);

    // Nếu người dùng xác nhận, thực hiện thay đổi trạng thái
    if (isConfirmed) {
        const statusElement = document.getElementById("status-" + productId);
        if (statusElement) {
            statusElement.innerText = newStatus;
        } else {
            console.error(`Status element with ID status-${productId} not found!`);
            return;
        }

        fetch(`/admin/product/updateStatus`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify({
                id: productId,
                status: newStatus,
            }),
        })
            .then(response => {
                if (response.status === 200) {
                    element.setAttribute('data-status', newStatus);
                } else {
                    console.error('Failed to update status:', response.status);
                    statusElement.innerText = currentStatus;
                }
            })
            .catch(error => {
                console.error('Error updating status:', error);
                statusElement.innerText = currentStatus;
            });
    } else {
        // Nếu người dùng không xác nhận, không làm gì
        console.log('Status update was cancelled');
    }
}





		            // Nếu có ít nhất một checkbox được chọn thì hiển thị nút, ngược lại thì ẩn nút
		            deleteButton.style.display = isAnyChecked ? 'block' : 'none';
		        
				
				
/*				document.querySelector('.btn.btn-success').addEventListener('click', function() {
				    // Lấy giá trị từ data-* attributes
				    const productId = this.getAttribute('data-product-id');
				    const unitName = this.getAttribute('data-unit-name');
				    
				    // Gọi hàm với các tham số này
				    showAddConversionForm(productId, unitName);
				});
				
				

					let units = [];
					fetch('/admin/product/getUnits')
					    .then(response => {
					        if (!response.ok) {
					            throw new Error('Network response was not ok');
					        }
					        return response.json();
					    })
					    .then(data => {
					        if (Array.isArray(data)) {
					            units = data;
					            console.log("Loaded units:", units);
					        } else {
					            console.error("Server response is not an array:", data);
					            alert("Failed to load unit data.");
					        }
					    })
					    .catch(error => {
					        console.error("Error fetching units:", error);
					        alert("Failed to load unit data. Please check the network or server.");
					    });

						
						
					document.addEventListener('DOMContentLoaded', function() {
					    const addConversionButtons = document.querySelectorAll('.btn.btn-success.Conversion');
	
					    addConversionButtons.forEach(function(button) {
					        button.addEventListener('click', function() {
					            const productId = this.getAttribute('data-product-id');
					            const unitName = this.getAttribute('data-unit-name');
								const unitId = this.getAttribute('data-unit-id');
					            showAddConversionForm(productId, unitName, unitId);
					        });
					    });
					});
					function showAddConversionForm(productId, unitName, unitId) {
					    const formHtml = `
					        <div id="addConversionModal" class="modal" style="display: block;">
					            <div class="modal-content">
					                <span class="close" onclick="closeAddConversionForm()">&times;</span>
					                <h3>Add Conversion</h3>
									<form action="/admin/product/addConversion" method="post" >
									
										<input type="hidden" name="product_id" value="${productId}">
					                    <div class="form-group">
					                        <label for="fromUnit">From Unit</label>
					                        <input type="text"  value="${unitName}" class="form-control" readonly>
											<input type="hidden" name="to_unit_id" value="${unitId}">
					                    </div>
					                    <div class="form-group">
					                        <label for="toUnit">To Unit</label>
					                        <select name="from_unit_id" id="from_unit_id" class="form-control" required>
					                            ${units
					                                .map(unit => `<option value="${unit.id}">${unit.name}</option>`)
					                                .join("")}
					                        </select>
					                    </div>
					                    <div class="form-group">
					                        <label for="conversionRate">Conversion Rate</label>
					                        <input type="number" id="conversionRate" name="conversion_rate" class="form-control" required>
					                    </div>
					                    <button type="submit" class="btn btn-primary">Submit</button>
					                    <button type="button" class="btn btn-secondary" onclick="closeAddConversionForm()">Cancel</button>
					                </form>
					            </div>
					        </div>
					        <style>
					            .modal { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.5); }
					            .modal-content { background: white; margin: 15% auto; padding: 20px; width: 40%; border-radius: 5px; }
					            .close { float: right; font-size: 28px; font-weight: bold; cursor: pointer; }
					        </style>
					    `;

					    document.body.insertAdjacentHTML("beforeend", formHtml);
					}

					function deleteConversion(conversionId) {
					    if (confirm("Are you sure you want to delete this conversion?")) {
					        fetch('/admin/deleteConversion', {
					            method: 'POST',
					            headers: {
					                'Content-Type': 'application/x-www-form-urlencoded',
					            },
					            body: `conversionId=${conversionId}`
					        }).then(response => {
					            if (response.ok) {
					                window.location.reload();
					            } else {
					                alert("Failed to delete conversion.");
					            }
					        });
					    }
					}


				// Đóng form modal
				function closeAddConversionForm() {
				    const modal = document.getElementById("addConversionModal");
				    if (modal) {
				        modal.remove();
				    }
				}*/



