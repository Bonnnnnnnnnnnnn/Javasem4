
$(document).ready(function() {
       $.ajax({
           url: '/warehouseManager/warehouseReceipt/generateReceiptName',
           type: 'GET',
           success: function(data) {
               console.log("Received data:", data);
               $('#name').val(data);
           },
           error: function() {
               console.error("Failed to generate receipt name.");
           }
       });
   });
   //tìm kiếm product trong select
   function filterProducts() {
       const input = document.getElementById('searchProduct');
       const filter = input.value.toLowerCase();
       
       const select = document.getElementById('product_id');
       const options = select.options;
       
       let found = false;

       for (let i = 1; i < options.length; i++) { 
           const option = options[i];
           const txtValue = option.textContent || option.innerText;
           if (txtValue.toLowerCase().includes(filter)) {
               option.style.display = "";
               found = true;
           } else {
               option.style.display = "none";
           }
       }
       
       if (!found) {
           const noResultOption = document.createElement('option');
           noResultOption.textContent = 'No matching products';
           noResultOption.disabled = true;
           noResultOption.style.display = "";
           select.appendChild(noResultOption);
       } else {
           const noResultOption = select.querySelector('option[disabled][style*="display:"]');
           if (noResultOption) {
               select.removeChild(noResultOption);
           }
       }
   }


	   // Giới hạn cho shipping_fee
	   const shippingFeeInput = document.getElementById('shipping_fee');
	   shippingFeeInput.addEventListener('blur', function (e) {
	       let input = e.target.value;

	       input = input.replace(/[^0-9.]/g, '');

	       let value = parseFloat(input);
	       if (!isNaN(value)) {
	           if (value > 100000) {
	               value = 100000;
	               alert('Maximum shipping fee allowed is 100,000.');
	           }
	           e.target.value = value.toFixed(2);
	       } else {
	           e.target.value = '';
	       }
	   });

	   shippingFeeInput.addEventListener('input', function (e) {
	       e.target.value = e.target.value.replace(/[^0-9.]/g, '');

	       const value = parseFloat(e.target.value);
	       if (value > 100000) {
	           e.target.value = '100000';
	           alert('Maximum shipping fee allowed is 100,000.');
	       }
	   });

	   // Giới hạn cho wh_price
	   const wh_priceInput = document.getElementById('wh_price');
	   wh_priceInput.addEventListener('blur', function (e) {
	       let input = e.target.value;
	       input = input.replace(/[^0-9.]/g, '');

	       let value = parseFloat(input);
	       if (!isNaN(value)) {
	           if (value > 100000) {
	               value = 100000;
	               alert('Maximum warehouse price allowed is 100,000.');
	           }
	           e.target.value = value.toFixed(2);
	       } else {
	           e.target.value = '';
	       }
	   });
	   wh_priceInput.addEventListener('input', function (e) {
	       e.target.value = e.target.value.replace(/[^0-9.]/g, '');

	       const value = parseFloat(e.target.value);
	       if (value > 100000) {
	           e.target.value = '100000';
	           alert('Maximum warehouse price allowed is 100,000.');
	       }
	   });
	   // add thêm cột để nhập hàng
	   function addDetailGroup() {
	       const detailsContainer = document.getElementById('detailsContainer');

	       let receiptCounter = parseInt(detailsContainer.getAttribute('data-receipt-counter')) || 0;

	       receiptCounter++;
	       const originalReceiptGroup = detailsContainer.querySelector('.detail-group').cloneNode(true);

	       const existingTitle = originalReceiptGroup.querySelector('.receipt-title');
	       if (existingTitle) {
	           existingTitle.innerText = `Warehouse Receipt ${receiptCounter}`;
	       } else {
	           const newTitle = document.createElement('h3');
	           newTitle.classList.add('receipt-title');
	           newTitle.innerText = `Warehouse Receipt ${receiptCounter}`;
	           originalReceiptGroup.insertBefore(newTitle, originalReceiptGroup.firstChild);
	       }

	       const inputs = originalReceiptGroup.querySelectorAll('input, select');
	       inputs.forEach(input => {
	           input.value = '';
	           if (input.tagName === 'SELECT') {
	               input.selectedIndex = 0;
	           }
	       });

	       const separator = document.createElement('hr');
	       separator.classList.add('separator');

	       detailsContainer.appendChild(separator);
	       detailsContainer.appendChild(originalReceiptGroup);
	       detailsContainer.setAttribute('data-receipt-counter', receiptCounter);
	   }

	   function fetchConversionId() {
	           var productId = document.getElementById("product_id").value;
	           
	           if (productId) {
	               fetch('getConversionByProductId/' + productId)
	                   .then(response => response.json())
	                   .then(data => {
	                       if (data && data.conversionId) {
	                           document.getElementById("conversionId").value = data.conversionId;
	                       }
	                   })
	                   .catch(error => {
	                       console.error('Error fetching conversion:', error);
	                   });
	           }
	       }




