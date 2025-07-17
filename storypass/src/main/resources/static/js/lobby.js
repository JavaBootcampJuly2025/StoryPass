const token = localStorage.getItem('token');

// Redirect to login if token missing
if (!token) {
  window.location.href = '/login.html';
}

async function fetchRooms() {
  try {
    const res = await fetch('/api/rooms', {
      headers: {
        'Authorization': `Bearer ${token}`
      }
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
    const list = document.getElementById('rooms');
    list.innerHTML = '';
    rooms.forEach(room => {
      const li = document.createElement('li');
      li.textContent = room.name;
      list.appendChild(li);
    });
  } catch (error) {
    console.error('Error fetching rooms:', error);
    alert('An error occurred. Please try again later.');
  }
}

function createRoom() {
  alert('Create Room clicked');
}

function joinRoom() {
  alert('Join Room clicked');
}

function logout() {
  localStorage.removeItem('token');
  window.location.href = '/login.html';
}


fetchRooms();
