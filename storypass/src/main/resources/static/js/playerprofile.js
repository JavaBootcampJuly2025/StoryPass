const token = localStorage.getItem('token');

if (!token) {
    window.location.href = '/login.html';
}

async function fetchProfile() {
    try {
        const res = await fetch('/api/profile', {
            headers: { 'Authorization': `Bearer ${token}` }
        });

        if (!res.ok) {
            alert('Failed to load profile.');
            return;
        }

        const profile = await res.json();

        document.getElementById('welcome-message').textContent = `Welcome, ${profile.nickname}`;

        const list = document.getElementById('story-list');
        list.innerHTML = '';

        if (!profile.stories || profile.stories.length === 0) {
            list.innerHTML = '<li>You have not participated in any stories yet.</li>';
        } else {
            profile.stories.forEach(story => {
                const li = document.createElement('li');

                const strong = document.createElement('strong');
                strong.textContent = story.title;

                const button = document.createElement('button');
                button.textContent = 'Download PDF';
                button.style.marginLeft = '10px';
                button.addEventListener('click', () => downloadPdf(story.storyId));

                li.appendChild(strong);
                li.appendChild(button);

                list.appendChild(li);
            });

        }

    } catch (error) {
        console.error('Error loading profile:', error);
    }
}

function logout() {
    localStorage.removeItem('token');
    window.location.href = '/login.html';
}

function downloadPdf(storyId) {
    window.location.href = `/api/export/pdf?storyId=${storyId}`;
}


document.addEventListener('DOMContentLoaded', () => {
    fetchProfile();
});
