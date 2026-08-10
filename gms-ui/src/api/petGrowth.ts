import axios from 'axios';

/** 宠物成长阶段配置 */
export interface PetGrowthStageForm {
  id?: number;
  chainCode?: string;
  stage?: number;
  name?: string;
  petId?: number;
  nextPetId?: number | null;
  needExp?: number;
  expPerFeed?: number;
  feedItemIds?: string | null;
  expRate?: number;
  dropRate?: number;
  mesoRate?: number;
  sortOrder?: number;
  enabled?: number;
  petExists?: boolean;
  nextPetExists?: boolean;
  petNameResolved?: string;
  nextPetNameResolved?: string;
}

/** 进阶链预览 */
export interface PetGrowthPreview {
  chainCode: string;
  safe: boolean;
  warning?: string;
  stages: PetGrowthStageForm[];
}

export function getStageList() {
  return axios.get<PetGrowthStageForm[]>('/petGrowth/v1/getStageList');
}

export function getPreview() {
  return axios.get<PetGrowthPreview[]>('/petGrowth/v1/preview');
}

export function getStage(id: number) {
  return axios.get<PetGrowthStageForm>(`/petGrowth/v1/getStage/${id}`);
}

export function saveStage(data: PetGrowthStageForm) {
  return axios.post<PetGrowthStageForm>('/petGrowth/v1/saveStage', data);
}

export function toggleEnabled(id: number) {
  return axios.post(`/petGrowth/v1/toggleEnabled/${id}`);
}

export function deleteStage(id: number) {
  return axios.delete(`/petGrowth/v1/deleteStage/${id}`);
}

export function reloadCache() {
  return axios.post('/petGrowth/v1/reload');
}
