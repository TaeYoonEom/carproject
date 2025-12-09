document.addEventListener('DOMContentLoaded', () => {
  console.log("🔧 filter_init.js loaded (공통 필터 스크립트)");

  // -----------------------------------------------------------
  // 1) 공통적으로 찾는 select 필드
  // -----------------------------------------------------------
  const fields = {
    yearFrom: document.getElementById('yearFrom'),
    yearTo: document.getElementById('yearTo'),
    monthFrom: document.getElementById('monthFrom'),
    monthTo: document.getElementById('monthTo'),
    mileageFrom: document.getElementById('mileageFrom'),
    mileageTo: document.getElementById('mileageTo'),
    priceFrom: document.getElementById('priceFrom'),
    priceTo: document.getElementById('priceTo'),
  };

  // -----------------------------------------------------------
  // 2) 서버에서 넘어온 selected 값(th:value)
  //    → 어떤 페이지든 id만 일치하면 자동 반영
  // -----------------------------------------------------------
  const selected = {
      yearFrom:  document.getElementById("selectedYearFrom")?.value || "",
      yearTo:    document.getElementById("selectedYearTo")?.value || "",
      monthFrom: document.getElementById("selectedMonthFrom")?.value || "",
      monthTo:   document.getElementById("selectedMonthTo")?.value || "",
      mileageFrom: document.getElementById("selectedMileageFrom")?.value || "",
      mileageTo:   document.getElementById("selectedMileageTo")?.value || "",
      priceFrom:  document.getElementById("selectedPriceFrom")?.value || "",
      priceTo:    document.getElementById("selectedPriceTo")?.value || ""
  };

  // -----------------------------------------------------------
  // ⭐ 옵션 생성 함수 (공통)
  // -----------------------------------------------------------
  function fillOptions(select, values, unit, selectedVal) {
    if (!select) return;

    values.forEach(v => {
      const opt = document.createElement("option");
      opt.value = v;
      opt.textContent = `${v}${unit}`;
      if (String(selectedVal) === String(v)) opt.selected = true;
      select.appendChild(opt);
    });
  }

  // -----------------------------------------------------------
  // ⭐ 1) 연식 옵션 생성
  // -----------------------------------------------------------
  const now = new Date();
  const currentYear = now.getFullYear();

  const years = [];
  for (let y = currentYear; y >= 1990; y--) years.push(y);

  const months = Array.from({ length: 12 }, (_, i) => i + 1);

  fillOptions(fields.yearFrom, years, "년", selected.yearFrom);
  fillOptions(fields.yearTo, years, "년", selected.yearTo);
  fillOptions(fields.monthFrom, months, "월", selected.monthFrom);
  fillOptions(fields.monthTo, months, "월", selected.monthTo);

  // -----------------------------------------------------------
  // ⭐ 2) 주행거리 1만 km~20만 km
  // -----------------------------------------------------------
  for (let km = 10000; km <= 200000; km += 10000) {
    const label = `${km / 10000}만 km`;

    if (fields.mileageFrom) {
      const opt1 = document.createElement("option");
      opt1.value = km;
      opt1.textContent = label;
      if (String(selected.mileageFrom) === String(km)) opt1.selected = true;
      fields.mileageFrom.appendChild(opt1);
    }

    if (fields.mileageTo) {
      const opt2 = document.createElement("option");
      opt2.value = km;
      opt2.textContent = label;
      if (String(selected.mileageTo) === String(km)) opt2.selected = true;
      fields.mileageTo.appendChild(opt2);
    }
  }

  // -----------------------------------------------------------
  // ⭐ 3) 가격 100만원 ~ 10000만원
  // -----------------------------------------------------------
  for (let p = 100; p <= 10000; p += 100) {
    const label = `${p.toLocaleString()}만원`;

    if (fields.priceFrom) {
      const opt1 = document.createElement("option");
      opt1.value = p;
      opt1.textContent = label;
      if (String(selected.priceFrom) === String(p)) opt1.selected = true;
      fields.priceFrom.appendChild(opt1);
    }

    if (fields.priceTo) {
      const opt2 = document.createElement("option");
      opt2.value = p;
      opt2.textContent = label;
      if (String(selected.priceTo) === String(p)) opt2.selected = true;
      fields.priceTo.appendChild(opt2);
    }
  }

  console.log("✅ 공통 옵션 생성 완료");

  // -----------------------------------------------------------
  // ⭐ 4) hidden input 자동 연동(있을 때만)
  // -----------------------------------------------------------
  function bindHidden(selectId, hiddenId) {
    const sel = document.getElementById(selectId);
    const hidden = document.getElementById(hiddenId);
    if (!sel || !hidden) return;

    sel.addEventListener("change", () => hidden.value = sel.value);
  }

  bindHidden("priceFrom", "priceMin");
  bindHidden("priceTo", "priceMax");
  bindHidden("yearFrom", "yearFromVal");
  bindHidden("yearTo", "yearToVal");
  bindHidden("mileageFrom", "mileageMin");
  bindHidden("mileageTo", "mileageMax");

  // -----------------------------------------------------------
  // ⭐ 5) reset 버튼 공통 처리
  //   (각 페이지에서 sendFilterRequest()가 있으면 AJAX 실행됨)
  // -----------------------------------------------------------
  const resetBtn = document.querySelector(".btn-reset");

  if (resetBtn) {
    resetBtn.addEventListener("click", () => {

      // 체크박스 해제
      document.querySelectorAll(".encar-filters input[type='checkbox']")
        .forEach(el => el.checked = false);

      // select 초기화
      document.querySelectorAll(".encar-filters select")
        .forEach(el => el.selectedIndex = 0);

      // hidden 초기화
      ["priceMin","priceMax","yearFromVal","yearToVal","mileageMin","mileageMax"]
        .forEach(id => {
          const el = document.getElementById(id);
          if (el) el.value = "";
        });

      // 페이지에서 AJAX 함수 존재하면 실행, 없으면 form submit
      if (typeof sendFilterRequest === "function") {
        sendFilterRequest();
      } else {
        const form = document.getElementById("filterForm")
                 || document.getElementById("importFilterForm")
                 || document.getElementById("electricFilterForm")
                 || null;
        if (form) form.submit();
      }
    });
  }

});
