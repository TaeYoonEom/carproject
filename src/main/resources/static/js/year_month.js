// /js/year-month.js
document.addEventListener('DOMContentLoaded', () => {
  const yFrom = document.getElementById('year_from_y');
  const yTo   = document.getElementById('year_to_y');
  const mFrom = document.getElementById('year_from_m');
  const mTo   = document.getElementById('year_to_m');

  if (!yFrom || !yTo || !mFrom || !mTo) return;

  const currentYear = new Date().getFullYear();
  const minYear = 1990; // 필요 시 2000 등으로 조정

  // 연도 옵션: currentYear → minYear (내림차순)
  function fillYears(select, start, end) {
    for (let y = start; y >= end; y--) {
      const opt = document.createElement('option');
      opt.value = String(y);
      opt.textContent = `${y}`;
      select.appendChild(opt);
    }
  }

  // 월 옵션: 01~12
  function fillMonths(select) {
    for (let m = 1; m <= 12; m++) {
      const opt = document.createElement('option');
      opt.value = String(m).padStart(2, '0');    // "01"~"12"
      opt.textContent = `${m}월`;
      select.appendChild(opt);
    }
  }

  fillYears(yFrom, currentYear, minYear);
  fillYears(yTo,   currentYear, minYear);
  fillMonths(mFrom);
  fillMonths(mTo);
});
