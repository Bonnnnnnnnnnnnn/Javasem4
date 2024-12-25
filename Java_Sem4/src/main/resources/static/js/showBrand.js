//delete brand
function confirmDeleteBr() {
	return confirm("Are you sure you want to delete this Brand ?");
}
$(document).ready(function () {
        // Kiểm tra xem có thông báo thành công hay không
        var message = /*[[${message}]]*/'';
        if (message) {
            // Hiển thị thông báo Toast
            var toast = $('#toast-message');
            toast.text(message).fadeIn(400); // Hiển thị thông báo
            setTimeout(function () {
                toast.fadeOut(3000); // Mờ dần trong 3 giây
            }, 3000); // Đợi 3 giây rồi mới mờ dần
        }
    });