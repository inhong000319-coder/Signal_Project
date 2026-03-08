export type Nullable<T> = T | null;

export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, string | number | boolean | null>;
}

export interface ApiSuccess<T> {
  data: T;
}
