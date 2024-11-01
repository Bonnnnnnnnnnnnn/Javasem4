function loadPage(pageNumber) {
    $.ajax({
        type: "GET",
        url: "/businessManager/warehouseReleasenotes",
        data: {
            cp: pageNumber
        },
        success: function(response) {
            $('#pageContent').html(response); 

            updatePagination(response.pv);
        },
        error: function(e) {
            console.log("Error: ", e);
        }
    });
}

function goToDetail(id) {
      window.location.href = '/businessManager/updateOrderDetail?id=' + id;
  }

function confirmDelete() {
   return confirm("Are you sure you want to delete this OrderRequest?");
  }
