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







		  