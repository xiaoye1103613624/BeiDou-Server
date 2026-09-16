import axios from 'axios';

export interface CommandReq {
  id?: number;
  level?: number;
  levelList?: number[];
  syntax?: string;
  defaultLevel?: number;
  defaultLevelList?: number[];
  clazz?: string;
  description?: string;
  enabled?: boolean;
}

export function getCommandList(data: any) {
  return axios.post('/command/v1/getCommandListFromDB', data);
}

export function updateCommand(data: CommandReq) {
  return axios.post('/command/v1/updateCommand', data);
}

export function reloadEventsByGMCommand() {
  return axios.get('/command/v1/reloadEventsByGMCommand');
}

export function reloadPortalsByGMCommand() {
  return axios.get('/command/v1/reloadPortalsByGMCommand');
}

export function reloadMapsByGMCommand() {
  return axios.get('/command/v1/reloadMapsByGMCommand');
}

export function reloadMapScriptsByGMCommand() {
  return axios.get('/command/v1/reloadMapScriptsByGMCommand');
}

export function reloadQuestScriptsByGMCommand() {
  return axios.get('/command/v1/reloadQuestScriptsByGMCommand');
}

export function reloadNpcScriptsByGMCommand() {
  return axios.get('/command/v1/reloadNpcScriptsByGMCommand');
}

export function reloadReactorScriptsByGMCommand() {
  return axios.get('/command/v1/reloadReactorScriptsByGMCommand');
}

export function reloadAllScriptsByGMCommand() {
  return axios.get('/command/v1/reloadAllScriptsByGMCommand');
}

export interface ReloadScriptsByPathsResult {
  reloaded: string[];
  skipped: { path: string; reason: string }[];
}

export function reloadScriptsByPaths(paths: string[]) {
  return axios.post('/command/v1/reloadScriptsByPaths', { paths });
}

export function reloadShopsByGMCommand() {
  return axios.get('/command/v1/reloadShopsByGMCommand');
}

export function reloadDropsByGMCommand() {
  return axios.get('/command/v1/reloadDropsByGMCommand');
}
