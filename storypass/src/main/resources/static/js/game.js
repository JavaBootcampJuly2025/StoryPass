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

async function fetchCurrentUser() {
    try {
        const res = await fetch('/api/me', {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) throw new Error('Failed to fetch user info');
        const data = await res.json();
        currentUserNickname = data.nickname;
        const greetingLink = document.getElementById('greeting-link');
        if (greetingLink) {
            greetingLink.textContent = `Hello, ${currentUserNickname}`;
            greetingLink.href = `/profile/`;
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
    document.getElementById('view-story-btn').addEventListener('click', () => {
        console.log('Button "View Full Story" clicked!');
        fetchAndShowStory(roomId);
    });

    document.getElementById('export-pdf-btn').addEventListener('click', () => exportStoryAsPdf(storyId));


    const storyModal = document.getElementById('story-modal');
    storyModal.querySelector('.modal-close-btn').addEventListener('click', () => storyModal.style.display = 'none');
    storyModal.addEventListener('click', (event) => {
        if (event.target === storyModal) {
            storyModal.style.display = 'none';
        }
    });
});
let storyId = null;
async function fetchGameState() {
    try {
        const res = await fetch(`/api/rooms/${roomId}/state`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) return;
        const state = await res.json();
        storyId = state.storyId;
        updateGameStateUI(state);
        updatePlayersList(state.players, state.maxPlayers, state.currentPlayerCount || []);
    } catch (e) {
        console.error(e);
    }
}

function updatePlayersList(players, maxplayers, currentPlayerCount) {
    const playersUl = document.getElementById('players');

    const playercount = document.getElementById('player-count');

    playercount.textContent= currentPlayerCount + "/" +  maxplayers ;
    if (!playersUl) return;
    playersUl.innerHTML = '';
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

    const gameInfoBlock = document.getElementById('game-info-block');
    const finishedBlock = document.getElementById('finished-block');
    const turnSection = document.getElementById('turn-section');
    const viewStoryBtn = document.getElementById('view-story-btn');
    const leaveRoomBtn = document.getElementById('leave-room-btn');

    gameInfoBlock.style.display = 'none';
    finishedBlock.style.display = 'none';
    turnSection.style.display = 'none';
    viewStoryBtn.style.display = 'none';
    leaveRoomBtn.style.display = 'inline-block';
    if (countdownInterval) clearInterval(countdownInterval);

    if (state.status === 'FINISHED') {
        finishedBlock.style.display = 'block';
        viewStoryBtn.style.display = 'inline-block';
        removeStartGameButton();
    } else if (state.status === 'IN_PROGRESS') {
        removeStartGameButton();
        gameInfoBlock.style.display = 'block';

        const isMyTurn = currentUserNickname === state.currentPlayerNickname;

        document.getElementById('lastlinecap').style.display = 'block';
        document.getElementById('current-player-wrapper').style.display = 'block';
        document.getElementById('time-left-wrapper').style.display = 'block';
        document.getElementById('lastlinecap').textContent = isMyTurn ? 'Last line:' : 'is typing..';
        document.getElementById('last-line').style.visibility = isMyTurn ? 'visible' : 'hidden';

        turnSection.style.display = isMyTurn ? 'block' : 'none';

        document.getElementById('game-status').textContent = state.status;
        document.getElementById('last-line').textContent = state.lastLine || '(No lines yet)';
        document.getElementById('current-player').textContent = state.currentPlayerNickname || 'N/A';
        if (!isMyTurn) document.getElementById('turn-text').value = '';


        let timeLeft = state.timeLeftSeconds ?? 0;
        document.getElementById('time-left').textContent = timeLeft;
        countdownInterval = setInterval(() => {
            if (timeLeft > 0) {
                timeLeft--;
                document.getElementById('time-left').textContent = timeLeft;
            } else {
                clearInterval(countdownInterval);
                if (isMyTurn) {
                    const text = document.getElementById('turn-text').value.trim();
                    submitTurnAuto(text);
                }
            }
        }, 1000);


    } else if (state.status === 'WAITING_FOR_PLAYERS') {
        gameInfoBlock.style.display = 'block';
        document.getElementById('lastlinecap').style.display = 'none';
        document.getElementById('current-player-wrapper').style.display = 'none';
        document.getElementById('time-left-wrapper').style.display = 'none';

        document.getElementById('game-status').textContent = state.status;


        const isOwner = currentUserNickname === state.ownerNickname;
        if (isOwner) {
            showStartGameButton(state.players, state.maxPlayers);
        } else {
            removeStartGameButton();
        }
    }
}

async function submitTurn() {
    const text = document.getElementById('turn-text').value.trim();
    if (!text) {
        alert('Please enter some text.');
        return;
    }
    try {
        await fetch(`/api/rooms/${roomId}/turn`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            body: JSON.stringify({ text })
        });
        document.getElementById('turn-text').value = '';
    } catch (e) {
        console.error(e);
        alert('Error submitting turn.');
    }
}

async function submitTurnAuto(text) {
    try {
        await fetch(`/api/rooms/${roomId}/turn`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}`, 'Content-Type': 'application/json' },
            body: JSON.stringify({ text })
        });
        document.getElementById('turn-text').value = '';
    } catch (e) {
        console.error('Error auto-submitting turn:', e);
    }
}

async function leaveRoom() {
    if (!confirm('Are you sure you want to leave the room?')) return;
    try {
        await fetch(`/api/rooms/${roomId}/leave`, {
            method: 'POST',
            headers: { 'Authorization': `Bearer ${token}` }
        });
        window.location.href = '/lobby';
    } catch (e) {
        console.error(e);
        alert('Error leaving room.');
    }
}

function showStartGameButton(players, maxPlayers) {
    const btnExists = document.getElementById('start-game-btn');
    if (btnExists) btnExists.remove();
    if (players.length < 2) return;

    const btn = document.createElement('button');
    btn.textContent = 'Start Game';
    btn.id = 'start-game-btn';
    btn.onclick = () => startGame(roomId);
    document.getElementById('action-buttons').appendChild(btn);
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
        }
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
            updatePlayersList(state.players || [], state.maxPlayers, state.currentPlayerCount || 0);
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

async function fetchAndShowStory(roomId) {
    try {
        const res = await fetch(`/api/rooms/${roomId}/full-story`, {
            headers: { 'Authorization': `Bearer ${token}` }
        });
        if (!res.ok) {
            throw new Error('Failed to fetch the story. It might not be finished yet.');
        }
        const storyData = await res.json();
        console.log('Response from server', storyData);
        displayStory(storyData);
    } catch (e) {
        console.error(e);
        alert(e.message);
    }
}


function displayStory(storyData) {
    const storyModal = document.getElementById('story-modal');
    const titleElem = document.getElementById('story-title');
    const participantsElem = document.getElementById('story-participants'); // Находим новый элемент
    const contentElem = document.getElementById('story-text-content');

    titleElem.textContent = storyData.title || 'Our Story';

    participantsElem.innerHTML = '';
    contentElem.innerHTML = '';

    if (storyData && Array.isArray(storyData.participants) && storyData.participants.length > 0) {
        participantsElem.textContent = `Participants: ${storyData.participants.join(', ')}`;
    }
    if (storyData && Array.isArray(storyData.lines)) {
        storyData.lines.forEach(line => {
            const p = document.createElement('p');
            p.innerHTML = `${line.text} — <strong>${line.authorNickname}</strong>`;
            contentElem.appendChild(p);
        });
    }
    storyModal.style.display = 'flex';
}

// document.getElementById('generatetitle').addEventListener('click', async () => {
//     try {
//
//         const roomRes = await fetch(`/api/rooms/${roomId}`, {
//             headers: { 'Authorization': `Bearer ${token}` }
//         });
//         if (!roomRes.ok) throw new Error('Failed to fetch room data');
//         const roomData = await roomRes.json();
//
//         const storyId = roomData.storyId;
//         if (!storyId) throw new Error('No story associated with this room');
//
//
//         document.getElementById('story-title').textContent = "Generating...";
//         const res = await fetch(`/api/stories/${storyId}/generate-title`, {
//             headers: { 'Authorization': `Bearer ${token}` }
//         });
//         if (!res.ok) throw new Error('Failed to generate title');
//         const title = await res.text();
//         document.getElementById('story-title').textContent = title;
//
//     } catch (e) {
//         alert(e.message);
//     }
// });
//
//


function exportStoryAsPdf(storyId) {

    window.location.href = `/api/export/pdf?storyId=${storyId}`;
}



