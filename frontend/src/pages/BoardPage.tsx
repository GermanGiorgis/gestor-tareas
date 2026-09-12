import { useEffect, useState } from "react";
import { Navbar } from "../components/Navbar";
import { CategoryBar } from "../components/CategoryBar";
import { KanbanBoard } from "../components/KanbanBoard";
import { TaskModal } from "../components/TaskModal";
import { PackageSelector } from "../components/PackageSelector";
import { ShareModal } from "../components/ShareModal";
import { PendingInvitations } from "../components/PendingInvitations";
import * as tareasApi from "../api/tareas";
import * as categoriasApi from "../api/categorias";
import * as paquetesApi from "../api/paquetes";
import * as invitacionesApi from "../api/invitaciones";
import type { Categoria, EstadoTarea, Invitacion, Paquete, Tarea, TareaInput } from "../types";

export function BoardPage() {
  const [paquetes, setPaquetes] = useState<Paquete[]>([]);
  const [paqueteActivoId, setPaqueteActivoId] = useState<number | null>(null);
  const [invitaciones, setInvitaciones] = useState<Invitacion[]>([]);
  const [shareModalAbierto, setShareModalAbierto] = useState(false);

  const [tareas, setTareas] = useState<Tarea[]>([]);
  const [categorias, setCategorias] = useState<Categoria[]>([]);
  const [categoriaSeleccionada, setCategoriaSeleccionada] = useState<number | null>(null);
  const [estadoSeleccionado, setEstadoSeleccionado] = useState<EstadoTarea | "">("");
  const [busqueda, setBusqueda] = useState("");
  const [modalAbierto, setModalAbierto] = useState(false);
  const [tareaEditando, setTareaEditando] = useState<Tarea | null>(null);
  const [cargando, setCargando] = useState(true);

  const paqueteActivo = paquetes.find((p) => p.id === paqueteActivoId) ?? null;

  async function cargarPaquetesEInvitaciones() {
    const [paquetesData, invitacionesData] = await Promise.all([
      paquetesApi.listarPaquetes(),
      invitacionesApi.misInvitaciones(),
    ]);
    setPaquetes(paquetesData);
    setInvitaciones(invitacionesData);
    setPaqueteActivoId((actual) => actual ?? paquetesData[0]?.id ?? null);
  }

  useEffect(() => {
    cargarPaquetesEInvitaciones();
  }, []);

  useEffect(() => {
    if (paqueteActivoId === null) return;
    setCargando(true);
    Promise.all([tareasApi.listarTareas(paqueteActivoId), categoriasApi.listarCategorias()]).then(
      ([tareasData, categoriasData]) => {
        setTareas(tareasData);
        setCategorias(categoriasData);
        setCargando(false);
      }
    );
  }, [paqueteActivoId]);

  const tareasFiltradas = tareas.filter((t) => {
    if (categoriaSeleccionada !== null && t.categoria?.id !== categoriaSeleccionada) return false;
    if (estadoSeleccionado && t.estado !== estadoSeleccionado) return false;
    if (busqueda && !t.titulo.toLowerCase().includes(busqueda.toLowerCase())) return false;
    return true;
  });

  async function handleGuardarTarea(input: TareaInput) {
    if (paqueteActivoId === null) return;
    if (tareaEditando) {
      const actualizada = await tareasApi.actualizarTarea(paqueteActivoId, tareaEditando.id, {
        ...input,
        estado: tareaEditando.estado,
      });
      setTareas((prev) => prev.map((t) => (t.id === actualizada.id ? actualizada : t)));
    } else {
      const nueva = await tareasApi.crearTarea(paqueteActivoId, input);
      setTareas((prev) => [...prev, nueva]);
    }
    setModalAbierto(false);
    setTareaEditando(null);
  }

  async function handleEliminarTarea(id: number) {
    if (paqueteActivoId === null) return;
    setTareas((prev) => prev.filter((t) => t.id !== id));
    await tareasApi.eliminarTarea(paqueteActivoId, id);
  }

  function handleMover(tareaId: number, nuevoEstado: EstadoTarea, nuevoOrden: number) {
    if (paqueteActivoId === null) return;
    setTareas((prev) =>
      prev.map((t) => (t.id === tareaId ? { ...t, estado: nuevoEstado, orden: nuevoOrden } : t))
    );
    tareasApi.moverTarea(paqueteActivoId, tareaId, nuevoEstado, nuevoOrden);
  }

  async function handleCrearCategoria(nombre: string, color: string) {
    const nueva = await categoriasApi.crearCategoria(nombre, color);
    setCategorias((prev) => [...prev, nueva]);
  }

  async function handleEliminarCategoria(id: number) {
    if (categoriaSeleccionada === id) setCategoriaSeleccionada(null);
    setCategorias((prev) => prev.filter((c) => c.id !== id));
    await categoriasApi.eliminarCategoria(id);
  }

  async function handleCrearPaquete(nombre: string) {
    const nuevo = await paquetesApi.crearPaquete(nombre);
    setPaquetes((prev) => [...prev, nuevo]);
    setPaqueteActivoId(nuevo.id);
  }

  async function handleAceptarInvitacion(id: number) {
    await invitacionesApi.aceptarInvitacion(id);
    setInvitaciones((prev) => prev.filter((i) => i.id !== id));
    const paquetesData = await paquetesApi.listarPaquetes();
    setPaquetes(paquetesData);
  }

  async function handleRechazarInvitacion(id: number) {
    await invitacionesApi.rechazarInvitacion(id);
    setInvitaciones((prev) => prev.filter((i) => i.id !== id));
  }

  return (
    <div className="board-page">
      <Navbar />

      <PendingInvitations
        invitaciones={invitaciones}
        onAceptar={handleAceptarInvitacion}
        onRechazar={handleRechazarInvitacion}
      />

      <div className="board-toolbar">
        <div className="toolbar-row">
          {paquetes.length > 0 && (
            <PackageSelector
              paquetes={paquetes}
              paqueteActivoId={paqueteActivoId}
              onSeleccionar={setPaqueteActivoId}
              onCrear={handleCrearPaquete}
              onCompartir={() => setShareModalAbierto(true)}
            />
          )}
        </div>
        <div className="toolbar-row">
          <CategoryBar
            categorias={categorias}
            categoriaSeleccionada={categoriaSeleccionada}
            onSeleccionar={setCategoriaSeleccionada}
            onCrear={handleCrearCategoria}
            onEliminar={handleEliminarCategoria}
          />
          <div className="board-toolbar-right">
            <select
              className="estado-filter"
              value={estadoSeleccionado}
              onChange={(e) => setEstadoSeleccionado(e.target.value as EstadoTarea | "")}
            >
              <option value="">Todos los estados</option>
              <option value="PENDIENTE">Pendiente</option>
              <option value="EN_PROGRESO">En progreso</option>
              <option value="COMPLETADA">Completada</option>
            </select>
            <input
              className="search-input"
              placeholder="Buscar tarea..."
              value={busqueda}
              onChange={(e) => setBusqueda(e.target.value)}
            />
            <button
              type="button"
              className="btn-primary"
              disabled={paqueteActivoId === null}
              onClick={() => {
                setTareaEditando(null);
                setModalAbierto(true);
              }}
            >
              + Nueva tarea
            </button>
          </div>
        </div>
      </div>

      {cargando ? (
        <p className="board-loading">Cargando...</p>
      ) : (
        <KanbanBoard
          tareas={tareasFiltradas}
          onMover={handleMover}
          onEditar={(tarea) => {
            setTareaEditando(tarea);
            setModalAbierto(true);
          }}
          onEliminar={handleEliminarTarea}
        />
      )}

      {modalAbierto && (
        <TaskModal
          tarea={tareaEditando}
          categorias={categorias}
          onGuardar={handleGuardarTarea}
          onCerrar={() => {
            setModalAbierto(false);
            setTareaEditando(null);
          }}
        />
      )}

      {shareModalAbierto && paqueteActivo && (
        <ShareModal paquete={paqueteActivo} onCerrar={() => setShareModalAbierto(false)} />
      )}
    </div>
  );
}
