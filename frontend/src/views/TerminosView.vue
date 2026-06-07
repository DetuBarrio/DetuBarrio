<template>
  <div class="legal-page py-5">
    <div class="container container-lg">
      <div class="mb-4">
        <RouterLink to="/" class="btn btn-outline-secondary btn-sm rounded-pill px-3">
          <i class="bi bi-arrow-left me-1"></i> Volver al inicio
        </RouterLink>
      </div>

      <div class="card border-0 shadow-sm rounded-4 p-4 p-lg-5">
        <h1 class="fw-bold mb-1">Términos y Condiciones</h1>
        <p class="text-muted small mb-4">Última actualización: Junio 2026</p>

        <section class="mb-4">
          <h5 class="fw-bold">1. Introducción</h5>
          <p>Bienvenido a DetuBarrio. Al acceder y utilizar esta plataforma, aceptas cumplir con los siguientes términos y condiciones. Si no estás de acuerdo con alguno de ellos, te recomendamos no utilizar nuestros servicios.</p>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">2. Descripción del Servicio</h5>
          <p>DetuBarrio es una plataforma digital que conecta a comercios locales con vecinos de su comunidad. Los comercios pueden registrarse para ofrecer sus productos y servicios, mientras que los usuarios pueden descubrir, valorar y contactar con negocios de su barrio.</p>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">3. Registro y Cuenta</h5>
          <p>Para acceder a ciertas funcionalidades, es necesario crear una cuenta. Eres responsable de mantener la confidencialidad de tus credenciales y de todas las actividades realizadas bajo tu cuenta.</p>
          <ul>
            <li>Debes proporcionar información veraz y actualizada.</li>
            <li>No puedes utilizar una cuenta de otro usuario sin autorización.</li>
            <li>Debes notificar inmediatamente cualquier uso no autorizado de tu cuenta.</li>
          </ul>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">4. Uso Aceptable</h5>
          <p>Como usuario de DetuBarrio, te comprometes a:</p>
          <ul>
            <li>No utilizar la plataforma para actividades ilegales o no autorizadas.</li>
            <li>No publicar contenido falso, engañoso o difamatorio.</li>
            <li>Respetar las opiniones y valoraciones de otros usuarios.</li>
            <li>No realizar reservas falsas o malintencionadas.</li>
          </ul>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">5. Responsabilidad del Comercio</h5>
          <p>Los comercios registrados en DetuBarrio son responsables de:</p>
          <ul>
            <li>Mantener actualizada la información de su negocio.</li>
            <li>Gestionar correctamente las reservas y disponibilidad.</li>
            <li>Cumplir con la normativa aplicable a su actividad comercial.</li>
            <li>Responder directamente ante los clientes por los servicios prestados.</li>
          </ul>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">6. Propiedad Intelectual</h5>
          <p>Todos los contenidos de la plataforma (logos, diseño, textos, imágenes) son propiedad de DetuBarrio o de sus respectivos titulares. Queda prohibida su reproducción sin autorización expresa.</p>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">7. Limitación de Responsabilidad</h5>
          <p>DetuBarrio actúa como intermediario entre comercios y usuarios. No nos hacemos responsables de:</p>
          <ul>
            <li>La calidad de los productos o servicios ofrecidos por los comercios.</li>
            <li>Incumplimientos entre comercios y clientes.</li>
            <li>Daños derivados del uso de la plataforma.</li>
          </ul>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">8. Modificaciones</h5>
          <p>Nos reservamos el derecho de modificar estos términos en cualquier momento. Los cambios serán notificados a través de la plataforma y entrarán en vigor inmediatamente después de su publicación.</p>
        </section>

        <section class="mb-4">
          <h5 class="fw-bold">9. Contacto</h5>
          <p>Para cualquier consulta sobre estos términos, puedes contactarnos a través de nuestra <RouterLink to="/contacto">página de contacto</RouterLink>.</p>
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
