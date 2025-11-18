document.addEventListener('DOMContentLoaded', () => {
  console.log("import_filter_init.js loaded");

  // === 요소 찾기 ===
  const yearFrom = document.getElementById('yearFrom');
  const yearTo = document.getElementById('yearTo');
  const monthFrom = document.getElementById('monthFrom');
  const monthTo = document.getElementById('monthTo');
  const mileageFrom = document.getElementById('mileageFrom');
  const mileageTo = document.getElementById('mileageTo');
  const priceFrom = document.getElementById('priceFrom');
  const priceTo = document.getElementById('priceTo');

  if (!yearFrom || !priceFrom) {
    console.warn("⚠️ 필터 요소를 찾지 못했습니다. HTML id가 일치하는지 확인 필요.");
    return;
  }

  // ----------------------------
  // ⭐ 1) 연식 (년도)
  // ----------------------------
  const now = new Date();
  const currentYear = now.getFullYear();

  // 최근 연도 → 1990년 (내림차순)
  const years = [];
  for (let y = currentYear; y >= 1990; y--) {
    years.push(y);
  }

  const months = Array.from({ length: 12 }, (_, i) => i + 1);

  function fillOptions(select, values, unit) {
    if (!select) return;
    values.forEach(v => {
      const opt = document.createElement('option');
      opt.value = v;
      opt.textContent = `${v}${unit}`;
      select.appendChild(opt);
    });
  }

  fillOptions(yearFrom, years, "년");
  fillOptions(yearTo, years, "년");
  fillOptions(monthFrom, months, "월");
  fillOptions(monthTo, months, "월");

  // ----------------------------
  // ⭐ 2) 주행거리 (1만 ~ 20만)
  // ----------------------------
  for (let km = 10000; km <= 200000; km += 10000) {
    [mileageFrom, mileageTo].forEach(sel => {
      if (!sel) return;
      const opt = document.createElement('option');
      opt.value = km;
      opt.textContent = `${(km / 10000)}만 km`;
      sel.appendChild(opt);
    });
  }

  // ----------------------------
  // ⭐ 3) 가격 (100만원 ~ 10,000만원)
  // 가격 입력은 "만원" 단위
  // ----------------------------
  for (let p = 100; p <= 10000; p += 100) {  // 100만원 단위
    [priceFrom, priceTo].forEach(sel => {
      if (!sel) return;
      const opt = document.createElement('option');
      opt.value = p; // "만원" 단위 그대로 전송
      opt.textContent = `${p.toLocaleString()}만원`;
      sel.appendChild(opt);
    });
  }

  console.log("✅ 연식·주행거리·가격 옵션 자동 생성 완료!");

  const resetBtn = document.querySelector('.btn-reset');

  if (resetBtn) {
    resetBtn.addEventListener('click', () => {

      // 체크박스 전체 해제
      document.querySelectorAll(".encar-filters input[type='checkbox']")
        .forEach(el => el.checked = false);

      // select 첫 옵션으로 초기화
      document.querySelectorAll(".encar-filters select")
        .forEach(el => el.selectedIndex = 0);

      // 🔥 필터 AJAX 다시 호출
      sendFilterRequest();
    });
  }

});
