<script>
document.addEventListener('DOMContentLoaded', () => {
  // === 공통 유틸 ===
  const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;
  const H = { 'Content-Type':'application/json' };
  if (csrfToken && csrfHeader) H[csrfHeader] = csrfToken;

  const listContainer = document.querySelector('.encar-grid')?.parentElement;

  // 인승 버킷 → 숫자리스트
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
      case '10인승 이상': return [10,11,12,13,14,15,16,17,18,19,20];
      default: return [];
    }
  };
  const collectCapacityList = () => {
    const buckets = [...document.querySelectorAll('.cap-bucket:checked')]
      .map(el => el.dataset.bucket);
    const nums = new Set();
    buckets.forEach(b => mapBucketToCaps(b).forEach(n => nums.add(n)));
    return [...nums];
  };

  // === 전역 applyFilter (다른 스크립트에서 호출 가능) ===
  window.applyFilter = async function applyFilter() {
    if (!listContainer) return;

    // 연식/월 (필요 없으면 제거)
    const yFrom = document.getElementById('year_from_y')?.value || '';
    const yTo   = document.getElementById('year_to_y')?.value || '';
    // 주행거리/가격은 셀렉트 값 기준 (직접입력은 사용 중일 때만)
    const priceDirect = document.getElementById('price-direct')?.checked;
    const priceMin = priceDirect
      ? Number(document.querySelector('input[name="price_from_input"]')?.value || '') || null
      : (Number(document.querySelector('select[name="price_from"]')?.value || '') || null);
    const priceMax = priceDirect
      ? Number(document.querySelector('input[name="price_to_input"]')?.value || '') || null
      : (Number(document.querySelector('select[name="price_to"]')?.value || '') || null);

    const body = {
      // 체크박스 계열
      carTypes:       [...document.querySelectorAll('input[name="carTypes"]:checked')].map(el => el.value),
      manufacturers:  [...document.querySelectorAll('input[name="manufacturers"]:checked')].map(el => el.value),
      modelNames:     [...document.querySelectorAll('input[name="modelNames"]:checked')].map(el => el.value),
      carNames:       [...document.querySelectorAll('input[name="carNames"]:checked')].map(el => el.value),
      saleLocations:  [...document.querySelectorAll('input[name="saleLocations"]:checked')].map(el => el.value),
      sellerTypes:    [...document.querySelectorAll('input[name="sellerTypes"]:checked')].map(el => el.value),
      saleMethods:    [...document.querySelectorAll('input[name="saleMethods"]:checked')].map(el => el.value),
      exteriorColors: [...document.querySelectorAll('input[name="exteriorColors"]:checked')].map(el => el.value),
      interiorColors: [...document.querySelectorAll('input[name="interiorColors"]:checked')].map(el => el.value),
      fuelTypes:      [...document.querySelectorAll('input[name="fuelTypes"]:checked')].map(el => el.value),
      transmissions:  [...document.querySelectorAll('input[name="transmissions"]:checked')].map(el => el.value),
      performanceOpen:[...document.querySelectorAll('input[name="performanceOpen"]:checked')].map(el => el.value),

      // 인승(버킷→정수 리스트)
      capacities: collectCapacityList(),

      // 숫자 범위
      yearFrom:  yFrom ? Number(yFrom) : null,
      yearTo:    yTo   ? Number(yTo)   : null,
      priceMin:  priceMin,
      priceMax:  priceMax,
      mileageMin: (Number(document.querySelector('select[name="mile_from"]')?.value || '') || null),
      mileageMax: (Number(document.querySelector('select[name="mile_to"]')?.value   || '') || null)
    };

    const res  = await fetch('/korean/filter', { method:'POST', headers:H, body: JSON.stringify(body) });
    const html = await res.text();
    listContainer.innerHTML = html; // 카드 fragment 교체
  };

  // === 이벤트 바인딩 ===
  // 모든 체크박스/셀렉트/입력 변경 시 필터 적용
  document.querySelectorAll(
    '.filter-group input[type="checkbox"], .filter-group select, .manual-inputs input'
  ).forEach(el => el.addEventListener('change', () => window.applyFilter()));

  // 가격 직접입력 토글 시 입력창 표시/숨김
  document.getElementById('price-direct')?.addEventListener('change', (e) => {
    const manual = document.querySelector('.manual-inputs');
    if (manual) manual.hidden = !e.target.checked;
    window.applyFilter();
  });

  // === 제조사 → 모델 facet Ajax ===
  const makersUL  = document.getElementById('facet-makers');
  const modelsBox = document.getElementById('facet-models');

  makersUL?.addEventListener('change', async (e) => {
    if (e.target.name !== 'manufacturers') return;

    const selected = [...document.querySelectorAll('input[name="manufacturers"]:checked')]
                     .map(el => el.value);

    if (selected.length === 1) {
      const maker = encodeURIComponent(selected[0]);

      // ✅ GET은 헤더 없이도 OK (CSRF 불필요). 굳이 넣을 땐 'Accept: text/html' 정도만.
      const res  = await fetch(`/korean/facet/models?maker=${maker}&top=6`);
      if (!res.ok) { console.warn('facet load fail', res.status); return; }
      const html = await res.text();

      // ✅ 전환: 제조사 목록 숨기고 모델 섹션 표시
      makersUL.hidden  = true;
      modelsBox.hidden = false;
      modelsBox.innerHTML = html;

      bindModelFacetEvents(); // 새로 삽입된 요소에 리스너 바인딩
    } else {
      // 복수 선택/모두 해제 → 모델 섹션 닫고 제조사 목록 다시 표시
      modelsBox.hidden = true;
      modelsBox.innerHTML = '';
      makersUL.hidden = false;
    }

    window.applyFilter?.();
  });

  function bindModelFacetEvents() {
    // X 버튼: 제조사 해제 + 목록 복귀
    modelsBox.querySelector('.btn-clear-maker')?.addEventListener('click', () => {
      document.querySelectorAll('input[name="manufacturers"]:checked').forEach(el => el.checked = false);
      modelsBox.hidden = true;
      modelsBox.innerHTML = '';
      makersUL.hidden = false;
      window.applyFilter?.();
    });

    // 인기모델(modelName) & 이름순(carName) 체크 시 필터 적용
    modelsBox.querySelectorAll('input[name="modelNames"], input[name="carNames"]').forEach(el => {
      el.addEventListener('change', () => window.applyFilter?.());
    });
  }

});
</script>
