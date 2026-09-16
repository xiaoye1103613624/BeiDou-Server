import axios from 'axios';

export interface ClientDataPathInfo {
  configured?: string;
  resolved?: string;
  jvmProperty?: string;
  configCode?: string;
  ok?: boolean;
  skipped?: boolean;
  warning?: boolean;
  message?: string;
}

export interface PathValidateResult {
  ok?: boolean;
  skipped?: boolean;
  warning?: boolean;
  path?: string;
  message?: string;
}

export interface DirectoryEntry {
  name: string;
  path: string;
}

/** POST body is wrapped by axios interceptor as `{ data }`. Empty string clears path. */
export function getClientDataPath() {
  return axios.get<any, { data: ClientDataPathInfo }>('/clientPath/v1');
}

export function setClientDataPath(path: string) {
  // interceptor skips falsy body; whitespace-only clears on server (hasText)
  return axios.post<any, { data: ClientDataPathInfo }>(
    '/clientPath/v1',
    path === '' ? ' ' : path
  );
}

export function validateClientDataPath(path: string) {
  return axios.post<any, { data: PathValidateResult }>(
    '/clientPath/v1/validate',
    path === '' ? ' ' : path
  );
}

export function listDirectories(absolutePath: string) {
  return axios.post<any, { data: DirectoryEntry[] }>(
    '/clientPath/v1/listDirectories',
    absolutePath
  );
}
