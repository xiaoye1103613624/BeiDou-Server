import axios from 'axios';

export interface GcInfo {
  name: string;
  collectionCount: number;
  collectionTimeMs: number;
}

export interface JvmInfo {
  heapUsed: number;
  heapMax: number;
  nonHeapUsed: number;
  nonHeapMax: number;
  threadCount: number;
  daemonThreadCount: number;
  uptimeMs: number;
  gc: GcInfo[];
}

export interface OsInfo {
  name: string;
  arch: string;
  availableProcessors: number;
  systemCpuLoad: number;
  processCpuLoad: number;
  totalPhysicalMemory: number;
  freePhysicalMemory: number;
}

export interface ChannelOnlineInfo {
  id: number;
  playerCount: number;
}

export interface WorldOnlineInfo {
  id: number;
  playerCount: number;
  channels: ChannelOnlineInfo[];
}

export interface GameInfo {
  online: boolean;
  version: string;
  worlds: WorldOnlineInfo[];
}

export interface ServerMonitorInfo {
  sampledAt: number;
  jvm: JvmInfo;
  os: OsInfo;
  game: GameInfo;
}

export interface MysqlPoolInfo {
  available: boolean;
  activeCount: number;
  poolingCount: number;
  waitThreadCount: number;
  maxActive: number;
  connectCount: number;
  connectErrorCount: number;
  createCount: number;
  destroyCount: number;
}

export interface MysqlMonitorInfo {
  sampledAt: number;
  version: string;
  databaseName: string;
  uptimeSeconds: number;
  threadsConnected: number;
  maxConnections: number;
  threadsRunning: number;
  questions: number;
  slowQueries: number;
  databaseSizeBytes: number;
  pool: MysqlPoolInfo;
}

export function getServerMonitor() {
  return axios.get<ServerMonitorInfo>('/monitor/v1/serverInfo');
}

export function getMysqlMonitor() {
  return axios.get<MysqlMonitorInfo>('/monitor/v1/mysqlInfo');
}
