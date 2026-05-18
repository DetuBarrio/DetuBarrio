<template>
  <div style="background-color: #f8fafc; min-height: 100vh; padding: 2rem 1rem; font-family: sans-serif;">
    <div style="max-w: 850px; margin: 0 auto;">
      
      <div style="background-color: #ffffff; border-radius: 16px; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); border: 1px solid #e2e8f0; overflow: hidden;">
        
        <header style="background: linear-gradient(135deg, #0f172a 0%, #1e1b4b 100%); padding: 2rem; color: #ffffff;">
          <h1 style="color: #ffffff !important; margin: 0; font-size: 1.85rem; font-weight: 800; letter-spacing: -0.025em; display: block;">
            Configuración del Perfil
          </h1>
          <p style="color: #cbd5e1 !important; margin: 0.5rem 0 0 0; font-size: 0.875rem; font-weight: 400;">
            Gestiona la identidad visual y los datos de tu comercio en tiempo real.
          </p>
        </header>

        <form @submit.prevent="guardarDatosGenerales" style="margin: 0;">
          
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

          <div style="padding: 1.5rem 2rem; background-color: #f1f5f9; display: flex; justify-content: flex-end; border-top: 1px solid #e2e8f0;">
            <button type="submit" :disabled="loading" 
                    style="width: 100%; max-width: 220px; background-color: #0f172a; color: #ffffff; border: none; padding: 0.85rem 1.5rem; border-radius: 10px; font-weight: 700; font-size: 0.875rem; text-transform: uppercase; letter-spacing: 0.05em; cursor: pointer; box-shadow: 0 4px 6px -1px rgba(0,0,0,0.1); display: flex; items-center: center; justify-content: center; gap: 0.5rem; transition: background-color 0.2s;">
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
import axios from 'axios';

const comercio = ref({
  id: null,
  nombreComercio: '',
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

onMounted(async () => {
  const usuarioId = localStorage.getItem('usuarioId');
  if (!usuarioId) {
    console.warn("No hay usuarioId en localStorage");
    return;
  }

  try {
    const response = await axios.get(`http://localhost:8080/api/comercios/usuario/${usuarioId}`);
    if (response.data) {
      comercio.value = response.data;
    }
  } catch (error) {
    console.error("Error cargando comercio:", error);
  }
});

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
  if (!comercio.value.id) {
    alert("Error: No se ha cargado el ID del comercio.");
    return;
  }

  loading.value = true;
  try {
    const formData = new FormData();
    
    // Mapeo estricto con los @RequestParam de tu ComercioController
    formData.append('nombre', comercio.value.nombreComercio || '');
    formData.append('descripcion', comercio.value.descripcion || '');
    formData.append('horario', comercio.value.horario || '');
    formData.append('diasApertura', comercio.value.diasApertura || '');

    // Adjuntar archivos usando los nombres exactos que espera tu backend ('logo' y 'banner')
    if (logoFile.value instanceof File) {
      formData.append('logo', logoFile.value);
    }
    
    if (bannerFile.value instanceof File) {
      formData.append('banner', bannerFile.value);
    }

    // URL corregida añadiendo '/fotos' al final tal como dicta tu @PutMapping
    const response = await axios.put(`http://localhost:8080/api/comercios/${comercio.value.id}/fotos`, formData, {
      headers: { 'Content-Type': 'multipart/form-data' }
    });

    // Actualizamos el estado del comercio con la respuesta detallada del servidor
    comercio.value = response.data;
    alert("¡Perfil actualizado con éxito!");
  } catch (error) {
    console.error("Error al guardar:", error);
    const mensajeError = error.response?.data?.message || "Error interno del servidor (500).";
    alert(`Error al actualizar: ${mensajeError}\nRevisa la consola del servidor backend si el error persiste.`);
  } finally {
    loading.value = false;
  }
}
</script>