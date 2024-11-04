function removeDollarSign(input) {
    input.value = input.value.replace(/[$,]/g, '');
}

function addDollarSign(input) {
    if (input.value) {
        input.value = '$' + parseFloat(input.value).toFixed(2);
    }
}
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


