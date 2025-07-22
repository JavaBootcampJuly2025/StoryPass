console.log("Main JS loaded");


document.addEventListener("DOMContentLoaded", () => {
    const forms = document.querySelectorAll("form");
    forms.forEach(form => {
        form.addEventListener("submit", e => {
            const password = form.querySelector("input[name='password']");
            const confirm = form.querySelector("input[name='confirm_password']");
            if (password && confirm && password.value !== confirm.value) {
                e.preventDefault();
                alert("Passwords do not match!");
            }
        });
    });
});
