const STORAGE_KEY = 'ecommerce.session';

// Claves antiguas que guardaban el token y el usuario por separado. Se
// eliminan al iniciar sesion para no dejar datos obsoletos en el navegador.
const LEGACY_KEYS = ['user', 'token'];

export const ROL_ADMIN = 'ADMIN';
export const ROL_USUARIO = 'USER';

/**
 * Normaliza los roles que puede devolver el backend (ADMIN, ROLE_ADMIN,
 * CLIENTE, USER...) a los dos roles que entiende la interfaz.
 */
export function normalizeRole(role) {
  if (!role) {
    return ROL_USUARIO;
  }

  const normalizado = String(role).trim().toUpperCase();
  if (normalizado === ROL_ADMIN || normalizado === 'ROLE_ADMIN') {
    return ROL_ADMIN;
  }
  return ROL_USUARIO;
}

export function isAdmin(user) {
  return normalizeRole(user?.role) === ROL_ADMIN;
}

function removeLegacyKeys() {
  LEGACY_KEYS.forEach((key) => localStorage.removeItem(key));
}

/**
 * Construye la sesion a partir de la respuesta de login del backend.
 */
export function buildSession(loginResponse) {
  if (!loginResponse?.token || !loginResponse?.id) {
    throw new Error('La respuesta de login no incluye un token valido');
  }

  const segundos = Number(loginResponse.expiraEnSegundos);
  return {
    token: loginResponse.token,
    tokenType: loginResponse.tokenType || 'Bearer',
    expiresAt:
      Number.isFinite(segundos) && segundos > 0
        ? Date.now() + segundos * 1000
        : null,
    user: {
      id: loginResponse.id,
      nombre: loginResponse.nombre || '',
      email: loginResponse.email || '',
      role: normalizeRole(loginResponse.role),
    },
  };
}

export function saveSession(loginResponse) {
  const session = buildSession(loginResponse);
  localStorage.setItem(STORAGE_KEY, JSON.stringify(session));
  removeLegacyKeys();
  return session;
}

export function clearSession() {
  localStorage.removeItem(STORAGE_KEY);
  removeLegacyKeys();
}

/**
 * Devuelve la sesion vigente. Si el token expiro, limpia el almacenamiento
 * y devuelve null.
 */
export function getSession() {
  const raw = localStorage.getItem(STORAGE_KEY);
  if (!raw) {
    removeLegacyKeys();
    return null;
  }

  try {
    const session = JSON.parse(raw);
    if (!session?.token || !session?.user?.id) {
      clearSession();
      return null;
    }

    if (session.expiresAt && Date.now() >= session.expiresAt) {
      clearSession();
      return null;
    }

    // Normaliza el rol por si la sesion fue guardada por una version previa.
    return {
      ...session,
      user: { ...session.user, role: normalizeRole(session.user.role) },
    };
  } catch (error) {
    clearSession();
    return null;
  }
}

export function getUser() {
  return getSession()?.user || null;
}

export function getAccessToken() {
  return getSession()?.token || null;
}
