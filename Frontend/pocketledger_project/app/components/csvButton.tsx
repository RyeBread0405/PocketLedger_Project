"use client";

import { FileUp } from "lucide-react";
import { useRef, useState, type ChangeEvent } from "react";

interface CsvButtonProps {
  onFileSelected?: (file: File) => void;
}

export default function CsvButton({ onFileSelected }: CsvButtonProps) {
  const fileInputRef = useRef<HTMLInputElement>(null);
  const [selectedFileName, setSelectedFileName] = useState("");
  const [error, setError] = useState("");

  function handleFileChange(event: ChangeEvent<HTMLInputElement>) {
    const file = event.currentTarget.files?.[0];
    event.currentTarget.value = "";
    if (!file) return;

    if (!file.name.toLowerCase().endsWith(".csv")) {
      setSelectedFileName("");
      setError("Choose a CSV file.");
      return;
    }

    setSelectedFileName(file.name);
    setError("");
    onFileSelected?.(file);
  }

  return (
    <div>
      <input
        ref={fileInputRef}
        type="file"
        accept=".csv,text/csv"
        onChange={handleFileChange}
        className="hidden"
      />
      <button
        type="button"
        onClick={() => fileInputRef.current?.click()}
        className="inline-flex items-center gap-2 rounded-lg border border-[#CFE0CF] bg-[#EEF5EC] px-4 py-2 font-medium text-[#3F5747] transition hover:bg-[#E4EFE3] focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-[#A9C9B0]"
      >
        <FileUp className="h-4 w-4" />
        Choose CSV
      </button>
      {selectedFileName && (
        <p className="mt-2 text-sm text-[#7A8580]" aria-live="polite">
          Selected: {selectedFileName}
        </p>
      )}
      {error && (
        <p className="mt-2 text-sm text-[#B5574B]" role="alert">
          {error}
        </p>
      )}
    </div>
  );
}