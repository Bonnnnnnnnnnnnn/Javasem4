// định giá usa và bắt lỗi khi không nhập giá đồng nhất
const priceInput = document.getElementById('price');
const priceChangesInput = document.getElementById('priceChanges');
const errorMessage = document.getElementById('error-message');

function formatCurrency(value) {
    return value.toFixed(2);
}
function parseAndFormat(value) {
    value = value.replace(/[^0-9.]/g, ''); // Xóa ký tự không hợp lệ
    const num = parseFloat(value);
    return isNaN(num) ? '' : num.toFixed(2); // Hiển thị 2 chữ số sau dấu phẩy
}
function validateMatchingValues() {
    const priceValue = parseFloat(priceInput.value.replace(/[^0-9.]/g, '')) || 0;
    const priceChangesValue = parseFloat(priceChangesInput.value.replace(/[^0-9.]/g, '')) || 0;

    if (priceValue !== priceChangesValue) {
        errorMessage.style.display = 'block'; // Hiển thị thông báo lỗi
    } else {
        errorMessage.style.display = 'none'; // Ẩn thông báo lỗi
    }
}
// Xử lý khi người dùng rời khỏi ô nhập (blur)
function handleBlur(event) {
    const input = event.target;
    let value = input.value;
    value = parseAndFormat(value); // Định dạng giá trị
    if (value) {
        const numValue = parseFloat(value);
        if (numValue > 100000) {
            alert('Maximum price allowed is 100,000 USD.');
            input.value = formatCurrency(100000); // Đặt giá trị tối đa
        } else {
            input.value = formatCurrency(numValue); // Định dạng thành số với 2 chữ số thập phân
        }
    } else {
        input.value = '';
    }

    validateMatchingValues(); // Kiểm tra khớp giá trị
}
// Gán sự kiện cho cả hai ô
priceInput.addEventListener('blur', handleBlur);
priceChangesInput.addEventListener('blur', handleBlur);
// Khi nhập liệu, chỉ cho phép số và dấu chấm
function handleInput(event) {
    event.target.value = event.target.value.replace(/[^0-9.]/g, ''); // Xóa ký tự không hợp lệ
}
priceInput.addEventListener('input', handleInput);
priceChangesInput.addEventListener('input', handleInput);
// Gán sự kiện cho cả hai ô
priceInput.addEventListener('blur', handleBlur);
priceChangesInput.addEventListener('blur', handleBlur);
// Khi nhập liệu, chỉ cho phép số và dấu chấm
function handleInput(event) {
    event.target.value = event.target.value.replace(/[^0-9.]/g, ''); // Xóa ký tự không hợp lệ
}
priceInput.addEventListener('input', handleInput);
priceChangesInput.addEventListener('input', handleInput); 
   //cái des cảu product
   CKEDITOR.replace('description');
   document.getElementById('searchBrand').addEventListener('input', function() {
       const filter = this.value.toLowerCase();
       const options = document.getElementById('brandId').options;

       for (let i = 0; i < options.length; i++) {
           const optionText = options[i].text.toLowerCase();
           options[i].style.display = optionText.includes(filter) ? '' : 'none';
       }
   });
   // Chức năng xem trước ảnh chính
       function showImage(event) {
           const file = event.target.files[0];
           const preview = document.getElementById('preview');

           if (file) {
               const reader = new FileReader();
               reader.onload = function(e) {
                   preview.src = e.target.result; // Cập nhật src của hình ảnh chính
                   preview.style.display = 'block'; // Hiển thị hình ảnh chính
               };
               reader.readAsDataURL(file); // Đọc nội dung file
           } else {
               preview.style.display = 'none'; // Ẩn hình ảnh nếu không có file
           }
       }
	   document.getElementById("dateStart").value = new Date().toISOString();
	   






   



