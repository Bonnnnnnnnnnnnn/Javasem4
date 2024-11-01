$(document).ready(function() {
    let detailCount = 1;

    $('#addDetailButton').click(function() {
        detailCount++;
        const detailHtml = `
            <h3>Warehouse Receipt Details ${detailCount}</h3>
            <div class="form-group">
                <label for="quantity">Quantity</label>
                <input type="number" name="quantity" class="form-control" placeholder="Enter Quantity" required>
            </div>
            <div class="form-group">
                <label for="wh_price">Warehouse Price</label>
                <input type="number" name="wh_price" class="form-control" placeholder="Enter Warehouse Price" step="0.01" required>
            </div>
        `;
        $('#detailsContainer').append(detailHtml);
    });
});
