const API = '/api'

function authHeader() {
  const email = sessionStorage.getItem('email')
  const senha = sessionStorage.getItem('senha')

  if (!email || !senha) return {}

  return {
    Authorization: `Basic ${btoa(`${email}:${senha}`)}`
  }
}

export async function request(path, options = {}) {
  const response = await fetch(`${API}${path}`, {
    ...options,
    headers: {
      'Content-Type': 'application/json',
      ...authHeader(),
      ...(options.headers || {})
    }
  })

  const contentType = response.headers.get('content-type') || ''
  let body = null

  if (response.status !== 204) {
    body = contentType.includes('application/json')
      ? await response.json()
      : await response.text()
  }

  if (!response.ok) {
    const message =
      body?.message ||
      body?.erro ||
      (typeof body === 'string' && body) ||
      `Erro ${response.status}`

    const error = new Error(message)
    error.status = response.status
    error.body = body
    throw error
  }

  return body
}

export function saveCredentials(email, senha) {
  sessionStorage.setItem('email', email)
  sessionStorage.setItem('senha', senha)
}

export function clearCredentials() {
  sessionStorage.clear()
}

export const api = {
  me: () => request('/auth/me'),

  pets: () => request('/pets'),
  meusPets: () => request('/pets/meus'),
  resumo: () => request('/pets/resumo-risco'),
  criticos: () => request('/pets/criticos'),

  criarPet: (pet) =>
    request('/pets', {
      method: 'POST',
      body: JSON.stringify(pet)
    }),

  atualizarPet: (id, pet) =>
    request(`/pets/${id}`, {
      method: 'PUT',
      body: JSON.stringify(pet)
    }),

  excluirPet: (id) =>
    request(`/pets/${id}`, {
      method: 'DELETE'
    }),

  tutores: () => request('/tutores'),

  criarTutor: (tutor) =>
    request('/tutores', {
      method: 'POST',
      body: JSON.stringify(tutor)
    }),

  atualizarTutor: (id, tutor) =>
    request(`/tutores/${id}`, {
      method: 'PUT',
      body: JSON.stringify(tutor)
    }),

  excluirTutor: (id) =>
    request(`/tutores/${id}`, {
      method: 'DELETE'
    })
}
