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
