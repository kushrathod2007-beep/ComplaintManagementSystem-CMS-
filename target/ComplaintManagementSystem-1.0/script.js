const loginForm = document.getElementById("loginForm");
const username = document.getElementById("username");
const password = document.getElementById("password");
const togglePassword = document.getElementById("togglePassword");
const toast = document.getElementById("toast");

// SHOW / HIDE PASS
togglePassword.addEventListener("click", function () {
    if (password.type === "password") {
        password.type = "text";
        togglePassword.textContent = "Hide";
    } else {
        password.type = "password";
        togglePassword.textContent = "Show";
    }
});

// FOCUS USERNAME
const focusUsername = document.getElementById("focusUsername");
focusUsername.addEventListener("click", function () {
    username.focus();
});

// LOGIN
loginForm.addEventListener("submit", function (event) {
    event.preventDefault();

    const userValue = username.value.trim();
    const passwordValue = password.value;

    if (userValue === "") {
        showToast("Please enter your username or email.");
        username.focus();
        return;
    }

    if (passwordValue === "") {
        showToast("Please enter your password.");
        password.focus();
        return;
    }

    // BACKEND CONNECTION
    const formData = new URLSearchParams();
    formData.append("username", userValue);
    formData.append("password", passwordValue);

    fetch("/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: formData
    })
        .then(res => res.json().catch(() => ({ success: false, message: "The server hit an error. Check that MariaDB is running." })))
        .then(data => {
            if (data.success) {
                showToast("Login Successful!");

                // Redirect to the correct dashboard based on the user's role
                setTimeout(() => {
                    if (data.role === 'Admin') {
                        window.location.href = "admin.html";
                    } else if (data.role === 'Staff') {
                        window.location.href = "staff.html";
                    } else {
                        window.location.href = "student.html";
                    }
                }, 1000); // Wait 1 second so the user can see the success toast!

            } else {
                showToast(data.message);
            }
        })
        .catch(err => showToast("Cannot reach the server. Is Tomcat running?"));
});

// TOAST
function showToast(message) {
    toast.textContent = message;
    toast.classList.add("show");

    setTimeout(function () {
        toast.classList.remove("show");
    }, 2800);
}