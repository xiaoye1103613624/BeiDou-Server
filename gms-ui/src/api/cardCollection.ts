import axios from 'axios';

export interface CardCollectionItem {
  id?: number;
  regionName: string;
  sortOrder: number;
  monsterId: number;
  cardItemId: number;
}

export function getCardCollectionList(data: any) {
  return axios.post('/cardCollection/v1/getConfigList', data);
}

export function addCardCollection(data: CardCollectionItem) {
  return axios.put('/cardCollection/v1/addConfig', data);
}

export function updateCardCollection(data: CardCollectionItem) {
  return axios.post('/cardCollection/v1/updateConfig', data);
}

export function deleteCardCollection(id: number) {
  return axios.delete(`/cardCollection/v1/deleteConfig/${id}`);
}

export function deleteCardCollectionList(ids: number[]) {
  return axios.post('/cardCollection/v1/deleteConfigList', ids);
}
