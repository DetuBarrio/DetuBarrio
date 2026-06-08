// CORREGIDO: Valores simplificados para filtrar por "X estrellas o más" de forma acumulativa
export const valoracionOpciones = [
  { value: '', label: 'Todas las valoraciones' },
  { value: '1', label: '1.0 o más estrellas' },
  { value: '2', label: '2.0 o más estrellas' },
  { value: '3', label: '3.0 o más estrellas' },
  { value: '4', label: '4.0 o más estrellas' },
  { value: '5', label: '5.0 estrellas' },
]

export const horarioOpciones = [
  { value: '', label: 'Todos los comercios' },
  { value: 'abierto', label: 'Abierto ahora' },
  { value: 'cerrado', label: 'Cerrado ahora' },
]

export function normalizeImageUrl(imageUrl, imageLookup, defaultImage) {
  if (!imageUrl) {
    return defaultImage
  }

  const fileName = imageUrl.split('/').pop()
  if (imageLookup[fileName]) {
    return imageLookup[fileName]
  }

  if (/^https?:\/\//i.test(imageUrl) || imageUrl.startsWith('data:') || imageUrl.startsWith('/')) {
    return imageUrl
  }

  const cleanedImage = imageUrl.replace(/^\.\//, '').replace(/^images\//, '')
  const cleanedName = cleanedImage.split('/').pop()

  if (imageLookup[cleanedName]) {
    return imageLookup[cleanedName]
  }

  return `/images/${cleanedName}`
}

export function formatRating(value) {
  return Number(value || 0).toFixed(1)
}

export function mapComercio(comercio, imageLookup, defaultImage) {
  return {
    ...comercio,
    imageUrl: normalizeImageUrl(comercio.logo || comercio.banner || comercio.imagen, imageLookup, defaultImage),
    isOpened: comercio.abierto ?? comercio.isOpen ?? comercio.disponible ?? comercio.activo ?? null,
  }
}

export function parseMinutes(timeValue) {
  const [hours, minutes] = timeValue.split(':').map(Number)
  if (Number.isNaN(hours) || Number.isNaN(minutes)) {
    return null
  }

  return (hours * 60) + minutes
}

const DIAS_MAP = {
  domingo: 0, lunes: 1, martes: 2, miércoles: 3, miercoles: 3, jueves: 4, viernes: 5, sábado: 6, sabado: 6,
}

function diaNombreToNumber(nombre) {
  return DIAS_MAP[nombre.toLowerCase().trim()] ?? -1
}

export function parseDaysText(textoDias) {
  const texto = (textoDias || '').toLowerCase().trim()

  if (!texto) {
    return null
  }

  if (texto.includes('lunes a domingo') || texto.includes('todos los dias') || texto.includes('todos los días')) {
    return [0, 1, 2, 3, 4, 5, 6]
  }

  const patrones = [
    'lunes a viernes', 'lunes a sabado', 'lunes a sábado',
    'martes a domingo', 'martes a sabado', 'martes a sábado', 'martes a viernes',
    'miércoles a domingo', 'miercoles a domingo', 'miércoles a sabado', 'miercoles a sabado',
    'miércoles a sábado', 'miercoles a sábado', 'miércoles a viernes', 'miercoles a viernes',
    'jueves a domingo', 'jueves a sabado', 'jueves a sábado', 'jueves a viernes',
    'viernes a domingo', 'viernes a sabado', 'viernes a sábado',
    'sábado a domingo', 'sabado a domingo',
  ]

  for (const patron of patrones) {
    if (texto.includes(patron)) {
      const [inicio, , fin] = patron.split(' ')
      const diaInicio = diaNombreToNumber(inicio)
      const diaFin = diaNombreToNumber(fin)
      if (diaInicio >= 0 && diaFin >= 0) {
        if (diaInicio <= diaFin) {
          return Array.from({ length: diaFin - diaInicio + 1 }, (_, i) => diaInicio + i)
        }
        return [
          ...Array.from({ length: 7 - diaInicio }, (_, i) => diaInicio + i),
          ...Array.from({ length: diaFin + 1 }, (_, i) => i),
        ]
      }
    }
  }

  const matchDiaADia = texto.match(/([a-záéíóúñ]+)\s*[-–a ]+\s*([a-záéíóúñ]+)/i)
  if (matchDiaADia) {
    const diaInicio = diaNombreToNumber(matchDiaADia[1])
    const diaFin = diaNombreToNumber(matchDiaADia[2])
    if (diaInicio >= 0 && diaFin >= 0) {
      if (diaInicio <= diaFin) {
        return Array.from({ length: diaFin - diaInicio + 1 }, (_, i) => diaInicio + i)
      }
      return [
        ...Array.from({ length: 7 - diaInicio }, (_, i) => diaInicio + i),
        ...Array.from({ length: diaFin + 1 }, (_, i) => i),
      ]
    }
  }

  const matchAbreviado = texto.match(/([dlmxjvs])\s*[-–]\s*([dlmxjvs])/i)
  if (matchAbreviado) {
    const mapaDias = { d: 0, l: 1, m: 2, x: 3, j: 4, v: 5, s: 6 }
    const diaInicio = mapaDias[matchAbreviado[1].toLowerCase()]
    const diaFin = mapaDias[matchAbreviado[2].toLowerCase()]

    if (diaInicio === undefined || diaFin === undefined) {
      return null
    }

    if (diaInicio <= diaFin) {
      return Array.from({ length: diaFin - diaInicio + 1 }, (_, i) => diaInicio + i)
    }

    return [
      ...Array.from({ length: 7 - diaInicio }, (_, i) => diaInicio + i),
      ...Array.from({ length: diaFin + 1 }, (_, i) => i),
    ]
  }

  return null
}

function horaEnRango(minutos, apertura, cierre) {
  if (apertura <= cierre) {
    return minutos >= apertura && minutos <= cierre
  }
  return minutos >= apertura || minutos <= cierre
}

export function isComercioOpen(comercio, currentDate) {
  if (typeof comercio.isOpened === 'boolean') {
    return comercio.isOpened
  }

  const diaActual = currentDate.getDay()
  const minutosActuales = (currentDate.getHours() * 60) + currentDate.getMinutes()
  const diasPermitidos = parseDaysText(comercio.diasApertura || comercio.horario)

  if (diasPermitidos && !diasPermitidos.includes(diaActual)) {
    return false
  }

  const textoHorario = `${comercio.horario || ''}`
  const rangos = [...textoHorario.matchAll(/(\d{1,2}:\d{2})\s*[-–]\s*(\d{1,2}:\d{2})/g)]

  if (rangos.length === 0) {
    return true
  }

  for (const rango of rangos) {
    const apertura = parseMinutes(rango[1])
    const cierre = parseMinutes(rango[2])
    if (apertura !== null && cierre !== null && horaEnRango(minutosActuales, apertura, cierre)) {
      return true
    }
  }

  return false
}