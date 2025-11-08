import React, { useState } from 'react';
import { useBookmarkService } from '@/services/bookmarkapi.ts';
import type { Tariff } from '@/services/types/countrytariff';

interface BookmarkButtonProps {
  savedResponse: Tariff;
  onSuccess?: () => void; // optional callback after saving
}

const BookmarkAddButton: React.FC<BookmarkButtonProps> = ({ savedResponse, onSuccess }) => {
  const { addBookmark } = useBookmarkService();
  const [bookmarkName, setBookmarkName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  const handleBookmark = async () => {
    if (!bookmarkName.trim()) {
      setError('Please enter a name.');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      console.log("Sending bookmark payload:", { savedResponse, bookmarkName });
      await addBookmark(savedResponse, bookmarkName.trim());
      setSaved(true);
      onSuccess?.();
    } catch (err: any) {
      console.error(err);
      setError('Failed to save bookmark');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="flex flex-col items-start gap-2">
      <input
        type="text"
        placeholder="Bookmark name"
        value={bookmarkName}
        onChange={(e) => setBookmarkName(e.target.value)}
        className="border px-2 py-1 rounded w-full"
      />

      <button
        onClick={handleBookmark}
        disabled={loading}
        className="px-3 py-1 bg-blue-500 text-white rounded w-full"
      >
        {loading ? 'Saving...' : saved ? 'Saved!' : 'Add Bookmark'}
      </button>

      {error && <span className="text-red-500 text-sm">{error}</span>}
    </div>
  );
};

export default BookmarkAddButton;
