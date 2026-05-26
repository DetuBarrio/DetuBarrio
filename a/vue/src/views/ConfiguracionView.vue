<template>
  <div style="background-color: #f8fafc; min-height: 100vh; padding: 2rem 1rem; font-family: sans-serif;">
    <div style="max-width: 850px; margin: 0 auto;">
      
      <div style="margin-bottom: 1rem; display: flex; justify-content: flex-start;">
        <button type="button" @click="irAlDashboard"
                style="background: none; border: none; color: #64748b; font-size: 0.9rem; font-weight: 600; cursor: pointer; display: flex; align-items: center; gap: 0.5rem; padding: 0.5rem 1rem; border-radius: 8px; transition: all 0.2s; background-color: #ffffff; border: 1px solid #e2e8f0; box-shadow: 0 1px 2px rgba(0,0,0,0.05);"
                onmouseover="this.style.color='#0f172a'; this.style.backgroundColor='#f1f5f9';"
                onmouseout="this.style.color='#64748b'; this.style.backgroundColor='#ffffff';">
          ⬅️ Volver al Panel
        </button>
      </div>

      <div style="background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); border: 1px solid #e2e8f0; overflow: hidden;">
        
        <header style="background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%); padding: 2rem; color: #ffffff;">
          <h1 style="color: #ffffff !important; margin: 0; font-size: 1.85rem; font-weight: 800; letter-spacing: -0.025em; display: block;">
            Configuración del Perfil
          </h1>
          <p style="color: #cbd5e1 !important; margin: 0.5rem 0 0 0; font-size: 0.875rem; font-weight: 400;">
            Gestiona la identidad visual y los datos de tu comercio en tiempo real.
          </p>
        </header>

        <div v-if="loadingComercio" style="padding: 3rem; text-center: center; color: #64748b; font-weight: 600; text-align: center;">
          ⏳ Cargando los datos de tu comercio...
        </div>

        <form v-else @submit.prevent="guardarDatosGenerales" style="margin: 0;">
          
          <div style="padding: 2rem; background-color: #f8fafc; border-bottom: 1px solid #e2e8f0;">
            <h3 style="color: #0f172a; font-size: 0.875rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; margin: 0 0 1.5rem 0; border-bottom: 2px solid #e2e8f0; padding-bottom: 0.5rem;">
              🖼️ Identidad Visual
            </h3>
            
            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(300px, 1fr)); gap: 1.5rem;">
              
              <div style="background-color: #ffffff; padding: 1.25rem; border-radius: 12px; border: 1px solid #cbd5e1; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02);">
                <label style="display: block; font-size: 0.875rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">Logo del Comercio</label>
                <input type="file" @change="onFileSelected($event, 'logo')" accept="image/*"
                       style="display: block; width: 100%; font-size: 0.875rem; color: #475569; border: 1px solid #cbd5e1; border-radius: 8px; padding: 0.5rem; background-color: #f8fafc;">
                <div style="font-size: 0.75rem; color: #64748b; margin-top: 0.5rem; line-height: 1.4;">
                  <p style="margin: 0;">• Recomendado: 500x500px</p>
                  <p style="margin: 0;">• Formatos: PNG, JPG</p>
                </div>
              </div>

              <div style="background-color: #ffffff; padding: 1.25rem; border-radius: 12px; border: 1px solid #cbd5e1; box-shadow: inset 0 2px 4px rgba(0,0,0,0.02); display: flex; flex-direction: column; justify-content: space-between;">
                <div>
                  <label style="display: block; font-size: 0.875rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">Banner Principal</label>
                  <input type="file" @change="onFileSelected($event, 'banner')" accept="image/*"
                         style="display: block; width: 100%; font-size: 0.875rem; color: #475569; border: 1px solid #cbd5e1; border-radius: 8px; padding: 0.5rem; background-color: #f8fafc;">
                </div>
                <div style="font-size: 0.75rem; color: #64748b; margin-top: 0.5rem;">
                  <p style="margin: 0;">• Formatos válidos: PNG, JPG</p>
                </div>
              </div>

            </div>
          </div>

          <div style="padding: 2rem; background-color: #ffffff; display: flex; flex-direction: column; gap: 1.5rem;">
            <h3 style="color: #0f172a; font-size: 0.875rem; font-weight: 700; text-transform: uppercase; letter-spacing: 0.05em; margin: 0; border-bottom: 2px solid #f1f5f9; padding-bottom: 0.5rem;">
              📝 Información del Establecimiento
            </h3>
            
            <div>
              <label style="display: block; font-size: 0.875rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">Nombre del Negocio</label>
              <input v-model="comercio.nombreComercio" type="text" placeholder="Ej: Chucherías Paqui"
                     style="width: 100%; box-sizing: border-box; padding: 0.75rem 1rem; border-radius: 10px; background-color: #f8fafc; border: 1px solid #cbd5e1; font-size: 0.9rem; color: #0f172a; outline: none; transition: all 0.2s;">
            </div>

            <div>
              <label style="display: block; font-size: 0.875rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">📍 Ubicación / Dirección Física</label>
              <input v-model="comercio.ubicacion" type="text" placeholder="Ej: Calle del Pan, 123, 28080 Madrid, España"
                     style="width: 100%; box-sizing: border-box; padding: 0.75rem 1rem; border-radius: 10px; background-color: #f8fafc; border: 1px solid #cbd5e1; font-size: 0.9rem; color: #0f172a; outline: none; transition: all 0.2s;">
              <span style="font-size: 0.75rem; color: #64748b; display: block; margin-top: 0.4rem;">
                Escribe la dirección completa. Esta cadena se usará para enlazar automáticamente el perfil con Google Maps.
              </span>
            </div>

            <div>
              <label style="display: block; font-size: 0.875rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">Descripción</label>
              <textarea v-model="comercio.descripcion" rows="4" placeholder="Cuéntale a tus vecinos qué haces especial..."
                        style="width: 100%; box-sizing: border-box; padding: 0.75rem 1rem; border-radius: 10px; background-color: #f8fafc; border: 1px solid #cbd5e1; font-size: 0.9rem; color: #0f172a; outline: none; resize: none; font-family: sans-serif;"></textarea>
            </div>

            <div style="display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 1.5rem;">
              <div>
                <label style="display: block; font-size: 0.875rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">Horario Comercial</label>
                <input v-model="comercio.horario" type="text" placeholder="Ej: 09:00 - 20:00" 
                       style="width: 100%; box-sizing: border-box; padding: 0.75rem 1rem; border-radius: 10px; background-color: #f8fafc; border: 1px solid #cbd5e1; font-size: 0.9rem; color: #0f172a; outline: none;">
              </div>
              <div>
                <label style="display: block; font-size: 0.875rem; font-weight: 700; color: #334155; margin-bottom: 0.5rem;">Días de Apertura</label>
                <input v-model="comercio.diasApertura" type="text" placeholder="Ej: Lunes a Sábado" 
                       style="width: 100%; box-sizing: border-box; padding: 0.75rem 1rem; border-radius: 10px; background-color: #f8fafc; border: 1px solid #cbd5e1; font-size: 0.9rem; color: #0f172a; outline: none;">
              </div>
            </div>
          </div>

          <div style="padding: 1.5rem 2rem; background-color: #f1f5f9; display: flex; justify-content: space-between; align-items: center; border-top: 1px solid #e2e8f0; gap: 1rem;">
            <button type="button" @click="irAlDashboard"
                    style="background-color: transparent; color: #475569; border: 1px solid #cbd5e1; padding: 0.85rem 1.5rem; border-radius: 10px; font-weight: 700; font-size: 0.875rem; text-transform: uppercase; letter-spacing: 0.05em; cursor: pointer; transition: all 0.2s;"
                    onmouseover="this.style.backgroundColor='#e2e8f0'; this.style.color='#0f172a';"
                    onmouseout="this.style.backgroundColor='transparent'; this.style.color='#475569';">
              Cancelar y Salir
            </button>
            
            <button type="submit" :disabled="loading" 
                    style="width: 100%; max-width: 220px; background-color: #0f172a; color: #ffffff; border: none; padding: 0.85rem 1.5rem; border-radius: 10px; font-weight: 700; font-size: 0.875rem; text-transform: uppercase; letter-spacing: 0.05em; cursor: pointer; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); display: flex; align-items: center; justify-content: center; gap: 0.5rem; transition: background-color 0.2s;"
                    onmouseover="this.style.backgroundColor='#1e1b4b';"
                    onmouseout="this.style.backgroundColor='#0f172a';">
              <span v-if="loading">⏳</span>
              {{ loading ? 'Guardando...' : 'Actualizar Perfil' }}
            </button>
          </div>

        </form>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';

const router = useRouter();

const comercio = ref({
  id: null,
  nombreComercio: '',
  ubicacion: '',
  descripcion: '',
  horario: '',
  diasApertura: '',
  logo: '',
  banner: '',
  categoriaId: null
});

const logoFile = ref(null);
const bannerFile = ref(null);
const loading = ref(false);
const loadingComercio = ref(true);

onMounted(async () => {
  const usuarioId = localStorage.getItem('usuarioId');
  
  // Intento recuperar un ID alternativo de comercio si la ruta falla
  const backupComercioId = localStorage.getItem('comercioId'); 
  
  if (!usuarioId) {
    console.warn("No hay usuarioId en localStorage");
    loadingComercio.value = false;
    return;
  }

  try {
    const response = await axios.get(`http://localhost:8080/api/comercios/usuario/${usuarioId}`);
    if (response.data) {
      comercio.value = response.data;
      
      // Aseguramos que si el backend devuelve 'nombre' se asigne a 'nombreComercio'
      if (response.data.nombre && !response.data.nombreComercio) {
        comercio.value.nombreComercio = response.data.nombre;
      }
    }
  } catch (error) {
    console.error("Error cargando comercio desde el endpoint de usuario:", error);
    
    // Si falla el endpoint de usuario pero tenemos un ID de comercio en la sesión, lo usamos como salvavidas
    if (backupComercioId) {
      console.log(`Usando ID de respaldo: ${backupComercioId}`);
      comercio.value.id = Number(backupComercioId);
    }
  } finally {
    loadingComercio.value = false;
  }
});

function irAlDashboard() {
  router.push('/dashboard/comercio');
}

function onFileSelected(event, type) {
  const file = event.target.files[0];
  if (!file) return;

  if (type === 'logo') {
    logoFile.value = file;
  } else {
    bannerFile.value = file;
  }
}

async function guardarDatosGenerales() {
  // Si sigue siendo null, intentamos verificar una vez más en el localStorage antes de fallar
  if (!comercio.value.id) {
    const ultimoRecursoId = localStorage.getItem('comercioId');
    if (ultimoRecursoId) {
      comercio.value.id = Number(ultimoRecursoId);
    } else {
      alert("Error: No se ha podido vincular un ID de comercio válido para actualizar. Revisa la conexión con el servidor.");
      return;
    }
  }

  loading.value = true;
  try {
    const formData = new FormData();
    
    // Spring Boot suele mapear "nombre" en el DTO/Entidad
    formData.append('nombre', comercio.value.nombreComercio || '');
    formData.append('ubicacion', comercio.value.ubicacion || ''); 
    formData.append('descripcion', comercio.value.descripcion || '');
    formData.append('horario', comercio.value.horario || '');
    formData.append('diasApertura', comercio.value.diasApertura || '');

    if (logoFile.value instanceof File) {
      formData.append('logo', logoFile.value);
    }
    
    if (bannerFile.value instanceof File) {
      formData.append('banner', bannerFile.value);
    }

    const response = await axios.put(`http://localhost:8080/api/comercios/${comercio.value.id}/fotos`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    if (response.data) {
      comercio.value = response.data;
      if (response.data.nombre) {
        comercio.value.nombreComercio = response.data.nombre;
      }
    }
    
    alert("¡Perfil actualizado con éxito!");
    irAlDashboard();
  } catch (error) {
    console.error("Error al guardar:", error);
    const mensajeError = error.response?.data?.message || "Error interno del servidor (500).";
    alert(`Error al actualizar: ${mensajeError}\n\nConsejo: Verifica en tu consola de Eclipse/STS por qué falla el servidor en la ruta /api/comercios/${comercio.value.id}/fotos`);
  } finally {
    loading.value = false;
  }
}
</script>