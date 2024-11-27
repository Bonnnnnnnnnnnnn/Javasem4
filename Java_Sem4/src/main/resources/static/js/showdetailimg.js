// Chức năng xem trước ảnh bổ sung
document.addEventListener("DOMContentLoaded", function () {
       const imageInput = document.getElementById("additionalImages");
       const imagePreviewContainer = document.getElementById("imagePreviewContainer");

       imageInput.addEventListener("change", function (event) {
           const files = event.target.files; // Lấy danh sách file

           if (!files.length) return; // Không có file nào được chọn

           Array.from(files).forEach((file) => {
               if (!file.type.startsWith("image/")) {
                   alert("Please select only image files.");
                   return;
               }

               const fileReader = new FileReader();

               fileReader.onload = function (e) {
                   // Tạo div cho xem trước ảnh
                   const previewDiv = document.createElement("div");
                   previewDiv.className = "image-preview";

                   // Tạo thẻ img để hiển thị ảnh
                   const img = document.createElement("img");
                   img.src = e.target.result;

                   // Tạo nút xóa
                   const removeBtn = document.createElement("button");
                   removeBtn.className = "remove-btn";
                   removeBtn.textContent = "×";
                   removeBtn.addEventListener("click", function () {
                       previewDiv.remove(); // Xóa xem trước khi nhấn nút
                   });

                   // Thêm img và nút xóa vào previewDiv
                   previewDiv.appendChild(img);
                   previewDiv.appendChild(removeBtn);

                   // Thêm previewDiv vào container
                   imagePreviewContainer.appendChild(previewDiv);
               };

               fileReader.readAsDataURL(file); // Đọc file dưới dạng Data URL
           });
       });
   });