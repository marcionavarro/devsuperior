(function setupTogglePassword(inputId, toggleId) {
   const input = document.getElementById(inputId);
   const toggle = document.getElementById(toggleId);
   if (!input || !toggle) return;

   const eyeOn = toggle.querySelector('.icon-eye');
   const eyeOff = toggle.querySelector('.icon-eye-off');

   toggle.addEventListener('click', function () {
     const wasHidden = input.type === 'password';

     input.type = wasHidden ? 'text' : 'password';

     if (eyeOn) eyeOn.classList.toggle('is-hidden', wasHidden);
     if (eyeOff) eyeOff.classList.toggle('is-hidden', !wasHidden);

     toggle.setAttribute('aria-pressed', String(wasHidden));
     toggle.setAttribute(
       'aria-label',
       wasHidden ? 'Esconder senha' : 'Mostrar senha'
     );
   });
 })();