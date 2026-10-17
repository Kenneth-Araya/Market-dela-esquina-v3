// Helpers para leer y guardar JSON en localStorage sin que la app se caiga si falla
export function leer(clave, porDefecto) {
  try {
    const valor = localStorage.getItem(clave)
    return valor ? JSON.parse(valor) : porDefecto
  } catch {
    return porDefecto
  }
}

export function guardar(clave, valor) {
  try {
    localStorage.setItem(clave, JSON.stringify(valor))
  } catch {
    /* sin almacenamiento disponible: se ignora */
  }
}

export const pesos = (n) => '$' + Number(n).toLocaleString('es-CL')
export const DOMINIOS_PERMITIDOS = ['@duoc.cl', '@profesor.duoc.cl', '@gmail.com']
