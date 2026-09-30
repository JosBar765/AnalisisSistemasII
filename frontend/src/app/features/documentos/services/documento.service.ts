import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  AuditoriaDocumento,
  CatalogoDocumento,
  Documento,
  PacienteDocumento,
} from '../models/documento.model';

@Injectable({ providedIn: 'root' })
export class DocumentoService {
  private readonly http = inject(HttpClient);
  private readonly apiUrl = environment.apiUrl;

  listarPacientes(): Observable<PacienteDocumento[]> {
    return this.http.get<PacienteDocumento[]>(`${this.apiUrl}/pacientes`);
  }

  listarPorPaciente(idPaciente: number): Observable<Documento[]> {
    return this.http.get<Documento[]>(`${this.apiUrl}/documentos/paciente/${idPaciente}`);
  }

  listarAuditoriaPorPaciente(idPaciente: number): Observable<AuditoriaDocumento[]> {
    return this.http.get<AuditoriaDocumento[]>(`${this.apiUrl}/auditorias/documentos/paciente/${idPaciente}`);
  }

  subir(idPaciente: number, idCategoriaDocumento: number, archivo: File): Observable<Documento> {
    const datos = new FormData();
    datos.append('archivo', archivo);
    datos.append('idPaciente', String(idPaciente));
    datos.append('idCategoriaDocumento', String(idCategoriaDocumento));
    return this.http.post<Documento>(`${this.apiUrl}/documentos`, datos);
  }

  reemplazar(id: number, archivo: File, idMotivoModificacion: number, idCategoriaDocumento?: number): Observable<Documento> {
    const datos = new FormData();
    datos.append('archivo', archivo);
    datos.append('idMotivoModificacion', String(idMotivoModificacion));
    if (idCategoriaDocumento) {
      datos.append('idCategoriaDocumento', String(idCategoriaDocumento));
    }
    return this.http.put<Documento>(`${this.apiUrl}/documentos/${id}`, datos);
  }

  obtenerEnlace(id: number): Observable<{ url: string }> {
    return this.http.get<{ url: string }>(`${this.apiUrl}/documentos/${id}/enlace`);
  }

  listarCategorias(): Observable<CatalogoDocumento[]> {
    return this.http.get<CatalogoDocumento[]>(`${this.apiUrl}/catalogos/categorias-documento`);
  }

  listarMotivos(): Observable<CatalogoDocumento[]> {
    return this.http.get<CatalogoDocumento[]>(`${this.apiUrl}/catalogos/motivos-documento`);
  }
}
