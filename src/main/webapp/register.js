const registerForm =
    document.getElementById("registerForm");

const fullName =
    document.getElementById("fullName");

const email =
    document.getElementById("email");

const password =
    document.getElementById("password");

const confirmPassword =
    document.getElementById("confirmPassword");

const showPassword =
    document.getElementById("showPassword");

const terms =
    document.getElementById("terms");

const toast =
    document.getElementById("toast");

const roles =
    document.querySelectorAll(".role");

const strengthText =
    document.getElementById("strengthText");

const strengthFill =
    document.getElementById("strengthFill");

const passwordMatch =
    document.getElementById("passwordMatch");

const lengthCheck =
    document.getElementById("lengthCheck");

const upperCheck =
    document.getElementById("upperCheck");

const numberCheck =
    document.getElementById("numberCheck");

const specialCheck =
    document.getElementById("specialCheck");


let selectedRole = "Student";


// SHOW / HIDE PASSWORD

showPassword.addEventListener("click", function () {

    if (password.type === "password") {

        password.type = "text";

        showPassword.textContent = "Hide";

    } else {

        password.type = "password";

        showPassword.textContent = "Show";

    }

});


// ROLE SELECTION

roles.forEach(function (role) {

    role.addEventListener("click", function () {

        roles.forEach(function (item) {

            item.classList.remove("active");

        });

        role.classList.add("active");

        selectedRole =
            role.getAttribute("data-role");

    });

});


// PASSWORD STRENGTH

password.addEventListener("input", function () {

    const value = password.value;

    let strength = 0;

    let text = "—";


    const hasLength =
        value.length >= 8;

    const hasUppercase =
        /[A-Z]/.test(value);

    const hasNumber =
        /[0-9]/.test(value);

    const hasSpecial =
        /[^A-Za-z0-9]/.test(value);


    // REQUIREMENTS

    updateRequirement(
        lengthCheck,
        hasLength
    );

    updateRequirement(
        upperCheck,
        hasUppercase
    );

    updateRequirement(
        numberCheck,
        hasNumber
    );

    updateRequirement(
        specialCheck,
        hasSpecial
    );


    // STRENGTH SCORE

    if (value.length === 0) {

        strength = 0;

        text = "—";

    } else {

        // Length score

        if (value.length >= 4) {

            strength += 15;

        }

        if (value.length >= 6) {

            strength += 15;

        }

        if (value.length >= 8) {

            strength += 20;

        }

        // Uppercase

        if (hasUppercase) {

            strength += 15;

        }

        // Number

        if (hasNumber) {

            strength += 15;

        }

        // Special character

        if (hasSpecial) {

            strength += 20;

        }


        if (strength <= 25) {

            text = "Weak";

        } else if (strength <= 50) {

            text = "Medium";

        } else if (strength < 100) {

            text = "Good";

        } else {

            strength = 100;

            text = "Strong";

        }

    }


    strengthFill.style.width =
        strength + "%";

    strengthText.textContent =
        text;


    // BAR STATE

    if (strength <= 25) {

        strengthFill.style.background =
            "#d45b68";

        strengthText.style.color =
            "#d45b68";

    } else if (strength <= 50) {

        strengthFill.style.background =
            "#d99a45";

        strengthText.style.color =
            "#d99a45";

    } else if (strength < 100) {

        strengthFill.style.background =
            "#3867e8";

        strengthText.style.color =
            "#3867e8";

    } else {

        strengthFill.style.background =
            "#3fbe72";

        strengthText.style.color =
            "#3fbe72";

    }

});


// REQUIREMENT FUNCTION

function updateRequirement(element, valid) {

    if (valid) {

        element.classList.add("valid");

        element.textContent =
            "✓ " +
            element.textContent.substring(2);

    } else {

        element.classList.remove("valid");

        element.textContent =
            "○ " +
            element.textContent.substring(2);

    }

}


// PASSWORD MATCH

confirmPassword.addEventListener(
    "input",
    checkPasswordMatch
);


password.addEventListener(
    "input",
    checkPasswordMatch
);


function checkPasswordMatch() {

    const pass =
        password.value;

    const confirm =
        confirmPassword.value;


    if (confirm === "") {

        passwordMatch.textContent = "";

        passwordMatch.className =
            "password-match";

        return;
    }


    if (pass === confirm) {

        passwordMatch.textContent =
            "✓ Passwords match";

        passwordMatch.className =
            "password-match match";

    } else {

        passwordMatch.textContent =
            "✕ Passwords do not match";

        passwordMatch.className =
            "password-match no-match";

    }

}


// FORM SUBMIT

registerForm.addEventListener(
    "submit",
    function (event) {

        event.preventDefault();


        const nameValue =
            fullName.value.trim();

        const emailValue =
            email.value.trim();

        const passwordValue =
            password.value;

        const confirmValue =
            confirmPassword.value;


        // FULL NAME

        if (nameValue === "") {

            showToast(
                "Please enter your full name."
            );

            fullName.focus();

            return;
        }


        // EMAIL

        if (emailValue === "") {

            showToast(
                "Please enter your email."
            );

            email.focus();

            return;
        }


        // PASSWORD

        if (passwordValue === "") {

            showToast(
                "Please create a password."
            );

            password.focus();

            return;
        }


        // PASSWORD LENGTH

        if (passwordValue.length < 8) {

            showToast(
                "Password must be at least 8 characters."
            );

            password.focus();

            return;
        }


        // CONFIRM PASSWORD

        if (confirmValue === "") {

            showToast(
                "Please confirm your password."
            );

            confirmPassword.focus();

            return;
        }


        // PASSWORD MATCH

        if (passwordValue !== confirmValue) {

            showToast(
                "Passwords do not match."
            );

            confirmPassword.focus();

            return;
        }


        // TERMS

        if (!terms.checked) {

            showToast(
                "Please accept the Terms & Conditions."
            );

            return;
        }


        // SUCCESS

        const formData = new URLSearchParams();
        formData.append("fullName", nameValue);
        formData.append("email", emailValue);
        formData.append("password", passwordValue);
        formData.append("role", selectedRole);

        fetch("/api/register", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: formData
        })
        .then(res => res.json())
        .then(data => {
            if (data.success) {
                showToast("Account created successfully! Redirecting...");
                setTimeout(() => window.location.href = "index.html", 1500);
            } else {
                showToast(data.message);
            }
        })
        .catch(err => showToast("Server connection error. Is Tomcat running?"));

    }
);


// TOAST

function showToast(message) {

    toast.textContent =
        message;

    toast.classList.add("show");


    setTimeout(function () {

        toast.classList.remove("show");

    }, 2800);

}