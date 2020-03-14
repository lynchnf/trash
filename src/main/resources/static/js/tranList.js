var filterTranList = function () {
    var listFormId = $(this).attr("data-list-form");
    console.log("changePageSize: resetting pageNumber to 0.");
    $("#" + listFormId + " .pageNumber").val(0);

    var oldName = $("#whereName").val();
    var newName = $("#newWhereName").val();
    console.log("filterList: changing whereName from " + oldName + " to " + newName + ".");
    $("#whereName").val(newName);

    $("#" + listFormId).submit();
};

$(document).ready(function () {
    $("#filterTranList").on("click", filterTranList);
});
