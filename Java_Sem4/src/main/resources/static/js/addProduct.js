const priceInput = document.getElementById('price');
priceInput.addEventListener('blur', function (e) {
    let input = e.target.value;

    // Chỉ giữ lại các ký tự số và dấu chấm
    input = input.replace(/[^0-9.]/g, '');

    let value = parseFloat(input);
    if (!isNaN(value)) {
        // Giới hạn giá trị tối đa là 100000
        if (value > 100000) {
            value = 100000;
            alert('Maximum price allowed is 100,000 USD.');
        }
        e.target.value = value.toFixed(2); // Hiển thị giá trị với 2 chữ số sau dấu phẩy
    } else {
        e.target.value = ''; // Nếu không phải số, đặt lại input
    }
});

priceInput.addEventListener('input', function (e) {
    e.target.value = e.target.value.replace(/[^0-9.]/g, ''); // Xóa ký tự không hợp lệ

    const value = parseFloat(e.target.value);
    if (value > 100000) {
        e.target.value = '100000';
        alert('Maximum price allowed is 100,000 USD.');
    }
});

function showImage(event) {
       const file = event.target.files[0];
       const preview = document.getElementById('preview');

       if (file) {
           const reader = new FileReader();
           reader.onload = function(e) {
               preview.src = e.target.result;
               preview.style.display = 'block'; // Hiển thị hình ảnh
           };
           reader.readAsDataURL(file); // Đọc nội dung file
       } else {
           preview.src = '#';
           preview.style.display = 'none'; // Ẩn hình ảnh nếu không có file
       }
   }
   
   CKEDITOR.replace('description');
   document.getElementById('searchBrand').addEventListener('input', function() {
       const filter = this.value.toLowerCase();
       const options = document.getElementById('brandId').options;

       for (let i = 0; i < options.length; i++) {
           const optionText = options[i].text.toLowerCase();
           options[i].style.display = optionText.includes(filter) ? '' : 'none';
       }
   });
   



