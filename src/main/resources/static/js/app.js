// URL에서 token 파라미터 추출
function getTokenFromUrl() {
    const params = new URLSearchParams(window.location.search);
    return params.get("token");
}

// 로그인 후 redirect로 온 token 저장
(function init() {
    const token = getTokenFromUrl();
    if (token) {
        localStorage.setItem("accessToken", token);
        // URL에서 token 제거
        window.history.replaceState({}, document.title, "/");
    }

    loadUserInfo();
})();

function goLogin() {
    window.location.href = "/login.html";
}

function logout() {
    localStorage.removeItem("accessToken");
    alert("로그아웃 완료");
    location.reload();
}

// JWT 포함해서 API 호출
async function loadUserInfo() {
    const token = localStorage.getItem("accessToken");
    if (!token) {
        document.getElementById("user-info").innerText = "로그인 필요";
        return;
    }

    const res = await fetch("/api/me", {
        headers: {
            "Authorization": "Bearer " + token
        }
    });

    if (res.ok) {
        const data = await res.json();
        document.getElementById("user-info").innerText =
            "환영합니다: " + data.email;
    } else {
        document.getElementById("user-info").innerText =
            "토큰 만료 또는 오류";
    }
}
