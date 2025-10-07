// === 메뉴 active 처리 ===
    document.addEventListener("DOMContentLoaded", () => {
      const path = window.location.pathname;
      document.querySelectorAll('.menu-left a, .menu-right a')
        .forEach(link => {
          if (link.getAttribute('data-path') === path) link.classList.add('active');
        });
    });

    // === 카드 전체 클릭: data-url로 이동 (하트 클릭은 제외) ===
    document.addEventListener('click', (e) => {
      // 하트 클릭이면 카드 네비게이션 무시
      if (e.target.closest('.wish-btn')) return;

      const card = e.target.closest('.encar-card');
      if (!card) return;

      const url = card.dataset.url || card.getAttribute('data-url');
      if (!url) return;

      // a나 버튼을 직접 눌렀으면 기본 동작 유지
      if (e.target.closest('a, button, input, select, textarea, label')) return;

      window.location.href = url;
    });

    // === CSRF 메타태그에서 읽어 헤더 생성 (Spring Security 사용 시) ===
    function csrfHeaders(base = {}) {
      const token  = document.querySelector('meta[name="_csrf"]')?.content;
      const header = document.querySelector('meta[name="_csrf_header"]')?.content;
      if (token && header) base[header] = token;
      return base;
    }

    // === 찜하기(하트) 토글 처리 ===
    let wishBusy = false; // 중복 클릭 방지

    document.addEventListener('click', async (e) => {
      const btn = e.target.closest('.wish-btn');
      if (!btn) return;

      // 디버깅 로그
      // console.log('✅ wish-btn clicked:', btn.dataset.carId);

      e.preventDefault();
      e.stopPropagation(); // 카드 네비게이션 차단

      if (wishBusy) return;
      wishBusy = true;

      const carId = Number(btn.dataset.carId);
      const prevOn = btn.classList.contains('on');

      // 낙관적 UI(즉시 토글) → 실패 시 롤백
      btn.classList.toggle('on', !prevOn);

      try {
        const res = await fetch('/api/wish/toggle', {
          method: 'POST',
          headers: csrfHeaders({ 'Content-Type': 'application/json' }),
          body: JSON.stringify({ carId })
        });

        if (res.status === 401) {
          btn.classList.toggle('on', prevOn); // 롤백
          alert('로그인이 필요합니다.');
          window.location.href = '/login';   // ✅ 로그인 페이지 이동
          return;
        }
        if (res.status === 403) {
          btn.classList.toggle('on', prevOn); // 롤백
          alert('요청이 거부되었어요(CSRF). 다시 로그인 후 시도해주세요.');
          window.location.href = '/login';   // ✅ CSRF 시에도 이동
          return;
        }
        if (res.status >= 500) {
          btn.classList.toggle('on', prevOn);
          const text = await res.text().catch(()=> '');
          console.error('SERVER-500:', text);
          alert('서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.');
          return;
        }
        const data = await res.json();
        if (!data.ok) {
          btn.classList.toggle('on', prevOn); // 롤백
          alert('처리 중 오류가 발생했습니다.');
        } else {
          // 서버 판단이 최종 상태
          btn.classList.toggle('on', !!data.liked);
        }
      } catch (err) {
        console.error(err);
        btn.classList.toggle('on', prevOn); // 롤백
        alert('네트워크 오류가 발생했습니다.');
      } finally {
        wishBusy = false;
      }
    });