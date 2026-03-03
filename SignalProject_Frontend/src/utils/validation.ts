export function validateLoginId(value: string): string | null {
  if (value.trim().length < 4) return "아이디는 4자 이상이어야 합니다.";
  return null;
}

export function validatePasswordForLogin(value: string): string | null {
  if (!value) return "비밀번호를 입력해 주세요.";
  return null;
}

export function validatePasswordPolicy(value: string): string | null {
  if (value.length < 8) return "비밀번호는 8자 이상이어야 합니다.";
  if (!/[a-z]/.test(value)) return "비밀번호에 소문자를 포함해 주세요.";
  if (!/[0-9]/.test(value)) return "비밀번호에 숫자를 포함해 주세요.";
  if (!/[^A-Za-z0-9]/.test(value)) return "비밀번호에 특수문자를 포함해 주세요.";
  return null;
}

export function validatePassword(value: string): string | null {
  return validatePasswordPolicy(value);
}

export function validateNickname(value: string): string | null {
  if (value.trim().length < 2) return "닉네임은 2자 이상이어야 합니다.";
  return null;
}

export function validateMessageContent(value: string): string | null {
  const trimmed = value.trim();
  if (!trimmed) return "메시지를 입력해 주세요.";
  if (trimmed.length > 2000) return "메시지는 2000자 이하여야 합니다.";
  return null;
}
