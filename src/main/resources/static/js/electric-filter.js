document.addEventListener("DOMContentLoaded", () => {

  const csrfToken  = document.querySelector('meta[name="_csrf"]')?.content;
  const csrfHeader = document.querySelector('meta[name="_csrf_header"]')?.content;

  const H = { "Content-Type": "application/json" };
  if (csrfToken && csrfHeader) H[csrfHeader] = csrfToken;

  const filterInputs = document.querySelectorAll(".encar-filters input[type='checkbox']");
  const filterSelects = document.querySelectorAll(".encar-filters select");

  filterInputs.forEach(el => el.addEventListener("change", sendFilterRequest));
  filterSelects.forEach(el => el.addEventListener("change", sendFilterRequest));

  function collectFilters() {

      const selected = {};

      // 체크박스 수집
      document.querySelectorAll(".encar-filters input[type='checkbox']:checked")
          .forEach(cb => {
              const name = cb.name;
              if (!selected[name]) selected[name] = [];
              selected[name].push(cb.value);
          });

      // select 값
      selected.yearFrom = parseInt(document.getElementById("yearFrom")?.value) || null;
      selected.yearTo = parseInt(document.getElementById("yearTo")?.value) || null;
      selected.monthFrom = parseInt(document.getElementById("monthFrom")?.value) || null;
      selected.monthTo = parseInt(document.getElementById("monthTo")?.value) || null;

      selected.mileageMin = parseInt(document.getElementById("mileageFrom")?.value) || null;
      selected.mileageMax = parseInt(document.getElementById("mileageTo")?.value) || null;

      selected.priceMin = parseInt(document.getElementById("priceFrom")?.value) || null;
      selected.priceMax = parseInt(document.getElementById("priceTo")?.value) || null;

      return selected;
  }

  // 🔥 AJAX 요청
  async function sendFilterRequest() {

      const filters = collectFilters();

      const csrfToken = document.querySelector('meta[name="_csrf"]').content;
      const csrfHeader = document.querySelector('meta[name="_csrf_header"]').content;

      const res = await fetch("/electric/filter", {
          method: "POST",
          headers: {
              "Content-Type": "application/json",
              [csrfHeader]: csrfToken
          },
          body: JSON.stringify(filters)
      });

      const data = await res.json();

      updateCarCards(data.carList);
  }

  function updateCarList(data) {

    const list = data.carList;

    // 사진우대
    const grid = document.querySelector(".encar-grid");
    grid.innerHTML = "";

    list.slice(0, 8).forEach(car => {
      grid.insertAdjacentHTML("beforeend", `
        <article class="encar-card">
          <button class="wish-btn" data-car-id="${car.carId}"></button>
          <a href="/cars/${car.carId}" class="encar-card-link">
            <img class="encar-img" src="${car.imageUrl}">
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

    // 우대등록
    const premiumBody = document.querySelector(".premium-table tbody");
    premiumBody.innerHTML = "";
    list.slice(0, 8).forEach(car => {
      premiumBody.insertAdjacentHTML("beforeend", `
        <tr>
          <td><img src="${car.imageUrl}" class="thumb"></td>
          <td>${car.carName} (${car.year}년, ${car.mileage}km)</td>
          <td>${(car.price/10000).toLocaleString()}만원</td>
        </tr>
      `);
    });

    // 일반등록
    const normalBody = document.querySelectorAll(".premium-table")[1].querySelector("tbody");
    normalBody.innerHTML = "";
    list.forEach(car => {
      normalBody.insertAdjacentHTML("beforeend", `
        <tr>
          <td><img src="${car.imageUrl}" class="thumb"></td>
          <td>${car.carName} (${car.year}년, ${car.mileage}km)</td>
          <td>${(car.price/10000).toLocaleString()}만원</td>
        </tr>
      `);
    });
  }

});
