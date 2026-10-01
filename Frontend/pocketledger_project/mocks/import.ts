import type { ImportPreview, ImportResult } from "../types/api";
export const mockImportPreview: ImportPreview = {//Mock CSV preview before the user confirms the import
  uploadId: "demo-upload-001",
  columns: ["Transaction Date", "Description", "Amount"],
  sampleRows: [
    ["09/03/2026", "WALMART #2841", "-42.67"],
    ["09/04/2026", "CHIPOTLE 2468", "-11.35"],
    ["09/05/2026", "OSU PAYROLL", "480.00"],
  ],
  rowCount: 28,
};
export const mockImportResult: ImportResult = {//Mock result after the CSV import finishes
  imported: 26,
  skipped: 2,
  failed: 0,
};      