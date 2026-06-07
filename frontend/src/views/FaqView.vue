<template>
  <div class="legal-page py-5">
    <div class="container container-lg">
      <div class="mb-4">
        <RouterLink to="/" class="btn btn-outline-secondary btn-sm rounded-pill px-3">
          <i class="bi bi-arrow-left me-1"></i> Volver al inicio
        </RouterLink>
      </div>

      <div class="card border-0 shadow-sm rounded-4 p-4 p-lg-5">
        <h1 class="fw-bold mb-1">Preguntas Frecuentes (FAQ)</h1>
        <p class="text-muted mb-4">Respuestas a las dudas más comunes sobre DetuBarrio.</p>

        <div class="accordion" id="faqAccordion">
          <div v-for="(item, index) in faqItems" :key="index" class="accordion-item border-0 mb-3 rounded-3 shadow-sm" style="background-color: #f8fafc;">
            <h2 class="accordion-header">
              <button class="accordion-button collapsed fw-bold" :class="{ 'rounded-3': true }" type="button"
                      data-bs-toggle="collapse" :data-bs-target="'#faq' + index"
                      :aria-expanded="index === 0 ? 'true' : 'false'" :aria-controls="'faq' + index"
                      style="background-color: #f8fafc; box-shadow: none; font-size: 0.95rem;">
                {{ item.question }}
              </button>
            </h2>
            <div :id="'faq' + index" class="accordion-collapse collapse" :class="{ show: index === 0 }"
                 data-bs-parent="#faqAccordion">
              <div class="accordion-body text-secondary pt-0">
                {{ item.answer }}
              </div>
            </div>
          </div>
        </div>

        <div class="mt-5 p-4 bg-primary-light rounded-4 text-center">
          <h5 class="fw-bold mb-2">¿No encuentras lo que buscas?</h5>
          <p class="text-muted mb-3">Estamos aquí para ayudarte.</p>
          <RouterLink to="/contacto" class="btn btn-primary rounded-pill px-4 fw-bold">
            <i class="bi bi-envelope me-2"></i>Contactar
          </RouterLink>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { RouterLink } from 'vue-router'

const faqItems = [
  {
    question: '¿Qué es DetuBarrio?',
    answer: 'DetuBarrio es una plataforma que conecta a comercios locales con vecinos de su comunidad. Permite descubrir negocios de barrio, ver sus productos y servicios, leer opiniones de otros clientes y realizar reservas online.'
  },
  {
    question: '¿Cómo me registro como usuario?',
    answer: 'Puedes registrarte haciendo clic en "Iniciar sesión" en la parte superior derecha y seleccionando la opción "Crear cuenta". Solo necesitas un nombre, correo electrónico y una contraseña segura.'
  },
  {
    question: '¿Cómo puedo añadir mi comercio a DetuBarrio?',
    answer: 'Regístrate como usuario y, una vez dentro de tu panel, podrás solicitar el registro de tu comercio. Un administrador revisará tu solicitud y, si todo está correcto, activará tu comercio en la plataforma.'
  },
  {
    question: '¿Es gratuito el registro?',
    answer: 'Sí, el registro y uso básico de DetuBarrio es completamente gratuito tanto para usuarios como para comercios.'
  },
  {
    question: '¿Cómo puedo hacer una reserva en un comercio?',
    answer: 'Navega hasta el comercio que te interese, consulta su disponibilidad horaria y selecciona el día y hora que prefieras. Confirma la reserva y recibirás una notificación con los detalles.'
  },
  {
    question: '¿Puedo cancelar o modificar una reserva?',
    answer: 'Sí, desde tu panel de usuario puedes ver todas tus reservas y cancelarlas si es necesario. Te recomendamos hacerlo con suficiente antelación para no perjudicar al comercio.'
  },
  {
    question: '¿Cómo funcionan las valoraciones y reseñas?',
    answer: 'Después de visitar un comercio, puedes dejar una valoración con estrellas y un comentario. Tu opinión ayuda a otros vecinos a elegir el mejor comercio para sus necesidades.'
  },
  {
    question: '¿Qué hago si olvido mi contraseña?',
    answer: 'En la pantalla de inicio de sesión, haz clic en "¿Olvidaste tu contraseña?" e introduce tu correo electrónico. Te enviaremos un enlace para restablecerla de forma segura.'
  },
  {
    question: '¿Cómo puedo contactar con el equipo de DetuBarrio?',
    answer: 'Puedes escribirnos a través de nuestra página de contacto. Estaremos encantados de resolver cualquier duda o incidencia que tengas.'
  },
]
</script>

<style scoped>
.legal-page {
  background:
    radial-gradient(circle at top left, rgba(58, 134, 255, 0.05), transparent 30%),
    linear-gradient(180deg, #f8fafd 0%, #ffffff 55%);
  min-height: 60vh;
}

.accordion-button:not(.collapsed) {
  color: #0f172a;
  font-weight: 700;
}

.accordion-button:focus {
  box-shadow: none;
  border-color: rgba(58, 134, 255, 0.3);
}

.bg-primary-light {
  background-color: rgba(58, 134, 255, 0.06);
}
</style>
