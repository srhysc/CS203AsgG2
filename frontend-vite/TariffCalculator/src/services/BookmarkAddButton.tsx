import React, { useState } from 'react';
import { useBookmarkService} from '@/services/bookmarkapi.ts';
import type { Tariff } from '@/services/types/countrytariff';

interface BookmarkButtonProps {
  request: Tariff;
  bookmarkName: string;
  onSuccess?: () => void; // optional callback after saving
}

const BookmarkAddButton: React.FC<BookmarkButtonProps> = ({ request, bookmarkName, onSuccess }) => {
  const { addBookmark } = useBookmarkService();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleBookmark = async () => {
    setLoading(true);
    setError(null);
    try {
      await addBookmark(request, bookmarkName);
      onSuccess?.(); // notify parent component
    } catch (err: any) {
      console.error(err);
      setError('Failed to save bookmark');
    } finally {
      setLoading(false);
    }
  };

  return (
    <button onClick={handleBookmark} disabled={loading} className="px-3 py-1 bg-blue-500 text-white rounded">
      {loading ? 'Saving...' : 'Add Bookmark'}
      {error && <span className="ml-2 text-red-200">{error}</span>}
    </button>
  );
};

export default BookmarkAddButton;
