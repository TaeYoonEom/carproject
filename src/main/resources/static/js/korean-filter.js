document.addEventListener('DOMContentLoaded', () => {
  console.log("✅ korean-filter.js 통합버전 loaded");

  // === 공통 유틸 ===
  const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
  const H = { 'Content-Type':'application/json' };
  if (csrfToken && csrfHeader) H[csrfHeader] = csrfToken;

  // ✅ 중앙 배너 고정 래퍼 (반드시 존재해야 함: .car-list 안에 th:insert/이중래퍼 권장)
  const rootList = document.querySelector('.car-list');
  if (!rootList) return;

  // 선택 갯수 드롭다운(교체 후 재바인딩 예정)
  // const viewCountSelect = document.getElementById("viewCountSelect");

  // === 인승 bucket → 숫자리스트 변환 ===
  const mapBucketToCaps = (label) => {
    switch (label) {
      case '2인승 이하': return [1,2];
      case '3인승': return [3];
      case '4인승': return [4];
      case '5인승': return [5];
      case '6인승': return [6];
      case '7인승': return [7];
      case '8인승': return [8];
      case '9인승': return [9];
      case '10인승 이상': return Array.from({length:11}, (_,i)=>i+10);
      default: return [];
    }
  };
  const collectCapacityList = () => {
    const buckets = [...document.querySelectorAll('.cap-bucket:checked')].map(el => el.dataset.bucket);
    const nums = new Set();
    buckets.forEach(b => mapBucketToCaps(b).forEach(n => nums.add(n)));
    return [...nums];
  };

  const pickOrNull = (v) => (v === '' ? null : Number(v));
  const toWon = (v) => v == null ? null : (v * 1);

  // ✅ 일반등록 섹션 안전 선택자 (래퍼/중복 대비: 마지막 요소 선택)
  const pickGeneral = (container) => {
    if (!container) return null;
    return container.querySelector('.general-section .encar-general-list')
        || container.querySelector('.encar-general-list'); // 예비
  };

  // === ✅ 메인 필터 함수 (정렬·페이지네이션 통합) ===
  window.applyFilter = async function applyFilter(extra = {}) {
    try {
      const mode = extra.mode || 'all';
      const sort = extra.sort || document.querySelector('.sort-btn.active')?.dataset.sort || 'recent';
      const page = Number(extra.page || 1);
      const size = document.getElementById('viewCountSelect')?.value || 10;

      // === 입력값 수집 ===
      const yFrom = document.getElementById('yearFrom')?.value || '';
      const mFrom = document.getElementById('monthFrom')?.value || '';
      const yTo   = document.getElementById('yearTo')?.value || '';
      const mTo   = document.getElementById('monthTo')?.value || '';
      const mileageMin = document.getElementById('mileageFrom')?.value || '';
      const mileageMax = document.getElementById('mileageTo')?.value || '';

      const priceDirect   = document.getElementById('directPrice')?.checked;
      const priceFromSel  = document.getElementById('priceFrom')?.value || '';
      const priceToSel    = document.getElementById('priceTo')?.value || '';
      const priceMinInput = document.getElementById('priceMinInput')?.value || '';
      const priceMaxInput = document.getElementById('priceMaxInput')?.value || '';

      const priceMinWon = priceDirect ? toWon(pickOrNull(priceMinInput)) : toWon(pickOrNull(priceFromSel));
      const priceMaxWon = priceDirect ? toWon(pickOrNull(priceMaxInput)) : toWon(pickOrNull(priceToSel));

      const body = {
        carTypes:        [...document.querySelectorAll('input[name="carTypes"]:checked')].map(el => el.value),
        manufacturers:   [...document.querySelectorAll('input[name="manufacturers"]:checked')].map(el => el.value),
        modelNames:      [...document.querySelectorAll('input[name="modelNames"]:checked')].map(el => el.value),
        carNames:        [...document.querySelectorAll('input[name="carNames"]:checked')].map(el => el.value),
        saleLocations:   [...document.querySelectorAll('input[name="saleLocations"]:checked')].map(el => el.value),
        sellerTypes:     [...document.querySelectorAll('input[name="sellerTypes"]:checked')].map(el => el.value),
        saleMethods:     [...document.querySelectorAll('input[name="saleMethods"]:checked')].map(el => el.value),
        exteriorColors:  [...document.querySelectorAll('input[name="exteriorColors"]:checked')].map(el => el.value),
        interiorColors:  [...document.querySelectorAll('input[name="interiorColors"]:checked')].map(el => el.value),
        fuelTypes:       [...document.querySelectorAll('input[name="fuelTypes"]:checked')].map(el => el.value),
        transmissions:   [...document.querySelectorAll('input[name="transmissions"]:checked')].map(el => el.value),
        performanceOpen: [...document.querySelectorAll('input[name="performanceOpen"]:checked')].map(el => el.value),
        capacities:      collectCapacityList(),
        yearFrom:  pickOrNull(yFrom),
        monthFrom: pickOrNull(mFrom),
        yearTo:    pickOrNull(yTo),
        monthTo:   pickOrNull(mTo),
        priceMin:  priceMinWon,
        priceMax:  priceMaxWon,
        mileageMin: pickOrNull(mileageMin),
        mileageMax: pickOrNull(mileageMax)
      };

      // === ✅ 서버 요청 ===
      const res = await fetch(`/korean/filter?page=${page}&size=${size}&sort=${sort}&mode=${mode}`, {
        method: "POST",
        headers: H,
        body: JSON.stringify(body)
      });

      if (!res.ok) {
        console.error('[filter] HTTP error', res.status);
        return;
      }

      const html = await res.text();
      const temp = document.createElement('div');
      temp.innerHTML = html;

      if (mode === 'general') {
        const newGeneral = pickGeneral(temp);
        const curGeneral = pickGeneral(rootList);
        if (newGeneral && curGeneral) {
          curGeneral.replaceWith(newGeneral);
        } else {
          console.warn('[filter] 일반등록 섹션을 찾지 못해 교체를 건너뜁니다.', { newGeneral: !!newGeneral, curGeneral: !!curGeneral });
             return;
        }
      } else {
        // 전체(사진/우대/일반) 교체
        rootList.innerHTML = temp.innerHTML;
      }

      rebindControls();
      window.scrollTo({ top: 300, behavior: 'smooth' });
      window.dispatchEvent(new Event('wishlistRebind'));
    } catch (e) {
      console.error('[filter] exception', e);
    }
  };

  // === ✅ 이벤트 바인딩 (위임) ===
  // 1) 필터(체크박스 + 두 종류의 셀렉트 클래스 모두 감지)
  document.addEventListener("change", (e) => {
    if (e.target.closest(".filter-group input[type='checkbox'], .styled-select, .styled-selected")) {
      window.applyFilter({ mode: "all", page: 1 });
    }
  });

  // 2) 정렬
  document.addEventListener("click", (e) => {
    const btn = e.target.closest(".sort-btn");
    if (!btn) return;
    document.querySelectorAll(".sort-btn").forEach((b) => b.classList.remove("active"));
    btn.classList.add("active");
    let size = Number(document.getElementById("viewCountSelect")?.value);
    if (!size || size < 1) size = 10;

    window.applyFilter({ mode: "general", page: 1, sort: btn.dataset.sort, size: size });
  });

  // 3) 페이지네이션
  document.addEventListener("click", (e) => {
    if (!e.target.classList.contains("page-link")) return;
    e.preventDefault();
    let size = Number(document.getElementById("viewCountSelect")?.value);
    if (!size || size < 1) size = 10;

    window.applyFilter({ mode: "general", page: Number(e.target.dataset.page), size: size });
  });

  // ✅ 교체 후 컨트롤 재바인딩
  function rebindControls() {
    const sel = document.getElementById('viewCountSelect');
    if (sel) sel.onchange = () => window.applyFilter({ mode: 'general', page: 1 });
    bindModelFacetEvents?.(); // 존재 시만 실행
  }

  // ✅ 초기화 버튼(중복 없이 1회만)
  const resetBtn = document.querySelector('.btn-reset');
  if (resetBtn) {
    resetBtn.addEventListener('click', () => {
      document
        .querySelectorAll(".encar-filters input[type='checkbox'], .encar-filters select")
        .forEach(el => {
          if (el.type === 'checkbox') el.checked = false;
          else if (el.tagName === 'SELECT') el.selectedIndex = 0;
        });
      window.applyFilter({ mode: 'all', page: 1 });
    });
  }

  // 5) 가격 직접입력 토글
  document.getElementById('directPrice')?.addEventListener('change', (e) => {
    const manual = document.querySelector('.manual-inputs');
    if (manual) manual.hidden = !e.target.checked;
    window.applyFilter({ mode: "all", page: 1 });
  });

  // 6) 제조사 → 모델 facet Ajax
  const makersUL  = document.getElementById('facet-makers');
  const modelsBox = document.getElementById('facet-models');

  makersUL?.addEventListener('change', async (e) => {
    if (e.target.name !== 'manufacturers') return;

    const selected = [...document.querySelectorAll('input[name="manufacturers"]:checked')].map(el => el.value);
    if (selected.length === 1) {
      const maker = encodeURIComponent(selected[0]);
      const res  = await fetch(`/korean/facet/models?maker=${maker}&top=6`);
      if (!res.ok) return console.warn("facet load fail", res.status);
      const html = await res.text();

      makersUL.hidden  = true;
      makersUL.style.display = 'none';
      modelsBox.hidden = false;
      modelsBox.style.display = 'block';
      modelsBox.innerHTML = html;
      bindModelFacetEvents();
    } else {
      // 선택 해제 → 다시 제조사 리스트 보이기
      modelsBox.hidden = true;
      modelsBox.style.display = 'none';
      modelsBox.innerHTML = '';

      makersUL.hidden = false;
      makersUL.style.display = 'block';
    }

    window.applyFilter({ mode: "all", page: 1 });
  });

  function bindModelFacetEvents() {
    // modelsBox가 현재 존재할 때만 바인딩
    if (!modelsBox) return;

    modelsBox.querySelector('.btn-clear-maker')?.addEventListener('click', () => {
      document.querySelectorAll('input[name="manufacturers"]:checked').forEach(el => el.checked = false);
      modelsBox.hidden = true;
       modelsBox.style.display = 'none';
      modelsBox.innerHTML = '';
      makersUL.hidden = false;
      makersUL.style.display = 'block';
      window.applyFilter({ mode: "all", page: 1 });
    });
    const maker = modelsBox.querySelector('.selected-maker-name')?.textContent.trim();

    modelsBox.querySelectorAll('input[name="modelNames"]').forEach(el => {
    el.addEventListener('change', async () => {
      // 단일 선택으로 만들고 싶으면 나머지 체크 해제
      modelsBox.querySelectorAll('input[name="modelNames"]').forEach(x => {
        if (x !== el) x.checked = false;
      });

      const modelName = el.checked ? el.value : "";

      if (maker && modelName) {
        const params = new URLSearchParams({
          maker: maker,
          modelName: modelName,
          top: "6"
        });
        const res = await fetch(`/korean/facet/models?` + params.toString());
        if (res.ok) {
          const html = await res.text();
          modelsBox.innerHTML = html;
          bindModelFacetEvents(); // 새 DOM 다시 바인딩
        }
      }

      window.applyFilter({ mode: "all", page: 1 });
    });
    // 모델/등급 체크 시 필터 적용
    modelsBox.querySelectorAll('input[name="modelNames"], input[name="carNames"]')
      .forEach(el => {
        el.addEventListener('change', () => window.applyFilter({ mode: 'all', page: 1 }));
      });
  });

  modelsBox.querySelectorAll('input[name="carNames"]').forEach(el => {
      el.addEventListener('change', () => {
        window.applyFilter({ mode: "all", page: 1 });
      });
  });

  }
});
