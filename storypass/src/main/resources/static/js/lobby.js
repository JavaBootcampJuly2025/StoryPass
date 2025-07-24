const token = localStorage.getItem('token');

if (!token) {
  window.location.href = '/login.html';
}


let stompClient = null;



async function fetchRooms() {
  try {
    const res = await fetch('/api/rooms', {
      headers: { 'Authorization': `Bearer ${token}` }
    });

    if (res.status === 401) {
      alert('Session expired or unauthorized. Please log in again.');
      localStorage.removeItem('token');
      window.location.href = '/login.html';
      return;
    }

    if (!res.ok) {
      alert('Failed to load rooms');
      return;
    }

    const rooms = await res.json();

    console.log('Rooms:', rooms);
    renderRoomList(rooms);


  } catch (error) {
    console.error('Error fetching rooms:', error);
    alert('An error occurred. Please try again later.');
  }
}



function renderRoomList(rooms) {
  const list = document.getElementById('rooms');
  list.innerHTML = '';

  rooms.forEach(room => {
    const li = document.createElement('li');
    li.innerHTML = `
      <strong>${room.title}</strong> - ${room.currentPlayerCount}/${room.maxPlayers} players 
      (${room.isPublic ? 'Public' : 'Private'}) - Owner: ${room.ownerNickname}
      <button onclick="joinRoomPrompt(${room.id}, ${room.isPublic})">Join</button>
    `;
    list.appendChild(li);
  });
}


function connectWebSocket() {
  const socket = new SockJS('/ws');
  stompClient = Stomp.over(socket);

  stompClient.connect({}, (frame) => {
    console.log('Connected to WebSocket:', frame);

    stompClient.subscribe('/topic/rooms', (message) => {
      const updatedRooms = JSON.parse(message.body);
      renderRoomList(updatedRooms);
    });
  }, (error) => {
    console.error('WebSocket error:', error);
  });
}



function handlePublicCheckboxChange() {
  const isPublic = document.getElementById('isPublic').checked;
  const roomCodeInput = document.getElementById('roomCode');
  roomCodeInput.disabled = isPublic;
}

document.addEventListener('DOMContentLoaded', () => {
  const isPublicCheckbox = document.getElementById('isPublic');


  handlePublicCheckboxChange();


  isPublicCheckbox.addEventListener('change', handlePublicCheckboxChange);
});


document.addEventListener('DOMContentLoaded', async () => {
  const greetingLink = document.getElementById('greeting-link');

  try {
    const res = await fetch('/api/me', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
    });

    if (res.ok) {
      const user = await res.json();
      greetingLink.textContent = `Hello, ${user.nickname}!`;
    } else {
      greetingLink.textContent = 'Hello!';
    }
  } catch (e) {
    console.error('Failed to fetch user data:', e);
    greetingLink.textContent = 'Hello!';
  }
});

document.getElementById('generatetitle').addEventListener('click', async (e) => {
  e.preventDefault(); // prevent form submission

  const titleInput = document.getElementById('title');
  const inputText = titleInput.value.trim();

  if (!inputText) {
    alert('Please enter some text to generate a title');
    return;
  }

  try {
    const token = localStorage.getItem('token');
    const res = await fetch('/api/stories/generate-title', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ inputText })
    });

    if (!res.ok) {
      alert('Failed to generate title');
      return;
    }

    const generatedTitle = await res.text();
    titleInput.value = generatedTitle;

  } catch (error) {
    console.error('Error generating title:', error);
    alert('Error generating title');
  }
});



function toggleCreateRoomForm() {
  const form = document.getElementById('create-room-form');
  const togglecreateform = document.getElementById('togglecreateform');
  const roomlist = document.getElementById('room-list');

  const isFormVisible = form.style.display === 'block';

  if (isFormVisible) {
    form.style.display = 'none';
    togglecreateform.style.display = 'inline-block';
    roomlist.style.display = 'block';
  } else {
    form.style.display = 'block';
    togglecreateform.style.display = 'none';
    roomlist.style.display = 'none';
  }
}





document.getElementById('roomForm').addEventListener('submit', async (e) => {
  e.preventDefault();

  const title = document.getElementById('title').value.trim();
  const isPublic = document.getElementById('isPublic').checked;
  const roomCode = document.getElementById('roomCode').value.trim();
  const maxPlayers = parseInt(document.getElementById('maxPlayers').value, 10);
  const timeLimitPerTurnInSeconds = parseInt(document.getElementById('timeLimitPerTurnInSeconds').value, 10);
  const turnsPerPlayer = parseInt(document.getElementById('turnsPerPlayer').value, 10);

  if (!title) {
    alert('Please enter a title');
    return;
  }

  if (!isPublic && !roomCode) {
    alert('Room code is required for private rooms');
    return;
  }

  const body = {
    title,
    isPublic,
    roomCode: isPublic ? null : roomCode,
    maxPlayers,
    timeLimitPerTurnInSeconds,
    turnsPerPlayer
  };

  try {
    const res = await fetch('/api/rooms', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(body)
    });

    if (res.status === 401) {
      alert('Unauthorized. Please log in again.');
      localStorage.removeItem('token');
      window.location.href = '/login.html';
      return;
    }

    if (!res.ok) {
      const err = await res.json();
      alert('Failed to create room: ' + (err.message || res.statusText));
      return;
    }

    const data = await res.json();
    window.location.href = `/game?roomId=${data.id}`;

  } catch (e) {
    alert('Error creating room.');
    console.error(e);
  }
});


async function joinRoomPrompt(roomId, isPublic) {
  let roomCode = '';

  if (!isPublic) {
    roomCode = prompt('This is a private room. Please enter the room code:');
    if (!roomCode) return;
  }

  try {
    const res = await fetch(`/api/rooms/${roomId}/join`, {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify({ roomCode })
    });

    if (res.status === 401) {
      alert('Unauthorized. Please log in again.');
      localStorage.removeItem('token');
      window.location.href = '/login.html';
      return;
    }

    if (!res.ok) {
      const err = await res.json();
      alert('Failed to join room: ' + (err.message || res.statusText));
      return;
    }

    window.location.href = `/game?roomId=${roomId}`;

  } catch (e) {
    alert('Error joining room.');
    console.error(e);
  }
}


function logout() {
  localStorage.removeItem('token');
  window.location.href = '/login.html';
}

// Initial room fetch
fetchRooms();
connectWebSocket();
