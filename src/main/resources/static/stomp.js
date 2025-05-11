const stompClient = new StompJs.Client({
  brokerURL: 'ws://localhost:8080/stomp/chats'
});

stompClient.onConnect = (frame) => {
  setConnected(true);
  showChatrooms(); // 사용자가 참여자한 목록
  console.log('Connected: ' + frame);
};

stompClient.onWebSocketError = (error) => {
  console.error('Error with websocket', error);
};

stompClient.onStompError = (frame) => {
  console.error('Broker reported error: ' + frame.headers['message']);
  console.error('Additional details: ' + frame.body);
};

function setConnected(connected) {
  $("#connect").prop("disabled", connected);
  $("#disconnect").prop("disabled", !connected);
  $("#create").prop("disabled", !connected);
}

function connect() {
  stompClient.activate();
}

function disconnect() {
  stompClient.deactivate();
  setConnected(false);
  console.log("Disconnected");
}

function sendMessage() {
  let chatroomId = $("#chatroom-id").val();

  stompClient.publish({
    destination: "/pub/chats/" + chatroomId,
    body: JSON.stringify(
        {'message': $("#message").val()})
  });
  $("#message").val("")
}


function createChatroom() { // 서버에서 호출
  $.ajax({
    type: 'POST',
    dataType: 'json', //서버에서 반환하는 데이터를 JSON 형태로 받겠다는 의미
    //클라이언트(프론트)에서 서버에 데이터를 넘길 때는 contentType: 'application/json'을 지정해야 JSON 형식으로 전송
    url: '/chats?title=' + $("#chatroom-title").val(),
    success: function (data) {
      console.log('data: ', data);
      showChatrooms();
      enterChatroom(data.id, true);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

function showChatrooms() { // 채팅방 목록을 서버에서 받아온다
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/chats',
    success: function (data) {
      console.log('data: ', data);
      renderChatrooms(data);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

// 채팅방 목록
function renderChatrooms(chatrooms) { // 서버에서 받아온데이터를 활용
  $("#chatroom-list").html(""); // 기존에 가지고 있던 채팅방 모록을 초기화
  for (let i = 0; i < chatrooms.length; i++) {
    $("#chatroom-list").append(
        "<tr onclick='joinChatroom(" + chatrooms[i].id + ")'><td>"
        + chatrooms[i].id + "</td><td>" + chatrooms[i].title + "</td><td>"
        + chatrooms[i].memberCount + "</td><td>" + chatrooms[i].createdAt
        + "</td></tr>"
    );
  }
}

let subscription;

// 처음 입장한 사람에게 입장 안내문을 작성
function enterChatroom(chatroomId, newMember) {
  $("#chatroom-id").val(chatroomId);
  $("#messages").html(""); //과거에 메시지를 지워준다
  showMessages(chatroomId); //과거 메시지를 받아온다
  $("#conversation").show();
  $("#send").prop("disabled", false); // 버튼 활성화
  $("#leave").prop("disabled", false); // 버튼 활성화

 //  기존에 가지고 있던 채팅방이 존재한다면
  if (subscription != undefined) {
    subscription.unsubscribe(); // 구독을 취소
  }

 // 새로운 방에 구독
  subscription = stompClient.subscribe('/sub/chats/' + chatroomId,
      (chatMessage) => {
        showMessage(JSON.parse(chatMessage.body));
      });

 // 이방에 처음 들어오는 맴버면 동작
  if (newMember) {
    stompClient.publish({
      destination: "/pub/chats/" + chatroomId,
      body: JSON.stringify(
          {'message': "님이 방에 들어왔습니다."})
    })
  }
}

function showMessages(chatroomId) { // 메시지 내역 가지고오기
  $.ajax({
    type: 'GET',
    dataType: 'json',
    url: '/chats/' + chatroomId + '/messages',
    success: function (data) {
      console.log('data: ', data);
      for (let i = 0; i < data.length; i++) {
        showMessage(data[i]);
      }
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

function showMessage(chatMessage) {
  $("#messages").append(
      "<tr><td>" + chatMessage.sender + " : " + chatMessage.message
      + "</td></tr>");
}

function joinChatroom(chatroomId) {
  $.ajax({
    type: 'POST',
    dataType: 'json',
    url: '/chats/' + chatroomId,
    success: function (data) {
      console.log('data: ', data);
      enterChatroom(chatroomId, data);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

// 현재 참여중인 방에서 나가기
function leaveChatroom() {
  let chatroomId = $("#chatroom-id").val(); // 현재 참여중인 방 아이디값가지고 오기
  $.ajax({ // 서버에 호출
    type: 'DELETE',
    dataType: 'json',
    url: '/chats/' + chatroomId,
    success: function (data) {
      console.log('data: ', data);
      showChatrooms(); // 채팅방 목록 갱신
      exitChatroom(chatroomId);
    },
    error: function (request, status, error) {
      console.log('request: ', request);
      console.log('error: ', error);
    },
  })
}

function exitChatroom(chatroomId) {
  $("#chatroom-id").val(""); // 현재 들어가 있는 방이없기 떄문에 초기화
  $("#conversation").hide();
  $("#send").prop("disabled", true);
  $("#leave").prop("disabled", true);
}

$(function () {
  $("form").on('submit', (e) => e.preventDefault());
  $("#connect").click(() => connect());
  $("#disconnect").click(() => disconnect());
  $("#create").click(() => createChatroom());
  $("#leave").click(() => leaveChatroom());
  $("#send").click(() => sendMessage());
});