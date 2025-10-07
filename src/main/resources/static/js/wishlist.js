// === 찜하기(하트) 토글 전용 ===
let wishBusy = false;

document.addEventListener('click', async (e) => {
  const btn = e.target.closest('.wish-btn');
  if (!btn) return;

  e.preventDefault();
  e.stopPropagation();

  if (wishBusy) return;
  wishBusy = true;

  const carId = Number(btn.dataset.carId);
  const prevOn = btn.classList.contains('on');

  // 낙관적 UI
  btn.classList.toggle('on', !prevOn);

  try {
    const res = await fetch('/api/wish/toggle', {
      method: 'POST',
      headers: window.csrfHeaders({ 'Content-Type': 'application/json' }),
      body: JSON.stringify({ carId })
    });

    if (res.status === 401) {
      btn.classList.toggle('on', prevOn);
      alert('로그인이 필요합니다.');
      window.location.href = '/login';
      return;
    }
    if (res.status === 403) {
      btn.classList.toggle('on', prevOn);
      alert('요청이 거부되었어요(CSRF). 다시 로그인 후 시도해주세요.');
      window.location.href = '/login';
      return;
    }
    if (res.status >= 500) {
      btn.classList.toggle('on', prevOn);
      const text = await res.text().catch(()=>'');
      console.error('SERVER-500:', text);
      alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
      return;
    }

    const data = await res.json();
    btn.classList.toggle('on', !!data.liked); // 서버 최종 판단 반영
  } catch (err) {
    console.error(err);
    btn.classList.toggle('on', prevOn);
    alert('네트워크 오류가 발생했습니다.');
  } finally {
    wishBusy = false;
  }
});
