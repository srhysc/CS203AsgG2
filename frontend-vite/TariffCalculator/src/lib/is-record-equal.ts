export function isRecordEqual<T extends Record<string, unknown>>(first: T, second: T): boolean {
  return (Object.keys(first) as Array<keyof T>).every((key) => first[key] === second[key]);
}

