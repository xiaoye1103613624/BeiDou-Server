import axios from 'axios';

export interface ScriptTreeForm {
  path: string;
}

export interface ScriptReadForm {
  path: string;
}

export interface ScriptWriteForm {
  path: string;
  content: string;
}

export interface ScriptCreateForm {
  path: string;
  directory: boolean;
}

export interface ScriptDeleteForm {
  path: string;
}

export interface ScriptRenameForm {
  oldPath: string;
  newPath: string;
}

export interface ScriptTreeNode {
  title: string;
  key: string;
  children?: ScriptTreeNode[];
  isLeaf: boolean;
  type: 'file' | 'directory';
}

export interface OverrideStatus {
  active: boolean;
  path: string | null;
}

export function treeScript(data: ScriptTreeForm) {
  return axios.post('/scriptFile/v1/tree', data);
}

export function readScript(data: ScriptReadForm) {
  return axios.post('/scriptFile/v1/read', data);
}

export function writeScript(data: ScriptWriteForm) {
  return axios.post('/scriptFile/v1/write', data);
}

export function createScript(data: ScriptCreateForm) {
  return axios.post('/scriptFile/v1/create', data);
}

export function deleteScript(data: ScriptDeleteForm) {
  return axios.post('/scriptFile/v1/delete', data);
}

export function renameScript(data: ScriptRenameForm) {
  return axios.post('/scriptFile/v1/rename', data);
}

export function getOverrideStatus() {
  return axios.get('/scriptFile/v1/overrideStatus');
}
