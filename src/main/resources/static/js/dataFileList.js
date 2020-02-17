var filterDataFileList = function () {
    var listFormId = $(this).attr("data-list-form");
    console.log("changePageSize: resetting pageNumber to 0.");
    $("#" + listFormId + " .pageNumber").val(0);

    var oldOriginalFilename = $("#whereOriginalFilename").val();
    var newOriginalFilename = $("#newWhereOriginalFilename").val();
    console.log("filterList: changing whereOriginalFilename from " + oldOriginalFilename + " to " +
        newOriginalFilename + ".");
    $("#whereOriginalFilename").val(newOriginalFilename);

    var oldStatus = $("#whereStatus").val();
    var newStatus = $("#newWhereStatus").val();
    console.log("filterList: changing whereStatus from " + oldStatus + " to " + newStatus + ".");
    $("#whereStatus").val(newStatus);

    $("#" + listFormId).submit();
};

$(document).ready(function () {
    $("#filterDataFileList").on("click", filterDataFileList);
});
