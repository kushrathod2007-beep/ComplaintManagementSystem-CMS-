// Shared dark-mode switch for the login and register pages.
// Uses the same storage key as the dashboards, so the choice carries across every page.
(function () {
  var root = document.documentElement;
  try {
    var saved = localStorage.getItem("cms-theme");
    if (saved) root.setAttribute("data-theme", saved);
  } catch (e) {}

  document.addEventListener("DOMContentLoaded", function () {
    var btn = document.createElement("button");
    btn.type = "button";
    btn.className = "theme-toggle";
    btn.setAttribute("aria-label", "Switch between light and dark mode");

    function paint() {
      btn.textContent = root.getAttribute("data-theme") === "dark" ? "Light mode" : "Dark mode";
    }
    btn.addEventListener("click", function () {
      var next = root.getAttribute("data-theme") === "dark" ? "light" : "dark";
      root.setAttribute("data-theme", next);
      try { localStorage.setItem("cms-theme", next); } catch (e) {}
      paint();
    });
    paint();
    document.body.appendChild(btn);
  });
})();
