var socket = new WebSocket("ws://localhost:8080/websocket-1.0-SNAPSHOT/echo");


socket.onmessage = function(event)
{
    var received_msg = event.data;
    document.getElementById('messageForm:outputOne').value = received_msg;
};

socket.sendMyMessage = function () {
    var message = document.getElementById("messageForm:messageOne").value;
    document.getElementById("messageForm:outputOne").value=message;
};
function init() {
    var elementOne = document.getElementById("messageForm:channelOne");
    elementOne.value = "echo";
    elementOne.disabled = true;
    elementOne.style.background = "#dddddd";
};
document.addEventListener('DOMContentLoaded', init, false);
