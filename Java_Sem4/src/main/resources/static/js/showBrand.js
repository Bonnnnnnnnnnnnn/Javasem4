//delete brand
function confirmDeleteBr() {
	return confirm("Are you sure you want to delete this Brand ?");
}
$(document).ready(function() {
    const newBrandRow = $(".highlighted-row");
    if (newBrandRow.length) {
        $('html, body').animate({
            scrollTop: newBrandRow.offset().top - 100
        }, 1000);
    }
});
