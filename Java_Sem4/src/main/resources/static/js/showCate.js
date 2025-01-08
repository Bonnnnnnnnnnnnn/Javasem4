		//delete category
		function confirmDeleteCate(){
			return confirm("Are you sure you want to delete this category ?");
		}
		
		$(document).ready(function () {
		    $('#add-category-form').on('submit', function (e) {
		        e.preventDefault(); 

		        let formData = $(this).serialize();
		        $.ajax({
		            url: $(this).attr('action'),
		            type: 'POST',
		            data: formData,
		            success: function (response) {
		                let toast = $('#toast-message');
		                if (toast.length > 0) {
		                    toast.text('Danh mục đã được thêm thành công!');
		                    toast.addClass('show');

		                    setTimeout(function () {
		                        toast.removeClass('show');
		                    }, 3000);

		                    setTimeout(function () {
		                        location.href = '/admin/category/showCategory';
		                    }, 3400);
		                } else {
		                    console.warn('Không tìm thấy thẻ #toast-message trong DOM!');
		                }
		            },
		            error: function () {
		                alert('Có lỗi xảy ra khi thêm danh mục.');
		            }
		        });
		    });
		});
		function goToDetail(id) {
		    window.location.href = '/admin/category/showProductFromCategory?id=' + id;
		}

		







