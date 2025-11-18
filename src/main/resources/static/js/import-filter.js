document.addEventListener("DOMContentLoaded", () => {

  const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

  // 공통 헤더
  const H = { "Content-Type": "application/json" };
  if (csrfToken && csrfHeader) H[csrfHeader] = csrfToken;

  // === 1) 모든 체크박스 감지 ===
  const filterInputs = document.querySelectorAll(".sidebar-left input[type='checkbox']");
  filterInputs.forEach(cb => cb.addEventListener("change", sendFilterRequest));

  // =============================
  // 2) 모든 SELECT 자동 감지 (연식/월/주행거리/가격)
  // =============================
  const allSelects = document.querySelectorAll(".sidebar-left select");
  allSelects.forEach(sel => sel.addEventListener("change", sendFilterRequest));

  // === 2) 필터값 수집 함수 ===
 function collectFilters() {
   const selected = {};

   document.querySelectorAll(".sidebar-left input[type='checkbox']:checked")
         .forEach(cb => {
           if (!cb.name) return;
           if (!selected[cb.name]) selected[cb.name] = [];
           selected[cb.name].push(cb.value);
         });

   // 숫자 범위 (연식, 가격, 주행거리)
   selected.yearFrom = document.querySelector("#yearFrom")?.value || null;
   selected.yearTo   = document.querySelector("#yearTo")?.value || null;
   selected.monthFrom = document.querySelector("#monthFrom")?.value || null;
   selected.monthTo   = document.querySelector("#monthTo")?.value || null;

   selected.mileageMin = document.querySelector("#mileageFrom")?.value || null;
   selected.mileageMax = document.querySelector("#mileageTo")?.value || null;

   selected.priceMin = document.querySelector("#priceFrom")?.value || null;
   selected.priceMax = document.querySelector("#priceTo")?.value || null;



   return selected;
 }

  // === 3) AJAX 요청 ===
  function sendFilterRequest() {
    const body = collectFilters();

    fetch("/import/filter", {
      method: "POST",
      headers: H,
      body: JSON.stringify(body)
    })
    .then(res => res.json())
    .then(data => updateCarList(data))
    .catch(err => console.error("필터 오류:", err));
  }

  // === 4) 중앙 차량 리스트 갱신 ===
  function updateCarList(data) {

    const list = data.carList;

    // ---------------------------
    // 1) 사진우대 - 앞 8개만
    // ---------------------------
    const photoGrid = document.querySelector(".encar-grid");
    photoGrid.innerHTML = "";

    list.slice(0, 8).forEach(car => {
      photoGrid.insertAdjacentHTML("beforeend", `
        <article class="encar-card">
          <button class="wish-btn" data-car-id="${car.carId}">♥</button>
          <a href="/cars/${car.carId}" class="encar-card-link">
            <img class="encar-img" src="${car.frontViewUrl}">
            <div class="encar-info">
              <div class="encar-title">${car.carName}</div>
              <div class="encar-category">
                ${car.year}년 | ${car.mileage}km<br>
                ${car.driveType} | ${car.saleLocation}
              </div>
              <div class="encar-price">${(car.price/10000).toLocaleString()}만원</div>
            </div>
          </a>
        </article>
      `);
    });

    // ---------------------------
    // 2) 우대등록 테이블 - 동일하게 8개만
    // ---------------------------
    const premiumBody = document.querySelector("#premium-body");
    premiumBody.innerHTML = "";

    list.slice(0, 8).forEach(car => {
      premiumBody.insertAdjacentHTML("beforeend", `
        <tr>
          <td><img src="${car.frontViewUrl}" class="thumb"></td>
          <td>${car.carName} (${car.year}년, ${car.mileage}km)</td>
          <td>${(car.price/10000).toLocaleString()}만원</td>
        </tr>
      `);
    });

    // ---------------------------
    // 3) 일반등록 테이블 - 전체 출력
    // ---------------------------
    const normalBody = document.querySelector("#normal-body");
    normalBody.innerHTML = "";

    list.forEach(car => {
      normalBody.insertAdjacentHTML("beforeend", `
        <tr>
          <td><img src="${car.frontViewUrl}" class="thumb"></td>
          <td>${car.carName} (${car.year}년, ${car.mileage}km)</td>
          <td>${(car.price/10000).toLocaleString()}만원</td>
        </tr>
      `);
    });
  }
});
