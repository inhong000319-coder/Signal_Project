import { useAuthStore } from "@/features/auth/store";
import type { TokenPair } from "@/types/domain";

interface RequestOptions {
  headers?: Record<string, string>;
  skipAuthRefresh?: boolean;
}

export interface ClientHttpError {
  status: number;
  code: string;
  message: string;
  details?: Array<{ field?: string; reason?: string }>;
}

interface BackendErrorResponse {
  code?: string;
  message?: string;
  details?: Array<{ field?: string; reason?: string }>;
}

const AUTH_LOGIN_PATH = '/api/auth/login';
const AUTH_REFRESH_PATH = '/api/auth/refresh';
let refreshInFlight: Promise<string | null> | null = null;

function buildHeaders(options?: RequestOptions, hasBody = false): Record<string, string> {
  const headers: Record<string, string> = {
    ...(options?.headers ?? {}),
  };

  if (hasBody && !Object.keys(headers).some((key) => key.toLowerCase() === 'content-type')) {
    headers['Content-Type'] = 'application/json';
  }

  return headers;
}

function hasAuthorizationHeader(headers: Record<string, string>): boolean {
  return Object.keys(headers).some((key) => key.toLowerCase() === 'authorization');
}

function withAuthorizationHeader(headers: Record<string, string>, token: string): Record<string, string> {
  const next = { ...headers };
  const existingKey = Object.keys(next).find((key) => key.toLowerCase() === 'authorization');
  next[existingKey ?? 'Authorization'] = `Bearer ${token}`;
  return next;
}

function shouldTryRefresh(path: string, options: RequestOptions | undefined, headers: Record<string, string>): boolean {
  if (options?.skipAuthRefresh) return false;
  if (path === AUTH_LOGIN_PATH || path === AUTH_REFRESH_PATH) return false;
  return hasAuthorizationHeader(headers);
}

async function parseBody(response: Response): Promise<unknown> {
  if (response.status === 204 || response.status === 205 || response.status === 304) {
    return undefined;
  }

  const text = await response.text();
  if (!text.trim()) {
    return undefined;
  }

  const contentType = response.headers.get('content-type') ?? '';
  if (contentType.includes('application/json')) {
    try {
      return JSON.parse(text);
    } catch {
      return text;
    }
  }

  return text;
}

function toClientError(status: number, body: unknown): ClientHttpError {
  if (body && typeof body === 'object') {
    const apiError = body as BackendErrorResponse;
    if (typeof apiError.code === 'string' && typeof apiError.message === 'string') {
      return {
        status,
        code: apiError.code,
        message: apiError.message,
        details: Array.isArray(apiError.details) ? apiError.details : undefined,
      };
    }
  }

  if (typeof body === 'string' && body.trim().length > 0) {
    return { status, code: 'HTTP_ERROR', message: body };
  }

  return {
    status,
    code: 'HTTP_ERROR',
    message: status >= 500 ? '서버 오류가 발생했습니다.' : '요청 처리에 실패했습니다.',
  };
}

async function requestNewAccessToken(): Promise<string | null> {
  const store = useAuthStore.getState();
  const refreshToken = store.tokenPair?.refreshToken;
  if (!refreshToken) {
    store.clearSession();
    return null;
  }

  try {
    const response = await fetch(AUTH_REFRESH_PATH, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken }),
    });
    const parsed = await parseBody(response);
    if (!response.ok) {
      store.clearSession();
      return null;
    }

    const tokenPair = parsed as TokenPair;
    if (!tokenPair?.accessToken || !tokenPair?.refreshToken) {
      store.clearSession();
      return null;
    }

    store.setSession(tokenPair, store.me);
    return tokenPair.accessToken;
  } catch {
    return null;
  }
}

async function refreshAccessTokenOnce(): Promise<string | null> {
  if (!refreshInFlight) {
    refreshInFlight = requestNewAccessToken().finally(() => {
      refreshInFlight = null;
    });
  }
  return refreshInFlight;
}

async function executeFetch(method: 'GET' | 'POST' | 'PATCH', path: string, body: unknown, headers: Record<string, string>): Promise<Response> {
  const hasBody = body !== undefined;
  return fetch(path, {
    method,
    headers,
    body: hasBody ? JSON.stringify(body) : undefined,
  });
}

async function request<T>(method: 'GET' | 'POST' | 'PATCH', path: string, body?: unknown, options?: RequestOptions): Promise<T> {
  const hasBody = body !== undefined;
  const requestHeaders = buildHeaders(options, hasBody);

  try {
    const response = await executeFetch(method, path, body, requestHeaders);

    const parsed = await parseBody(response);

    if (!response.ok) {
      if (response.status === 401 && shouldTryRefresh(path, options, requestHeaders)) {
        const newAccessToken = await refreshAccessTokenOnce();
        if (newAccessToken) {
          const retryHeaders = withAuthorizationHeader(requestHeaders, newAccessToken);
          const retried = await executeFetch(method, path, body, retryHeaders);
          const retriedParsed = await parseBody(retried);
          if (!retried.ok) {
            throw toClientError(retried.status, retriedParsed);
          }
          return retriedParsed as T;
        }
      }
      throw toClientError(response.status, parsed);
    }

    return parsed as T;
  } catch (error) {
    if (isClientHttpError(error)) {
      return Promise.reject(error);
    }

    return Promise.reject({
      status: 0,
      code: 'NETWORK_ERROR',
      message: '서버에 연결할 수 없습니다.',
    });
  }
}

function isClientHttpError(error: unknown): error is ClientHttpError {
  return (
    typeof error === 'object' &&
    error !== null &&
    'status' in error &&
    'code' in error &&
    'message' in error
  );
}

export const httpClient = {
  get<T>(path: string, options?: RequestOptions): Promise<T> {
    return request<T>('GET', path, undefined, options);
  },
  post<T>(path: string, body?: unknown, options?: RequestOptions): Promise<T> {
    return request<T>('POST', path, body, options);
  },
  patch<T>(path: string, body?: unknown, options?: RequestOptions): Promise<T> {
    return request<T>('PATCH', path, body, options);
  },
};

export function initializeMockData(): void {
  // WHY: StoreProvider still calls this during bootstrap; real REST mode intentionally performs no local seeding.
}
