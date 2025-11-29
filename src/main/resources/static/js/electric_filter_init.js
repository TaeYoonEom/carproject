document.addEventListener('DOMContentLoaded', () => {
  console.log("electric_filter_init.js loaded");

  const yearFrom = document.getElementById('yearFrom');
  const yearTo = document.getElementById('yearTo');
  const monthFrom = document.getElementById('monthFrom');
  const monthTo = document.getElementById('monthTo');
  const mileageFrom = document.getElementById('mileageFrom');
  const mileageTo = document.getElementById('mileageTo');
  const priceFrom = document.getElementById('priceFrom');
  const priceTo = document.getElementById('priceTo');
  const form = document.getElementById('electricFilterForm');

  if (!yearFrom || !priceFrom || !form) {
    console.warn("⚠️ Electric 필터 요소를 찾지 못했습니다.");
    return;
  }

  // ----------------------------
  // ✅ 1) 연식
  // ----------------------------
  const now = new Date();
  const currentYear = now.getFullYear();

  const years = [];
  for (let y = currentYear; y >= 1990; y--) years.push(y);
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
  // ✅ 2) 주행거리
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
  // ✅ 3) 가격
  // ----------------------------
  for (let p = 100; p <= 10000; p += 100) {
    [priceFrom, priceTo].forEach(sel => {
      if (!sel) return;
      const opt = document.createElement('option');
      opt.value = p;
      opt.textContent = `${p.toLocaleString()}만원`;
      sel.appendChild(opt);
    });
  }

  console.log("✅ Electric 연식·주행거리·가격 옵션 생성 완료");

  // ----------------------------
  // ✅ hidden 연결
  // ----------------------------
  function bindHidden(selectId, hiddenId) {
    const sel = document.getElementById(selectId);
    const input = document.getElementById(hiddenId);
    if (!sel || !input) return;
    sel.addEventListener('change', () => input.value = sel.value);
  }

  bindHidden("priceFrom", "priceMin");
  bindHidden("priceTo", "priceMax");
  bindHidden("yearFrom", "yearFromVal");
  bindHidden("yearTo", "yearToVal");
  bindHidden("mileageFrom", "mileageMin");
  bindHidden("mileageTo", "mileageMax");

  // ----------------------------
  // ✅ reset 버튼
  // ----------------------------
  const resetBtn = document.querySelector('.btn-reset');

    if (resetBtn) {
      resetBtn.addEventListener('click', () => {

        document.querySelectorAll(".encar-filters input[type='checkbox']")
          .forEach(el => el.checked = false);

        document.querySelectorAll(".encar-filters select")
          .forEach(el => el.selectedIndex = 0);

        // hidden 값도 초기화
        ["priceMin","priceMax","yearFromVal","yearToVal","mileageMin","mileageMax"]
          .forEach(id => {
            const el = document.getElementById(id);
            if (el) el.value = "";
          });

        form.submit();
      });
    }

});
