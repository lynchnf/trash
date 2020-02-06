var filterPatternList = function () {
    var listFormId = $(this).attr("data-list-form");
    console.log("changePageSize: resetting pageNumber to 0.");
    $("#" + listFormId + " .pageNumber").val(0);

    var oldRegex = $("#whereRegex").val();
    var newRegex = $("#newWhereRegex").val();
    console.log("filterList: changing whereRegex from " + oldRegex + " to " + newRegex + ".");
    $("#whereRegex").val(newRegex);

    $("#" + listFormId).submit();
};

$(document).ready(function () {
    $("#filterPatternList").on("click", filterPatternList);
});
