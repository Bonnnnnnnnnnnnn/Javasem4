$(document).ready(function() {
    let detailCount = 1;

    $('#addDetailButton').click(function() {
        detailCount++;
        const detailHtml = `
		<div id="detailsContainer">
            <h3>Warehouse Receipt Details ${detailCount}</h3>
			<div class="detail-entry row">
	            <div class="form-group col-md-6">
	                <label for="quantity">Quantity</label>
	                <input type="number" name="quantity" class="form-control" placeholder="Enter Quantity" required>
	            </div>
	            <div class="form-group col-md-6">
	                <label for="wh_price">Warehouse Price</label>
	                <input type="number" name="wh_price" class="form-control" placeholder="Enter Warehouse Price" step="0.01" required>
	            </div>
			</div>
		</div>
        `;
        $('#detailsContainer').append(detailHtml);
    });
});
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
   function filterProducts() {
       let input = document.getElementById('searchProduct');
       let filter = input.value.toLowerCase();
       
       let select = document.getElementById('product_id');
       let options = select.getElementsByTagName('option');
       
       for (let i = 1; i < options.length; i++) { 
           let option = options[i];
           let txtValue = option.textContent || option.innerText;
           if (txtValue.toLowerCase().indexOf(filter) > -1) {
               option.style.display = ""; 
           } else {
               option.style.display = "none"; 
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


