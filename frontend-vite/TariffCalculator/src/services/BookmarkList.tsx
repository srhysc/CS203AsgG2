import React, { useEffect, useState } from 'react';
import { useBookmarkService } from '@/services/bookmarkapi';
import type { UserSavedRoute } from '@/services/bookmarkapi';

const BookmarkList: React.FC = () => {
  const { getBookmarks } = useBookmarkService();
  const [bookmarks, setBookmarks] = useState<UserSavedRoute[]>([]);

  useEffect(() => {
    const fetchBookmarks = async () => {
      const data = await getBookmarks();
      setBookmarks(data);
    };
    fetchBookmarks();
  }, []);

  return (
    <ul>
      {bookmarks.map((b) => (
        <li key={b.name}>
          <strong>{b.name}</strong>: {b.request.importingCountry} → {b.request.exportingCountry}, 
          Total: {b.request.totalLandedCost} {b.request.currency}
        </li>
      ))}
    </ul>
  );
};

export default BookmarkList;