document.addEventListener('DOMContentLoaded', () => {
  console.log("✅ filter_init.js loaded");

  // === 안전 방어: 필수 요소 확인 ===
  const yearFrom = document.getElementById('yearFrom');
  const yearTo = document.getElementById('yearTo');
  const monthFrom = document.getElementById('monthFrom');
  const monthTo = document.getElementById('monthTo');
  const mileageFrom = document.getElementById('mileageFrom');
  const mileageTo = document.getElementById('mileageTo');
  const priceFrom = document.getElementById('priceFrom');
  const priceTo = document.getElementById('priceTo');

  if (!yearFrom || !priceFrom) {
    console.warn("⚠️ 필터 요소를 찾지 못했습니다. HTML id 불일치 가능성 있음.");
    return;
  }

  // === 연식(년/월) ===
  const now = new Date();
  const currentYear = now.getFullYear();
  const years = Array.from({ length: currentYear - 1989 }, (_, i) => 1990 + i);
  const months = Array.from({ length: 12 }, (_, i) => i + 1);

  function fillOptions(select, list, unit) {
    if (!select) return;
    list.forEach(v => {
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

  // === 주행거리 (1만 km ~ 20만 km) ===
  const mileageSteps = Array.from({ length: 20 }, (_, i) => (i + 1) * 10000);
  mileageSteps.forEach(v => {
    [mileageFrom, mileageTo].forEach(sel => {
      if (!sel) return;
      const opt = document.createElement('option');
      opt.value = v;
      opt.textContent = `${(v / 10000).toLocaleString()}만 km`;
      sel.appendChild(opt);
    });
  });

  // === 가격 (100만원 ~ 10,000만원) ===
  const priceSteps = Array.from({ length: 100 }, (_, i) => (i + 1) * 100); // 100, 200, ..., 10,000
  priceSteps.forEach(v => {
    [priceFrom, priceTo].forEach(sel => {
      if (!sel) return;
      const opt = document.createElement('option');
      opt.value = v * 10000; // 원 단위
      opt.textContent = `${v.toLocaleString()}만원`;
      sel.appendChild(opt);
    });
  });

  console.log("✅ 연식·주행거리·가격 필터 옵션 자동 생성 완료!");
});
