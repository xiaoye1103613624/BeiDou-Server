import axios from 'axios';

/** 进阶消耗材料 */
export interface EquipAdvanceCost {
  id?: number;
  itemId: number;
  count: number;
}

/** 进阶阶段配置 */
export interface EquipAdvanceStage {
  id?: number;
  stageOrder: number;
  targetItemId: number;
  targetItemName: string;
  mesoCost: number;
  cashCost: number;
  creditCost: number;
  strAdd: number;
  dexAdd: number;
  intAdd: number;
  lukAdd: number;
  hpAdd: number;
  mpAdd: number;
  watkAdd: number;
  matkAdd: number;
  wdefAdd: number;
  mdefAdd: number;
  accAdd: number;
  avoidAdd: number;
  speedAdd: number;
  jumpAdd: number;
  costs: EquipAdvanceCost[];
}

/** 进阶路线配置 */
export interface EquipAdvanceRoute {
  id?: number;
  jobGroup: string;
  routeName: string;
  enabled: number;
  stages: EquipAdvanceStage[];
}

/** 获取所有装备进阶路线 */
export function getEquipAdvanceList() {
  return axios.get('/equipAdvance/v1/getRouteList');
}

/** 获取单个装备进阶路线 */
export function getEquipAdvanceRoute(id: number) {
  return axios.get(`/equipAdvance/v1/getRoute/${id}`);
}

/** 保存装备进阶路线（新增或更新） */
export function saveEquipAdvanceRoute(data: EquipAdvanceRoute) {
  return axios.post('/equipAdvance/v1/saveRoute', data);
}

/** 删除装备进阶路线 */
export function deleteEquipAdvanceRoute(id: number) {
  return axios.delete(`/equipAdvance/v1/deleteRoute/${id}`);
}
