<template>
  <div class="legal-page py-5">
    <div class="container container-lg">
      <div class="mb-4">
        <RouterLink to="/" class="btn btn-outline-secondary btn-sm rounded-pill px-3">
          <i class="bi bi-arrow-left me-1"></i> Volver al inicio
        </RouterLink>
      </div>

      <div class="card border-0 shadow-sm rounded-4 p-4 p-lg-5">
        <h1 class="fw-bold mb-1">Política de Privacidad</h1>
        <p class="text-muted small mb-4">Última actualización: Junio 2026</p>

        <section class="mb-4">
          <h5 class="fw-bold">1. Responsable del Tratamiento</h5>
          <p>DetuBarrio, como responsable del tratamiento de tus datos personales, se compromete a proteger tu privacidad y a cumplir con el Reglamento General de Protección de Datos (RGPD) y la normativa española aplicable.</p>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">2. Datos que Recopilamos</h5>
          <p>Podemos recopilar la siguiente información:</p>
          <ul>
            <li><strong>Datos de registro:</strong> nombre, correo electrónico y contraseña.</li>
            <li><strong>Datos de perfil:</strong> información del comercio, logo, banner, dirección y horarios.</li>
            <li><strong>Datos de uso:</strong> reseñas, valoraciones, reservas y comercios favoritos.</li>
            <li><strong>Datos de comunicación:</strong> mensajes enviados a través de nuestro formulario de contacto.</li>
          </ul>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">3. Finalidad del Tratamiento</h5>
          <p>Tus datos serán utilizados para:</p>
          <ul>
            <li>Gestionar tu cuenta y proporcionar los servicios de la plataforma.</li>
            <li>Permitir la interacción entre comercios y usuarios.</li>
            <li>Mejorar nuestros servicios y la experiencia de usuario.</li>
            <li>Enviar comunicaciones relacionadas con el servicio (no publicitarias).</li>
            <li>Cumplir con obligaciones legales.</li>
          </ul>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">4. Base Legal</h5>
          <p>El tratamiento de tus datos se basa en:</p>
          <ul>
            <li>La ejecución del contrato de servicios al registrarte en la plataforma.</li>
            <li>Tu consentimiento explícito para ciertos tratamientos.</li>
            <li>El cumplimiento de obligaciones legales aplicables.</li>
          </ul>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">5. Conservación de Datos</h5>
          <p>Conservaremos tus datos personales mientras mantengas una cuenta activa en DetuBarrio. Una vez elimines tu cuenta, tus datos serán bloqueados y posteriormente eliminados conforme a los plazos legales establecidos, salvo que sean necesarios para el cumplimiento de obligaciones legales.</p>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">6. Destinatarios de los Datos</h5>
          <p>No cedemos tus datos personales a terceros, salvo:</p>
          <ul>
            <li>Por obligación legal.</li>
            <li>Con tu consentimiento previo.</li>
            <li>A proveedores de servicios necesarios para el funcionamiento de la plataforma (alojamiento, email, etc.), quienes actúan como encargados del tratamiento.</li>
          </ul>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">7. Tus Derechos</h5>
          <p>Tienes derecho a:</p>
          <ul>
            <li><strong>Acceder</strong> a tus datos personales.</li>
            <li><strong>Rectificar</strong> datos inexactos o incompletos.</li>
            <li><strong>Solicitar la supresión</strong> de tus datos cuando ya no sean necesarios.</li>
            <li><strong>Limitar u oponerte</strong> al tratamiento de tus datos.</li>
            <li><strong>Solicitar la portabilidad</strong> de tus datos.</li>
          </ul>
          <p>Puedes ejercer estos derechos contactándonos a través de nuestra <RouterLink to="/contacto">página de contacto</RouterLink>.</p>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">8. Seguridad</h5>
          <p>Implementamos medidas técnicas y organizativas adecuadas para proteger tus datos personales contra el acceso no autorizado, la alteración, divulgación o destrucción.</p>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">9. Cambios en la Política de Privacidad</h5>
          <p>Nos reservamos el derecho de actualizar esta política. Te notificaremos los cambios significativos a través de la plataforma o por correo electrónico.</p>
        </section>
      </div>
    </div>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'
</script>

<style scoped>
.legal-page {
  background:
    radial-gradient(circle at top left, rgba(58, 134, 255, 0.05), transparent 30%),
    linear-gradient(180deg, #f8fafd 0%, #ffffff 55%);
  min-height: 60vh;
}

section p:last-child {
  margin-bottom: 0;
}

ul li {
  margin-bottom: 0.35rem;
}
</style>
