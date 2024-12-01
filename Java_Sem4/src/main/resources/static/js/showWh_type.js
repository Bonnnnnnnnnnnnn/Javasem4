
		//delete warehouseType
		function confirmDeleteType(){
			return confirm("Are you sure you want to delete this warehouseType ?");
		}
		//delete warehouse
		function confirmDeleteWh(){
					return confirm("Are you sure you want to delete this warehouse ?");
		}
		//click vào xem chi tiết sản phẩm
		  function goToDetail(id) {
		        window.location.href = '/admin/warehouse/showWarehouseDetails?id=' + id;
		    }
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
			//xoa all warehuuse
			function deleteSelectedWarehouses() {
			    const selectedWarehouseIds = [];
			    document.querySelectorAll('.warehouseCheckbox:checked').forEach(checkbox => {
			        selectedWarehouseIds.push(parseInt(checkbox.value));
			    });
		
			    if (selectedWarehouseIds.length > 0) {
			        if (confirm('Are you sure you want to delete the selected warehouses?')) {
			            fetch('/admin/warehouse/deleteSelectedWarehouses', {
			                method: 'POST',
			                headers: {
			                    'Content-Type': 'application/json'
			                },
			                body: JSON.stringify(selectedWarehouseIds)
			            })
			            .then(response => {
			                if (response.ok) {
			                    return response.text();
			                } else {
			                    throw new Error('Failed to delete warehouses.');
			                }
			            })
			            .then(data => {
			                console.log(data);
			                window.location.reload();
			            })
			            .catch(error => {
			                console.error('Error:', error);
			                alert('An error occurred while deleting warehouses: ' + error.message);
			            });
			        }
			    } else {
			        alert('Please select at least one warehouse to delete.');
			    }
			}
			function toggleSelectAll(selectAllCheckbox) {
			    const checkboxes = document.querySelectorAll('.warehouseCheckbox');
			    checkboxes.forEach(checkbox => {
			        checkbox.checked = selectAllCheckbox.checked;
			    });
			}
			function loadWarehouses(typeId) {
			    loadWarehousesByTypeId(typeId);
			}
			function loadWarehousesByTypeId(typeId) {
			    $.ajax({
			        url: '/admin/warehouseType/getWarehousesByType?typeId=' + typeId,
			        type: 'GET',
			        success: function(response) {
			            var newTableHtml = `
			                <table class="table table-hover text-nowrap" id="warehouseTable">
			                    <thead>
			                        <tr class="text-center">
			                            <th>Name</th>
			                            <th>Address</th>
			                            <th>Type Name</th>
			                        </tr>
			                    </thead>
			                    <tbody>
			            `;
		
			            if (response.length === 0) {
			                newTableHtml += '<tr class="text-center"><td colspan="4">No warehouses found</td></tr>';
			            } else {
			                response.forEach(function(warehouse) {
			                    newTableHtml += `
			                        <tr class="text-center">
			                            <td>${warehouse.name}</td>
			                            <td>${warehouse.address}</td>
			                            <td>${warehouse.typeName}</td>
			                        </tr>
			                    `;
			                });
			            }
		
			            newTableHtml += `
			                    </tbody>
			                </table>
			                <div class="text-center mt-3">
			                    <a href="/admin/warehouseType/showWhType" class="btn btn-secondary">Back to List</a>
			                </div>
			            `;
		
			            $('#warehouseTypeTable').replaceWith(newTableHtml);
			        },
			        error: function() {
			            alert('Failed to load warehouses');
			        }
			    });
			}
			function searchTable() {
			       var searchTerm = $('#searchInput').val().toLowerCase();
			       var rows = $('#warehouseTable tr');

			       rows.each(function() {
			           var employeeName = $(this).find('td:nth-child(2) span').text().toLowerCase();
			           var warehouseName = $(this).find('td:nth-child(3) span').text().toLowerCase();

			           if (employeeName.indexOf(searchTerm) !== -1 || warehouseName.indexOf(searchTerm) !== -1) {
			               $(this).show();
			           } else {
			               $(this).hide();
			           }
			       });
			   }






