var moveRowUp = function (event) {
    event.preventDefault();
    event.stopPropagation();
    var thisRow = $(this).parent().parent();
    var prevRow = $(thisRow).prev();
    if (prevRow.length) {
        swapRows(prevRow, thisRow);
    }
};

var moveRowDown = function (event) {
    event.preventDefault();
    event.stopPropagation();
    var thisRow = $(this).parent().parent();
    var nextRow = $(thisRow).next();
    if (nextRow.length) {
        swapRows(nextRow, thisRow);
    }
};

var deleteRow = function (event) {
    event.preventDefault();
    event.stopPropagation();
    var rowIdx = parseInt($(this).parent().parent().attr("id").substring(11));
    var nbrOfRows = $("#tbodyPatternEdit TR").length;
    for (var i = rowIdx; i < nbrOfRows - 1; i++) {
        copyField(i, "id");
        copyField(i, "version");
        copyField(i, "regex");
        copyField(i, "catId");
    }
    $("#patternRows" + (nbrOfRows - 1)).remove();
};

var copyField = function (idx, suffix) {
    var elementId1 = "#patternRows" + idx + "\\." + suffix;
    var elementId2 = "#patternRows" + (idx + 1) + "\\." + suffix;
    $(elementId1).val($(elementId2).val());
};

var swapRows = function (row1, row2) {
    var rowId1 = $(row1).attr("id");
    var rowId2 = $(row2).attr("id");
    swapFields(rowId1, rowId2, "id");
    swapFields(rowId1, rowId2, "version");
    swapFields(rowId1, rowId2, "regex");
    swapFields(rowId1, rowId2, "catId");
};

var swapFields = function (rowId1, rowId2, suffix) {
    var elementId1 = "#" + rowId1 + "\\." + suffix;
    var elementId2 = "#" + rowId2 + "\\." + suffix;
    var swapValue = $(elementId1).val();
    $(elementId1).val($(elementId2).val());
    $(elementId2).val(swapValue);
};

var addPatternEdit = function () {
    var nbrOfRows = $("#tbodyPatternEdit TR").length;
    // @formatter:off
    $("#tbodyPatternEdit").append(
        "<tr id=\"patternRows" + nbrOfRows + "\">" +
            "<td>" +
                "<input type=\"hidden\" id=\"patternRows" + nbrOfRows + ".id\" name=\"patternRows[" + nbrOfRows + "].id\" value=\"\">" +
                "<input type=\"hidden\" id=\"patternRows" + nbrOfRows + ".version\" name=\"patternRows[" + nbrOfRows + "].version\" value=\"0\">" +
                "<a class=\"moveRowUp\" href=\"#\"><i class=\"fas fa-arrow-up\" title=\"Move up\"></i></a> " +
                "<a class=\"moveRowDown\" href=\"#\"><i class=\"fas fa-arrow-down\" title=\"Move down\"></i></a> " +
                "<a class=\"deleteRow\" href=\"#\"><i class=\"fas fa-ban\" title=\"Delete\"></i></a>" +
            "</td>" +
            "<td>" +
                "<input type=\"text\" class=\"form-control\" id=\"patternRows" + nbrOfRows + ".regex\" name=\"patternRows[" + nbrOfRows + "].regex\" value=\"\">" +
            "</td>" +
            "<td>" +
                "<select class=\"form-control\" id=\"patternRows" + nbrOfRows + ".catId\" name=\"patternRows[" + nbrOfRows + "].catId\">" +
                    "<option value=\"\">select</option>" +
                    $("#copyCatDropDown").html() +
                "</select>" +
            "</td>" +
        "</tr>");
    // @formatter:on
    $(".moveRowUp").on("click", moveRowUp);
    $(".moveRowDown").on("click", moveRowDown);
    $(".deleteRow").on("click", deleteRow);
};

$(document).ready(function () {
    $(".moveRowUp").on("click", moveRowUp);
    $(".moveRowDown").on("click", moveRowDown);
    $(".deleteRow").on("click", deleteRow);
    $("#addPatternEdit").on("click", addPatternEdit);
});
