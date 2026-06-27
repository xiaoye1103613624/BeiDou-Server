import axios from 'axios';

/** 每日活跃任务表单 */
export interface DailyActiveTaskForm {
  /** 任务ID（更新时必填） */
  id?: number;
  /** 任务标识（唯一），供代码/脚本按key调用，如 zhuangbei_zhajuan */
  taskKey?: string;
  /** 任务展示名称，如 装备砸卷 */
  taskName?: string;
  /** 完成该任务所需的进度次数 */
  targetCount?: number;
  /** 达成target_count后可领取的金币奖励 */
  rewardMeso?: number;
  /** 达成后可领取的奖励物品ID，0表示无物品奖励 */
  rewardItemId?: number;
  /** 奖励物品数量 */
  rewardItemCount?: number;
  /** 任务专用扩展配置（纯文本） */
  extraConfig?: string;
  /** 列表显示顺序，越小越靠前 */
  sortOrder?: number;
  /** 是否启用（0=禁用 1=启用） */
  enabled?: number;
}

/** 获取所有每日活跃任务列表（含已禁用） */
export function getTaskList() {
  return axios.get<DailyActiveTaskForm[]>('/dailyActiveTask/v1/getTaskList');
}

/** 获取单个每日活跃任务详情 */
export function getTask(id: number) {
  return axios.get<DailyActiveTaskForm>(`/dailyActiveTask/v1/getTask/${id}`);
}

/** 保存每日活跃任务（新增或更新） */
export function saveTask(data: DailyActiveTaskForm) {
  return axios.post<DailyActiveTaskForm>('/dailyActiveTask/v1/saveTask', data);
}

/** 切换每日活跃任务启用/禁用状态 */
export function toggleEnabled(id: number) {
  return axios.post(`/dailyActiveTask/v1/toggleEnabled/${id}`);
}

/** 删除每日活跃任务 */
export function deleteTask(id: number) {
  return axios.delete(`/dailyActiveTask/v1/deleteTask/${id}`);
}
