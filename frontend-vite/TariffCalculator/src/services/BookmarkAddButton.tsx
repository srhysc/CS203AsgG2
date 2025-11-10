// import React, { useState } from 'react';
// import { useBookmarkService } from '@/services/bookmarkapi.ts';
// import type { Tariff } from '@/services/types/countrytariff';

// interface BookmarkButtonProps {
//   savedResponse: Tariff;
//   onSuccess?: () => void; // optional callback after saving
// }

// const BookmarkAddButton: React.FC<BookmarkButtonProps> = ({ savedResponse, onSuccess }) => {
//   const { addBookmark } = useBookmarkService();
//   const [bookmarkName, setBookmarkName] = useState('');
//   const [loading, setLoading] = useState(false);
//   const [error, setError] = useState<string | null>(null);
//   const [saved, setSaved] = useState(false);

//   const handleBookmark = async () => {
//     if (!bookmarkName.trim()) {
//       setError('Please enter a name.');
//       return;
//     }
//     setLoading(true);
//     setError(null);
//     try {
//       console.log("Sending bookmark payload:", { savedResponse, bookmarkName });
//       await addBookmark(savedResponse, bookmarkName.trim());
//       setSaved(true);
//       onSuccess?.();
//     } catch (err: any) {
//       console.error(err);
//       setError('Failed to save bookmark');
//     } finally {
//       setLoading(false);
//     }
//   };

//   return (
//     <div className="flex flex-col items-start gap-2">
//       <input
//         type="text"
//         placeholder="Bookmark name"
//         value={bookmarkName}
//         onChange={(e) => setBookmarkName(e.target.value)}
//         className="border px-2 py-1 rounded w-full"
//       />

//       <button
//         onClick={handleBookmark}
//         disabled={loading}
//         className="px-3 py-1 bg-blue-500 text-white rounded w-full"
//       >
//         {loading ? 'Saving...' : saved ? 'Saved!' : 'Add Bookmark'}
//       </button>

//       {error && <span className="text-red-500 text-sm">{error}</span>}
//     </div>
//   );
// };

// export default BookmarkAddButton;

import React, { useState } from 'react';
import { useBookmarkService } from '@/services/types/bookmarkapi';
import type { Tariff } from '@/services/types/countrytariff';
import { Bookmark, Check, Loader2 } from 'lucide-react';

interface BookmarkButtonProps {
  savedResponse: Tariff;
  onSuccess?: () => void;
}

const BookmarkAddButton: React.FC<BookmarkButtonProps> = ({ savedResponse, onSuccess }) => {
  const { addBookmark } = useBookmarkService();
  const [bookmarkName, setBookmarkName] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [saved, setSaved] = useState(false);

  const handleBookmark = async () => {
    if (!bookmarkName.trim()) {
      setError('Please enter a bookmark name');
      return;
    }
    setLoading(true);
    setError(null);
    try {
      await addBookmark(savedResponse, bookmarkName.trim());
      setSaved(true);
      setBookmarkName('');
      setTimeout(() => setSaved(false), 2000);
      onSuccess?.();
    } catch (err: any) {
      console.error(err);
      setError('Failed to save bookmark');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="bg-slate-800/50 rounded-lg p-4 border border-white/10">
      <div className="flex items-center gap-2 mb-3">
        <Bookmark className="w-5 h-5 text-[#dcff1a]" />
        <h3 className="text-lg font-semibold text-gray-200">Save This Calculation</h3>
      </div>
      
      <div className="flex flex-col sm:flex-row gap-3">
        <input
          type="text"
          placeholder="Enter bookmark name..."
          value={bookmarkName}
          onChange={(e) => {
            setBookmarkName(e.target.value);
            setError(null);
          }}
          className="flex-1 px-4 py-2 bg-slate-700/50 border border-white/10 rounded-lg text-gray-200 placeholder-gray-500 focus:outline-none focus:ring-2 focus:ring-[#dcff1a] focus:border-transparent"
          disabled={loading || saved}
        />
        
        <button
          onClick={handleBookmark}
          disabled={loading || saved}
          className={`px-6 py-2 rounded-lg font-medium transition-all flex items-center justify-center gap-2 min-w-[140px] ${
            saved
              ? 'bg-emerald-500 text-white'
              : 'bg-gradient-to-r from-[#dcff1a] to-emerald-400 text-black hover:shadow-lg hover:scale-105'
          } disabled:opacity-50 disabled:cursor-not-allowed`}
        >
          {loading ? (
            <>
              <Loader2 className="w-4 h-4 animate-spin" />
              Saving...
            </>
          ) : saved ? (
            <>
              <Check className="w-4 h-4" />
              Saved!
            </>
          ) : (
            <>
              <Bookmark className="w-4 h-4" />
              Bookmark
            </>
          )}
        </button>
      </div>
      
      {error && (
        <p className="text-red-400 text-sm mt-2 flex items-center gap-1">
          ⚠️ {error}
        </p>
      )}
    </div>
  );
};

export default BookmarkAddButton;