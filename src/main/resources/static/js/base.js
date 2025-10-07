// === 메뉴 active 처리 ===
document.addEventListener("DOMContentLoaded", () => {
  const path = window.location.pathname;
  document.querySelectorAll('.menu-left a, .menu-right a')
    .forEach(link => {
      if (link.getAttribute('data-path') === path) link.classList.add('active');
    });
});

// === 카드 전체 클릭: data-url로 이동 (하트/폼 요소 클릭은 제외) ===
document.addEventListener('click', (e) => {
  if (e.target.closest('.wish-btn')) return; // 하트 클릭 제외
  if (e.target.closest('a, button, input, select, textarea, label')) return;

  const card = e.target.closest('.encar-card');
  if (!card) return;

  const url = card.dataset.url || card.getAttribute('data-url');
  if (url) window.location.href = url;
});

// === CSRF 헬퍼 (다른 파일에서 재사용) ===
window.csrfHeaders = function csrfHeaders(base = {}) {
  const token  = document.querySelector('meta[name="_csrf"]')?.content;
  const header = document.querySelector('meta[name="_csrf_header"]')?.content;
  if (token && header) base[header] = token;
  return base;
};
