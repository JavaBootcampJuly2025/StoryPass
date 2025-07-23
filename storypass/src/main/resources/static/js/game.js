const token = localStorage.getItem('token');
if (!token) {
    window.location.href = '/login.html';
}

const urlParams = new URLSearchParams(window.location.search);
const roomId = urlParams.get('roomId');
if (!roomId) {
    alert('No room ID specified.');
    window.location.href = '/lobby';
}

let currentUserNickname = null;
let stompClient = null;
let countdownInterval = null;
let timeLeft = 0;
let hasAutoSubmitted = false;

async function fetchCurrentUser() {
    try {
        const res = await fetch('/api/me', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) throw new Error('Failed to fetch user info');

        const data = await res.json();
        currentUserNickname = data.nickname;

        // Update greeting link if present
        const greetingLink = document.getElementById('greeting-link');
        if (greetingLink) {
            greetingLink.textContent = `Hello, ${currentUserNickname}`;
            greetingLink.href = `/profile/${currentUserNickname}`;
        }
    } catch (e) {
        console.error(e);
        alert('Error fetching user info. Please login again.');
        localStorage.removeItem('token');
        window.location.href = '/login.html';
    }
}

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('submit-turn-btn').addEventListener('click', submitTurn);
});

async function fetchGameState() {
    try {
        const res = await fetch(`/api/rooms/${roomId}/state`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) return;

        const state = await res.json();
        updateGameStateUI(state);

        // Update players list
        updatePlayersList(state.players || []);
    } catch (e) {
        console.error(e);
    }
}

function updatePlayersList(players) {
    const playersUl = document.getElementById('players');
    if (!playersUl) return;

    playersUl.innerHTML = ''; // Clear existing players

    if (players.length === 0) {
        playersUl.innerHTML = '<li>No players in room</li>';
        return;
    }

    players.forEach(player => {
        const li = document.createElement('li');
        li.textContent = player.nickname;

        if (player.nickname === currentUserNickname) {
            li.style.fontWeight = '700';
            li.style.color = '#4caf50';
            li.textContent += ' (You)';
        }

        playersUl.appendChild(li);
    });
}

function updateGameStateUI(state) {
    const lastLineElem = document.getElementById('last-line');
    const isMyTurn = currentUserNickname === state.currentPlayerNickname;
    const lastlinecap = document.getElementById('lastlinecap');


    if (isMyTurn) {
        lastLineElem.textContent = state.lastLine || '(No lines yet)';
        lastLineElem.style.visibility = "visible";
        lastlinecap.style.visibility = "visible";
    } else {
        lastLineElem.style.visibility = "hidden";
        lastlinecap.style.visibility = "hidden";
    }

    const currentPlayerWrapper = document.getElementById('current-player-wrapper');
    const timeLeftWrapper = document.getElementById('time-left-wrapper');

    const gameInProgress = state.status === 'IN_PROGRESS';

    if (gameInProgress) {
        document.getElementById('current-player').textContent = state.currentPlayerNickname || 'N/A';
    }

    if (countdownInterval) {
        clearInterval(countdownInterval);
    }

    timeLeft = state.timeLeftSeconds ?? 0;

    if (gameInProgress) {
        document.getElementById('time-left').textContent = timeLeft;

        currentPlayerWrapper.style.display = 'block';
        timeLeftWrapper.style.display = 'block';
    } else {
        currentPlayerWrapper.style.display = 'none';
        timeLeftWrapper.style.display = 'none';
    }

    countdownInterval = setInterval(() => {
        if (timeLeft > 0) {
            timeLeft--;
            if (gameInProgress) {
                document.getElementById('time-left').textContent = timeLeft;
            }
            hasAutoSubmitted = false;
        } else {
            clearInterval(countdownInterval);
            if (!hasAutoSubmitted && isMyTurn && state.status === 'IN_PROGRESS') {
                hasAutoSubmitted = true;
                let text = document.getElementById('turn-text').value.trim();
                if (!text) text = '(No input provided)';
                submitTurnAuto(text);
            }
        }
    }, 1000);

    document.getElementById('game-status').textContent = state.status || 'UNKNOWN';

    const isOwner = currentUserNickname === state.ownerNickname;

    document.getElementById('turn-section').style.display = (isMyTurn && gameInProgress) ? 'block' : 'none';

    if (!isMyTurn) {
        document.getElementById('turn-text').value = '';
    }

    if (state.status === 'WAITING_FOR_PLAYERS' && isOwner) {
        showStartGameButton(state.players, state.maxPlayers);
    } else {
        removeStartGameButton();
    }
}

async function submitTurn() {
    const text = document.getElementById('turn-text').value.trim();
    if (!text) {
        alert('Please enter some text.');
        return;
    }

    try {
        const res = await fetch(`/api/rooms/${roomId}/turn`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ text })
        });

        if (!res.ok) {
            const err = await res.json();
            alert('Failed to submit turn: ' + (err.message || res.statusText));
            return;
        }

        document.getElementById('turn-text').value = '';
    } catch (e) {
        console.error(e);
        alert('Error submitting turn.');
    }
}

async function submitTurnAuto(text) {
    try {
        const res = await fetch(`/api/rooms/${roomId}/turn`, {
            method: 'POST',
            headers: {
                'Authorization': `Bearer ${token}`,
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ text })
        });

        if (!res.ok) {
            const err = await res.json();
            console.warn('Auto-submit failed:', err.message || res.statusText);
            return;
        }

        document.getElementById('turn-text').value = '';
    } catch (e) {
        console.error('Error auto-submitting turn:', e);
    }
}

async function leaveRoom() {
    if (!confirm('Are you sure you want to leave the room?')) return;

    try {
        const res = await fetch(`/api/rooms/${roomId}/leave`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) {
            alert('Failed to leave the room.');
            return;
        }

        window.location.href = '/lobby';
    } catch (e) {
        console.error(e);
        alert('Error leaving room.');
    }
}

function showStartGameButton(players, maxPlayers) {
    const btnExists = document.getElementById('start-game-btn');
    if (btnExists || players.length < 2) return;

    const btn = document.createElement('button');
    btn.textContent = 'Start Game';
    btn.id = 'start-game-btn';
    btn.onclick = () => startGame(roomId);
    document.querySelector('.card').appendChild(btn);
}

function removeStartGameButton() {
    const btn = document.getElementById('start-game-btn');
    if (btn) btn.remove();
}

async function startGame(roomId) {
    try {
        const res = await fetch(`/api/rooms/${roomId}/start`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) {
            const err = await res.json();
            alert('Failed to start game: ' + (err.message || res.statusText));
            return;
        }

        alert('Game started!');
        removeStartGameButton();
    } catch (e) {
        alert('Error starting game.');
        console.error(e);
    }
}

function connectWebSocket() {
    const socket = new SockJS('/ws');
    stompClient = Stomp.over(socket);

    stompClient.connect({}, () => {
        console.log('✅ Connected to WebSocket');
        stompClient.subscribe(`/topic/room/${roomId}/state`, (message) => {
            const state = JSON.parse(message.body);
            updateGameStateUI(state);
            updatePlayersList(state.players || []);
        });
    }, (error) => {
        console.error('❌ WebSocket error:', error);
    });
}

(async function init() {
    await fetchCurrentUser();
    await fetchGameState();
    connectWebSocket();
})();
