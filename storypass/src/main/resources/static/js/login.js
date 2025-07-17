document.getElementById('loginForm').addEventListener('submit', async function (e) {
  e.preventDefault();
  const login = document.getElementById('login').value;
  const password = document.getElementById('password').value;

  const res = await fetch('/api/auth/login', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ login, password })
  });

  if (res.ok) {
    const data = await res.json();
    localStorage.setItem('token', data.token);
    window.location.href = '/lobby';
  } else {
    alert('Login failed');
  }
});
