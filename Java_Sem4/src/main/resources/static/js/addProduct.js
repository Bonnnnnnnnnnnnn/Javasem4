const priceInput = document.getElementById('price');
const priceChangesInput = document.getElementById('priceChanges');

function formatCurrency(value) {
    return value.toFixed(2);
}

function parseAndFormat(value) {
    value = value.replace(/[^0-9.]/g, ''); // Xóa ký tự không hợp lệ
    const num = parseFloat(value);
    return isNaN(num) ? '' : num.toFixed(2); // Hiển thị 2 chữ số sau dấu phẩy
}

function updatePriceChanges() {
    const priceValue = parseFloat(priceInput.value.replace(/[^0-9.]/g, '')) || 0;
    priceChangesInput.value = formatCurrency(priceValue); // Cập nhật giá trị cho ô "Price Changes"
}

// Xử lý khi người dùng rời khỏi ô nhập (blur)
function handleBlur(event) {
    let value = event.target.value;
    value = parseAndFormat(value); // Định dạng giá trị
    event.target.value = value; // Cập nhật lại giá trị ô nhập

    // Cập nhật giá trị cho "Price Changes"
    updatePriceChanges();
}

// Gán sự kiện cho ô "Price"
priceInput.addEventListener('blur', handleBlur);

// Khi nhập liệu, chỉ cho phép số và dấu chấm
function handleInput(event) {
    event.target.value = event.target.value.replace(/[^0-9.]/g, ''); // Xóa ký tự không hợp lệ
}

// Gán sự kiện cho cả hai ô
priceInput.addEventListener('input', handleInput);
priceInput.addEventListener('input', handleInput);
priceChangesInput.addEventListener('input', handleInput); 
   //cái des cảu product
   CKEDITOR.replace('description');

       // Tìm kiếm thương hiệu
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
	   






   



