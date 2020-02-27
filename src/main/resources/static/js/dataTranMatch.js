function allowDrop(event) {
    event.preventDefault();
}

function drag(event) {
    event.dataTransfer.setData("payloadId", event.target.id);
}

function drop(event) {
    event.preventDefault();
    var payloadId = event.dataTransfer.getData("payloadId");
    console.log("payloadId: ", payloadId);
    var targetId = event.target.id;
    console.log("targetId: ", targetId);
}
