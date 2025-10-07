// === 국산차: 차종 필터 Ajax ===
document.addEventListener('DOMContentLoaded', () => {
  const csrf = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

  // 차종 체크박스
  const ctBoxes = document.querySelectorAll('input[name="ct"]');

  // 카드 리스트 fragment가 들어갈 컨테이너 (encar-grid를 감싼 부모)
  const listContainer = document.querySelector('.encar-grid')?.parentElement;

  if (!listContainer) {
    console.warn('listContainer(.encar-grid parent) not found.');
    return;
  }

  function selectedCodes() {
    return Array.from(document.querySelectorAll('input[name="ct"]:checked'))
      .map(el => el.value);
  }

  async function applyFilter() {
    const body = JSON.stringify({ carTypes: selectedCodes() });

    const headers = { 'Content-Type': 'application/json' };
    if (csrf && csrfHeader) headers[csrfHeader] = csrf;

    const res = await fetch('/korean/filter', {
      method: 'POST',
      headers,
      body
    });

    // text/html fragment 반환
    const html = await res.text();
    listContainer.innerHTML = html; // 카드 목록 영역 교체
    // 이벤트는 'document' 위임으로 걸려 있어서 교체 후에도 정상 동작
  }

  ctBoxes.forEach(cb => cb.addEventListener('change', applyFilter));
});
