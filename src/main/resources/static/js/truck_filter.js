document.addEventListener("DOMContentLoaded", () => {

  console.log("🚚 truck_filter.js loaded!");

  const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

  const H = { "Content-Type": "application/json" };
  if (csrfToken && csrfHeader) H[csrfHeader] = csrfToken;

  // === 체크박스 감지 ===
  const filterInputs = document.querySelectorAll(".sidebar-left input[type='checkbox']");
  filterInputs.forEach(cb => cb.addEventListener("change", sendFilterRequest));

  // === SELECT 감지 ===
  const allSelects = document.querySelectorAll(".sidebar-left select");
  allSelects.forEach(sel => sel.addEventListener("change", sendFilterRequest));

  // ===============================
  // 필터값 모으기
  // ===============================
  function collectFilters() {
    const selected = {};

    document.querySelectorAll(".sidebar-left input[type='checkbox']:checked")
      .forEach(cb => {
        if (!cb.name) return;
        if (!selected[cb.name]) selected[cb.name] = [];
        selected[cb.name].push(cb.value);
      });

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

  // ===============================
  // AJAX 요청
  // ===============================
  function sendFilterRequest() {
    const body = collectFilters();

    fetch("/truck/filter", {
      method: "POST",
      headers: H,
      body: JSON.stringify(body)
    })
      .then(res => res.json())
      .then(data => updateCarList(data.cards))
      .catch(err => console.error("필터 오류:", err));
  }

  // ===============================
  // UI 업데이트
  // ===============================
  function updateCarList(list) {
    console.log("🚚 필터 결과:", list);

    const photoGrid = document.querySelector(".encar-grid");
        photoGrid.innerHTML = "";

        list.slice(0, 8).forEach(car => {
          photoGrid.insertAdjacentHTML("beforeend", `
            <article class="encar-card">
              <button class="wish-btn" data-car-id="${car.carId}">♥</button>
              <a href="/cars/${car.carId}" class="encar-card-link">
                <img class="encar-img" src="${car.frontViewUrl}">
                <div class="encar-info">
                  <div class="encar-title">${car.modelName}</div>
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
          <td>${car.modelName} (${car.year}년, ${car.mileage}km)</td>
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
          <td>${car.modelName} (${car.year}년, ${car.mileage}km)</td>
          <td>${(car.price/10000).toLocaleString()}만원</td>
        </tr>
      `);
    });
  }
});
