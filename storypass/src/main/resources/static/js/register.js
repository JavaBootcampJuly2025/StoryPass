document.getElementById('registerForm').addEventListener('submit', async function (e) {
  e.preventDefault();

  const login = document.getElementById('login').value;
  const nickname = document.getElementById('nickname').value;
  const password = document.getElementById('password').value;
  const confirm = document.getElementById('confirm').value;

  if (password !== confirm) {
    alert('Passwords do not match');
    return;
  }

  const res = await fetch('/api/auth/register', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ login, nickname, password })
  });

  if (res.ok) {
    const data = await res.json();
    localStorage.setItem('token', data.token);
    window.location.href = '/lobby';
  } else {
    alert('Registration failed');
  }
});
