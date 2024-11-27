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
		document.addEventListener('DOMContentLoaded', function() {
		    const urlParams = new URLSearchParams(window.location.search);
		    const activeTab = urlParams.get('activeTab');

		    if (activeTab === 'productSpecifications') {
		        const tab = new bootstrap.Tab(document.querySelector('#productSpecifications-tab'));
		        tab.show();
		    }
		});







<<<<<<< HEAD
		            // Nếu có ít nhất một checkbox được chọn thì hiển thị nút, ngược lại thì ẩn nút
		            deleteButton.style.display = isAnyChecked ? 'block' : 'none';
		        }
				
				
				document.querySelector('.btn.btn-success').addEventListener('click', function() {
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
				}


=======
>>>>>>> trongdev
		  