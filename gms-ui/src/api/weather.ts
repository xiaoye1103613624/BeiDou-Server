import axios from 'axios';

export interface WeatherOption {
  value: string;
  label: string;
  commandToken?: string | null;
}

export interface WeatherStatus {
  clock?: string;
  wallClock?: string;
  nightLevel?: number;
  timeFrozen?: boolean;
  skyForced?: boolean;
  bareSky?: boolean;
  skyId?: number;
  skyName?: string;
  skyNameZh?: string;
  equivalentCommand?: string;
  timeOptions?: WeatherOption[];
  skyOptions?: WeatherOption[];
}

export interface WeatherApplyPayload {
  auto?: boolean;
  time?: string;
  clock?: string;
  sky?: string;
  snap?: boolean;
}

export function getWeatherStatus() {
  return axios.get('/weather/v1/status');
}

export function applyWeather(data: WeatherApplyPayload) {
  return axios.post('/weather/v1/apply', data);
}
