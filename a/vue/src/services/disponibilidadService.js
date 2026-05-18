const API_URL = '/api/disponibilidad';

export const disponibilidadService = {
  
  // 1. Guardar la configuración de horas de Paqui
  async guardarConfiguracionHoraria(payload) {
    const response = await fetch(`${API_URL}/configurar`, {
      method: 'POST',
      headers: { 
        'Content-Type': 'application/json',
        // Aquí podrías añadir el token si lo necesitas
        'Authorization': `Bearer ${localStorage.getItem('token')}`
      },
      body: JSON.stringify(payload)
    });
    if (!response.ok) throw new Error('No se pudo guardar la disponibilidad');
    return await response.json();
  },

  // 2. Obtener las horas que Paqui ya tiene configuradas
  async getDisponibilidad(comercioId) {
    const response = await fetch(`${API_URL}/comercio/${comercioId}`);
    if (!response.ok) throw new Error('Error al obtener horarios');
    return await response.json();
  }
};