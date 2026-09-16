import axios from 'axios';

export interface FileTreeForm {
  currentKey: string;
}

export interface ReadForm {
  currentKey: string;
  title: string;
}

export interface WriteForm {
  currentKey: string;
  title: string;
  content: string;
}

export interface FileMutateForm {
  currentKey?: string;
  targetParentKey?: string;
  name?: string;
  directory?: boolean;
}

export interface FileTreeNode {
  title: string;
  key: string;
  path?: string;
  isLeaf?: boolean;
  children?: FileTreeNode[] | null;
}

export function treeFile(data: FileTreeForm) {
  return axios.post('/file/v1/tree', data);
}

export function readFile(data: ReadForm) {
  return axios.post('/file/v1/tree/read', data);
}

export function writeFile(data: WriteForm) {
  return axios.post('/file/v1/tree/write', data);
}

export function createFileNode(data: FileMutateForm) {
  return axios.post('/file/v1/tree/create', data);
}

export function renameFileNode(data: FileMutateForm) {
  return axios.post('/file/v1/tree/rename', data);
}

export function copyFileNode(data: FileMutateForm) {
  return axios.post('/file/v1/tree/copy', data);
}

export function moveFileNode(data: FileMutateForm) {
  return axios.post('/file/v1/tree/move', data);
}

export function deleteFileNode(data: FileMutateForm) {
  return axios.post('/file/v1/tree/delete', data);
}
