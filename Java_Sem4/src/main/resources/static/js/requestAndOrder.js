
function goToDetail(id) {
    var row = document.querySelector('tr[data-id="'+id+'"]');
    var status = row.querySelector('td:nth-child(5)').innerText.trim();

    if (status === 'Shipping') {
        alert("The export ticket has been completed.");
        return; 
    }

    window.location.href = '/warehouseManager/requestInWarehouseDetail?id=' + id;
}