import React, { useEffect, useState } from 'react';
import { useBookmarkService } from '@/services/bookmarkapi';
import type { UserSavedRoute } from '@/services/bookmarkapi';

const BookmarkList: React.FC = () => {
  const { getBookmarks } = useBookmarkService();
  const [bookmarks, setBookmarks] = useState<UserSavedRoute[]>([]);

  useEffect(() => {
console.log("bookmarklist!!")
    const fetchBookmarks = async () => {
      const data = await getBookmarks();
console.log("fetched bookmarks data")  
      setBookmarks(data);
    };
    fetchBookmarks();
  }, []);

  return (
    <ul>
      {Array.isArray(bookmarks) && bookmarks.map((b) => (
        <li key={b.name}>
          <strong>{b.name}</strong>: {" "}
          {b.savedResponse
          ? `${b.savedResponse.importingCountry} → ${b.savedResponse.exportingCountry}, Total: ${b.savedResponse.totalLandedCost} ${b.savedResponse.currency}`
          : "⚠️ Invalid or missing request data"}
        </li>
      ))}
    </ul>
  );
};

export default BookmarkList;