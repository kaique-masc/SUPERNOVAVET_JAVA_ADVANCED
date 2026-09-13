import { useEffect, useMemo, useState } from 'react'
import { api, clearCredentials, saveCredentials } from './api'

const emptyPet = {
  nome: '',
  idade: '',
  especie: '',
  nivelRisco: 'BAIXO',
  tutorEmail: ''
}

const emptyTutor = {
  nome: '',
  email: '',
  telefone: '',
  senha: ''
}

function Login({ onLogin }) {
  const [email, setEmail] = useState('admin@supernovavet.com')
  const [senha, setSenha] = useState('admin123')
  const [erro, setErro] = useState('')
  const [loading, setLoading] = useState(false)

  async function entrar(e) {
    e.preventDefault()
    setLoading(true)
    setErro('')

    try {
      saveCredentials(email, senha)
      const usuario = await api.me()
      onLogin(usuario)
    } catch {
      clearCredentials()
      setErro('E-mail ou senha inválidos.')
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-page">
      <div className="login-card">
        <div className="brand-mark">✦</div>
        <h1>SuperNova VET</h1>
        <p>Monitoramento inteligente e seguro para pets.</p>

        <form onSubmit={entrar}>
          <label>E-mail</label>
          <input
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            type="email"
            required
          />

          <label>Senha</label>
          <input
            value={senha}
            onChange={(e) => setSenha(e.target.value)}
            type="password"
            required
          />

          {erro && <div className="alert error">{erro}</div>}

          <button className="primary full" disabled={loading}>
            {loading ? 'Entrando...' : 'Entrar'}
          </button>
        </form>
      </div>
    </div>
  )
}

function Header({ usuario, onLogout }) {
  return (
    <header className="topbar">
      <div>
        <h2>SuperNova VET</h2>
        <span className="muted">Painel de monitoramento</span>
      </div>
      <div className="user-box">
        <div>
          <strong>{usuario.email}</strong>
          <span className={`badge role ${usuario.perfil?.toLowerCase()}`}>
            {usuario.perfil}
          </span>
        </div>
        <button className="ghost" onClick={onLogout}>Sair</button>
      </div>
    </header>
  )
}

function Sidebar({ page, setPage, perfil }) {
  const items = [
    ['dashboard', '⌂', 'Dashboard'],
    ['pets', '🐾', 'Pets'],
    ['criticos', '⚠', 'Pets críticos']
  ]

  if (perfil === 'ADMIN' || perfil === 'VETERINARIO') {
    items.push(['tutores', '👤', 'Tutores'])
  }

  return (
    <aside className="sidebar">
      <div className="logo">✦ SNV</div>
      <nav>
        {items.map(([key, icon, label]) => (
          <button
            key={key}
            className={page === key ? 'active' : ''}
            onClick={() => setPage(key)}
          >
            <span>{icon}</span>{label}
          </button>
        ))}
      </nav>
    </aside>
  )
}

function Dashboard({ perfil }) {
  const [resumo, setResumo] = useState({ BAIXO: 0, MEDIO: 0, ALTO: 0, TOTAL: 0 })

  useEffect(() => {
    async function carregarResumo() {
      try {
        if (perfil === 'TUTOR') {
          const pets = await api.meusPets()
          setResumo({
            BAIXO: pets.filter(p => p.nivelRisco === 'BAIXO').length,
            MEDIO: pets.filter(p => p.nivelRisco === 'MEDIO').length,
            ALTO: pets.filter(p => p.nivelRisco === 'ALTO').length,
            TOTAL: pets.length
          })
        } else {
          setResumo(await api.resumo())
        }
      } catch (e) {
        console.error(e)
      }
    }

    carregarResumo()
  }, [perfil])

  const cards = [
    ['TOTAL', resumo.TOTAL ?? 0, 'Todos os pets'],
    ['BAIXO', resumo.BAIXO ?? 0, 'Risco baixo'],
    ['MEDIO', resumo.MEDIO ?? 0, 'Risco médio'],
    ['ALTO', resumo.ALTO ?? 0, 'Risco alto']
  ]

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Dashboard</h1>
          <p>Visão geral do monitoramento dos animais.</p>
        </div>
      </div>
      <div className="stats-grid">
        {cards.map(([label, value, desc]) => (
          <div className="stat-card" key={label}>
            <span className={`badge risk ${label.toLowerCase()}`}>{label}</span>
            <strong>{value}</strong>
            <small>{desc}</small>
          </div>
        ))}
      </div>
      <div className="panel intro-panel">
        <div>
          <h3>Monitoramento de risco</h3>
          <p>
            Acompanhe os pets cadastrados, identifique animais críticos e mantenha
            os dados dos tutores organizados.
          </p>
        </div>
        <div className="big-icon">🐕</div>
      </div>
    </>
  )
}

function PetModal({ pet, tutores, onClose, onSaved }) {
  const [form, setForm] = useState(
    pet
      ? {
          nome: pet.nome || '',
          idade: pet.idade ?? '',
          especie: pet.especie || '',
          nivelRisco: pet.nivelRisco || 'BAIXO',
          tutorEmail: pet.tutor?.email || ''
        }
      : emptyPet
  )
  const [erro, setErro] = useState('')
  const [saving, setSaving] = useState(false)

  async function salvar(e) {
    e.preventDefault()
    setSaving(true)
    setErro('')

    const payload = {
      nome: form.nome,
      idade: Number(form.idade),
      especie: form.especie,
      nivelRisco: form.nivelRisco,
      tutor: {
        email: form.tutorEmail,
        nome: tutores.find(t => t.email === form.tutorEmail)?.nome || 'Tutor',
        telefone: tutores.find(t => t.email === form.tutorEmail)?.telefone || '11999999999'
      }
    }

    try {
      if (pet) {
        await api.atualizarPet(pet.id, payload)
      } else {
        await api.criarPet(payload)
      }
      onSaved()
      onClose()
    } catch (e) {
      setErro(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <div className="modal-title">
          <h3>{pet ? 'Editar pet' : 'Cadastrar pet'}</h3>
          <button className="icon-button" onClick={onClose}>×</button>
        </div>

        <form onSubmit={salvar} className="form-grid">
          <div>
            <label>Nome</label>
            <input value={form.nome} onChange={e => setForm({...form, nome:e.target.value})} required />
          </div>
          <div>
            <label>Idade</label>
            <input type="number" min="0" max="30" value={form.idade} onChange={e => setForm({...form, idade:e.target.value})} required />
          </div>
          <div>
            <label>Espécie</label>
            <input value={form.especie} onChange={e => setForm({...form, especie:e.target.value})} required />
          </div>
          <div>
            <label>Nível de risco</label>
            <select value={form.nivelRisco} onChange={e => setForm({...form, nivelRisco:e.target.value})}>
              <option>BAIXO</option>
              <option>MEDIO</option>
              <option>ALTO</option>
            </select>
          </div>
          {!pet && (
            <div className="span-2">
              <label>Tutor</label>
              <select value={form.tutorEmail} onChange={e => setForm({...form, tutorEmail:e.target.value})} required>
                <option value="">Selecione um tutor</option>
                {tutores.filter(t => t.perfil === 'TUTOR' || !t.perfil).map(t => (
                  <option key={t.id} value={t.email}>{t.nome} — {t.email}</option>
                ))}
              </select>
            </div>
          )}

          {erro && <div className="alert error span-2">{erro}</div>}

          <div className="modal-actions span-2">
            <button type="button" className="ghost" onClick={onClose}>Cancelar</button>
            <button className="primary" disabled={saving}>{saving ? 'Salvando...' : 'Salvar'}</button>
          </div>
        </form>
      </div>
    </div>
  )
}

function Pets({ perfil }) {
  const [pets, setPets] = useState([])
  const [tutores, setTutores] = useState([])
  const [busca, setBusca] = useState('')
  const [modal, setModal] = useState(null)
  const [erro, setErro] = useState('')

  const canWrite = perfil === 'ADMIN' || perfil === 'VETERINARIO'
  const canDelete = perfil === 'ADMIN'

  async function carregar() {
    try {
      if (perfil === 'TUTOR') {
        setPets(await api.meusPets())
      } else {
        setPets(await api.pets())
      }

      if (canWrite) {
        setTutores(await api.tutores())
      }
    } catch (e) {
      setErro(e.message)
    }
  }

  useEffect(() => { carregar() }, [])

  const filtrados = useMemo(() => {
    const q = busca.toLowerCase()
    return pets.filter(p =>
      [p.nome, p.especie, p.nivelRisco, p.tutor?.nome]
        .filter(Boolean)
        .some(v => String(v).toLowerCase().includes(q))
    )
  }, [pets, busca])

  async function excluir(pet) {
    if (!window.confirm(`Excluir o pet "${pet.nome}"?`)) return
    try {
      await api.excluirPet(pet.id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Pets</h1>
          <p>{perfil === 'TUTOR' ? 'Acompanhe somente os seus animais.' : 'Cadastro e acompanhamento dos animais.'}</p>
        </div>
        {canWrite && <button className="primary" onClick={() => setModal({})}>+ Novo pet</button>}
      </div>

      {erro && <div className="alert error">{erro}</div>}

      <div className="panel">
        <input className="search" placeholder="Buscar por nome, espécie, risco ou tutor..." value={busca} onChange={e => setBusca(e.target.value)} />

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Pet</th><th>Espécie</th><th>Idade</th><th>Risco</th><th>Tutor</th>
                {canWrite && <th>Ações</th>}
              </tr>
            </thead>
            <tbody>
              {filtrados.map(p => (
                <tr key={p.id}>
                  <td><strong>{p.nome}</strong></td>
                  <td>{p.especie}</td>
                  <td>{p.idade}</td>
                  <td><span className={`badge risk ${p.nivelRisco?.toLowerCase()}`}>{p.nivelRisco}</span></td>
                  <td>{p.tutor?.nome || '—'}</td>
                  {canWrite && (
                    <td className="actions">
                      <button className="link" onClick={() => setModal(p)}>Editar</button>
                      {canDelete && <button className="link danger" onClick={() => excluir(p)}>Excluir</button>}
                    </td>
                  )}
                </tr>
              ))}
              {!filtrados.length && <tr><td colSpan="6" className="empty">Nenhum pet encontrado.</td></tr>}
            </tbody>
          </table>
        </div>
      </div>

      {modal && (
        <PetModal
          pet={modal.id ? modal : null}
          tutores={tutores}
          onClose={() => setModal(null)}
          onSaved={carregar}
        />
      )}
    </>
  )
}

function Criticos({ perfil }) {
  const [pets, setPets] = useState([])

  useEffect(() => {
    async function carregarCriticos() {
      try {
        if (perfil === 'TUTOR') {
          const meusPets = await api.meusPets()
          setPets(meusPets.filter(p => p.nivelRisco === 'ALTO'))
        } else {
          setPets(await api.criticos())
        }
      } catch (e) {
        console.error(e)
      }
    }

    carregarCriticos()
  }, [perfil])

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Pets críticos</h1>
          <p>{perfil === 'TUTOR' ? 'Seus animais classificados com nível de risco ALTO.' : 'Animais classificados com nível de risco ALTO.'}</p>
        </div>
      </div>

      <div className="critical-grid">
        {pets.map(p => (
          <div className="critical-card" key={p.id}>
            <div className="critical-icon">⚠</div>
            <div>
              <h3>{p.nome}</h3>
              <p>{p.especie} • {p.idade} anos</p>
              <span>{p.tutor?.nome || 'Tutor não informado'}</span>
            </div>
            <span className="badge risk alto">ALTO</span>
          </div>
        ))}
        {!pets.length && <div className="panel empty">Nenhum pet crítico no momento.</div>}
      </div>
    </>
  )
}

function TutorModal({ tutor, onClose, onSaved }) {
  const [form, setForm] = useState(
    tutor
      ? { nome: tutor.nome, email: tutor.email, telefone: tutor.telefone, senha: '' }
      : emptyTutor
  )
  const [erro, setErro] = useState('')
  const [saving, setSaving] = useState(false)

  async function salvar(e) {
    e.preventDefault()
    setSaving(true)
    setErro('')

    try {
      if (tutor) {
        await api.atualizarTutor(tutor.id, form)
      } else {
        await api.criarTutor(form)
      }
      onSaved()
      onClose()
    } catch (e) {
      setErro(e.message)
    } finally {
      setSaving(false)
    }
  }

  return (
    <div className="modal-backdrop">
      <div className="modal">
        <div className="modal-title">
          <h3>{tutor ? 'Editar tutor' : 'Novo tutor'}</h3>
          <button className="icon-button" onClick={onClose}>×</button>
        </div>

        <form onSubmit={salvar} className="form-grid">
          <div>
            <label>Nome</label>
            <input value={form.nome} onChange={e => setForm({...form, nome:e.target.value})} required />
          </div>
          <div>
            <label>E-mail</label>
            <input type="email" value={form.email} onChange={e => setForm({...form, email:e.target.value})} required />
          </div>
          <div>
            <label>Telefone</label>
            <input value={form.telefone} onChange={e => setForm({...form, telefone:e.target.value})} placeholder="11999999999" required />
          </div>
          <div>
            <label>{tutor ? 'Nova senha' : 'Senha'}</label>
            <input
              type="password"
              value={form.senha}
              onChange={e => setForm({...form, senha:e.target.value})}
              placeholder={tutor ? 'Digite para alterar' : ''}
              required={!tutor}
            />
          </div>

          {erro && <div className="alert error span-2">{erro}</div>}

          <div className="modal-actions span-2">
            <button type="button" className="ghost" onClick={onClose}>Cancelar</button>
            <button className="primary" disabled={saving}>{saving ? 'Salvando...' : 'Salvar'}</button>
          </div>
        </form>
      </div>
    </div>
  )
}

function Tutores({ perfil }) {
  const [tutores, setTutores] = useState([])
  const [busca, setBusca] = useState('')
  const [modal, setModal] = useState(null)
  const [erro, setErro] = useState('')
  const isAdmin = perfil === 'ADMIN'

  async function carregar() {
    try {
      setTutores(await api.tutores())
      setErro('')
    } catch (e) {
      setErro(e.message)
    }
  }

  useEffect(() => { carregar() }, [])

  const filtrados = useMemo(() => {
    const q = busca.toLowerCase()
    return tutores.filter(t =>
      [t.nome, t.email, t.telefone, t.perfil]
        .filter(Boolean)
        .some(v => String(v).toLowerCase().includes(q))
    )
  }, [tutores, busca])

  async function excluir(tutor) {
    if (!window.confirm(`Excluir o tutor "${tutor.nome}"?`)) return
    try {
      await api.excluirTutor(tutor.id)
      carregar()
    } catch (e) {
      setErro(e.message)
    }
  }

  return (
    <>
      <div className="page-heading">
        <div>
          <h1>Tutores</h1>
          <p>{isAdmin ? 'Gerencie as credenciais e dados dos tutores.' : 'Consulta de tutores cadastrados.'}</p>
        </div>
        {isAdmin && <button className="primary" onClick={() => setModal({})}>+ Novo tutor</button>}
      </div>

      {erro && <div className="alert error">{erro}</div>}

      <div className="panel">
        <input className="search" placeholder="Buscar tutor..." value={busca} onChange={e => setBusca(e.target.value)} />

        <div className="table-wrap">
          <table>
            <thead>
              <tr>
                <th>Nome</th><th>E-mail</th><th>Telefone</th><th>Perfil</th>
                {isAdmin && <th>Ações</th>}
              </tr>
            </thead>
            <tbody>
              {filtrados.map(t => (
                <tr key={t.id}>
                  <td><strong>{t.nome}</strong></td>
                  <td>{t.email}</td>
                  <td>{t.telefone}</td>
                  <td><span className={`badge role ${t.perfil?.toLowerCase()}`}>{t.perfil || 'SEM PERFIL'}</span></td>
                  {isAdmin && (
                    <td className="actions">
                      {t.perfil === 'TUTOR' || !t.perfil ? (
                        <>
                          <button className="link" onClick={() => setModal(t)}>Editar</button>
                          <button className="link danger" onClick={() => excluir(t)}>Excluir</button>
                        </>
                      ) : (
                        <span className="muted">Conta do sistema</span>
                      )}
                    </td>
                  )}
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {modal && (
        <TutorModal
          tutor={modal.id ? modal : null}
          onClose={() => setModal(null)}
          onSaved={carregar}
        />
      )}
    </>
  )
}

export default function App() {
  const [usuario, setUsuario] = useState(null)
  const [page, setPage] = useState('dashboard')
  const [loading, setLoading] = useState(true)

  useEffect(() => {
    const email = sessionStorage.getItem('email')
    const senha = sessionStorage.getItem('senha')

    if (!email || !senha) {
      setLoading(false)
      return
    }

    api.me()
      .then(setUsuario)
      .catch(() => clearCredentials())
      .finally(() => setLoading(false))
  }, [])

  function logout() {
    clearCredentials()
    setUsuario(null)
    setPage('dashboard')
  }

  if (loading) return <div className="loading-page">Carregando...</div>
  if (!usuario) return <Login onLogin={setUsuario} />

  return (
    <div className="app-shell">
      <Sidebar page={page} setPage={setPage} perfil={usuario.perfil} />
      <div className="main-area">
        <Header usuario={usuario} onLogout={logout} />
        <main className="content">
          {page === 'dashboard' && <Dashboard perfil={usuario.perfil} />}
          {page === 'pets' && <Pets perfil={usuario.perfil} />}
          {page === 'criticos' && <Criticos perfil={usuario.perfil} />}
          {page === 'tutores' && <Tutores perfil={usuario.perfil} />}
        </main>
      </div>
    </div>
  )
}
