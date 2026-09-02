import axios from 'axios';

export interface SidebarToolConfig {
  toolIndex: number;
  label?: string;
  scriptPath?: string;
  tipTitle?: string;
  tipDesc?: string;
  enabled?: number;
  updatedAt?: string;
}

export interface SidebarScriptTreeNode {
  title: string;
  key: string;
  children?: SidebarScriptTreeNode[];
  isLeaf?: boolean;
  disabled?: boolean;
}

export function getSidebarToolList() {
  return axios.get('/sidebarTool/v1/list');
}

export function getSidebarScriptTree() {
  return axios.get('/sidebarTool/v1/scriptTree');
}

export function saveSidebarTool(data: SidebarToolConfig) {
  return axios.post('/sidebarTool/v1/save', data);
}

export function saveAllSidebarTool(data: SidebarToolConfig[]) {
  return axios.post('/sidebarTool/v1/saveAll', data);
}

export function reloadSidebarTool() {
  return axios.post('/sidebarTool/v1/reload');
}
