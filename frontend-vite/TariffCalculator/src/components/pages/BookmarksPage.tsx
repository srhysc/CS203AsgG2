import React, { useEffect, useState } from 'react';
import { useBookmarkService } from '@/services/types/bookmarkapi';
import type { UserSavedRoute } from '@/services/types/bookmarkapi';
import { Bookmark, Trash2, TrendingUp, Loader2, Package, DollarSign, Ship, Receipt } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";

const BookmarksPage: React.FC = () => {
  const { getBookmarks } = useBookmarkService();
  const [bookmarks, setBookmarks] = useState<UserSavedRoute[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    const fetchBookmarks = async () => {
      try {
        const data = await getBookmarks();
        setBookmarks(data);
      } catch (err) {
        console.error('Failed to fetch bookmarks:', err);
      } finally {
        setLoading(false);
      }
    };
    fetchBookmarks();
  }, [getBookmarks]);

  return (
    <div className="flex-1 space-y-8 p-8">
      {/* Header */}
      <div className="text-center space-y-4">
        <h1 className="text-5xl md:text-6xl font-bold bg-clip-text text-transparent bg-gradient-to-r from-[#dcff1a] to-emerald-400">
          Saved Bookmarks
        </h1>
        <p className="text-xl text-gray-400">
          View and manage your saved tariff calculations
        </p>
      </div>

      {/* Bookmarks Card */}
      <Card className="bg-white/5 backdrop-blur-lg border border-white/10">
        <CardHeader>
          <CardTitle className="flex items-center gap-2">
            <Bookmark className="w-6 h-6 text-[#dcff1a]" />
            Your Bookmarks
          </CardTitle>
        </CardHeader>
        <CardContent>
          {loading ? (
            <div className="flex items-center justify-center p-12">
              <Loader2 className="w-8 h-8 animate-spin text-[#dcff1a]" />
            </div>
          ) : !bookmarks || bookmarks.length === 0 ? (
            <div className="text-center p-12 bg-slate-800/30 rounded-lg border border-white/10">
              <Bookmark className="w-16 h-16 mx-auto mb-4 text-gray-500" />
              <p className="text-xl text-gray-400 mb-2">No bookmarks yet</p>
              <p className="text-sm text-gray-500">
                Save calculations from the Tariff Calculator to access them here
              </p>
            </div>
          ) : (
            <div className="space-y-4">
              {bookmarks.map((bookmark, index) => (
                <div
                  key={bookmark.name || index}
                  className="bg-slate-800/50 rounded-lg p-5 border border-white/10 hover:border-[#dcff1a]/50 transition-all"
                >
                  <div className="flex items-start justify-between gap-4">
                    <div className="flex-1">
                      <div className="flex items-center gap-2 mb-3">
                        <Bookmark className="w-5 h-5 text-[#dcff1a]" />
                        <h4 className="text-xl font-semibold text-gray-200">{bookmark.name}</h4>
                      </div>
                      
                      {bookmark.savedResponse ? (
                        <div className="space-y-4">
                          <div className="flex items-center gap-2 text-gray-300">
                            <TrendingUp className="w-5 h-5 text-emerald-400" />
                            <span className="text-lg">
                              {bookmark.savedResponse.exportingCountry} → {bookmark.savedResponse.importingCountry}
                            </span>
                          </div>
                          
                          {/* Product Info */}
                          <div className="grid grid-cols-1 md:grid-cols-3 gap-4 text-sm">
                            <div className="p-3 bg-slate-700/50 rounded-lg">
                              <span className="text-gray-500 block mb-1">Product</span>
                              <span className="text-gray-200 font-medium">{bookmark.savedResponse.petroleumName}</span>
                            </div>
                            <div className="p-3 bg-slate-700/50 rounded-lg">
                              <span className="text-gray-500 block mb-1">HS Code</span>
                              <span className="text-gray-200 font-medium">{bookmark.savedResponse.hsCode}</span>
                            </div>
                            <div className="p-3 bg-slate-700/50 rounded-lg">
                              <span className="text-gray-500 block mb-1">Price per Unit</span>
                              <span className="text-gray-200 font-medium">
                                {bookmark.savedResponse.pricePerUnit} {bookmark.savedResponse.currency}
                              </span>
                            </div>
                          </div>

                          {/* Cost Breakdown */}
                          <div className="bg-slate-700/30 rounded-lg p-4 space-y-3">
                            <h5 className="font-semibold text-gray-300 flex items-center gap-2">
                              <Receipt className="w-4 h-4 text-[#dcff1a]" />
                              Cost Breakdown
                            </h5>
                            
                            <div className="grid grid-cols-1 md:grid-cols-2 gap-3 text-sm">
                              <div className="flex items-center justify-between p-2 bg-slate-800/50 rounded">
                                <span className="text-gray-400 flex items-center gap-2">
                                  <Package className="w-4 h-4" />
                                  Base Price
                                </span>
                                <span className="text-gray-200 font-medium">
                                  {bookmark.savedResponse.basePrice} {bookmark.savedResponse.currency}
                                </span>
                              </div>
                              
                              <div className="flex items-center justify-between p-2 bg-slate-800/50 rounded">
                                <span className="text-gray-400 flex items-center gap-2">
                                  <Ship className="w-4 h-4" />
                                  Shipping Cost
                                </span>
                                <span className="text-gray-200 font-medium">
                                  {bookmark.savedResponse.shippingCost} {bookmark.savedResponse.currency}
                                </span>
                              </div>
                              
                              <div className="flex items-center justify-between p-2 bg-slate-800/50 rounded">
                                <span className="text-gray-400 flex items-center gap-2">
                                  <DollarSign className="w-4 h-4" />
                                  Tariff Fees ({bookmark.savedResponse.tariffRate}%)
                                </span>
                                <span className="text-gray-200 font-medium">
                                  {bookmark.savedResponse.tariffFees} {bookmark.savedResponse.currency}
                                </span>
                              </div>
                              
                              <div className="flex items-center justify-between p-2 bg-slate-800/50 rounded">
                                <span className="text-gray-400 flex items-center gap-2">
                                  <Receipt className="w-4 h-4" />
                                  VAT Fees ({bookmark.savedResponse.vatRate}%)
                                </span>
                                <span className="text-gray-200 font-medium">
                                  {bookmark.savedResponse.vatFees} {bookmark.savedResponse.currency}
                                </span>
                              </div>
                            </div>
                          </div>
                          
                          {/* Total */}
                          <div className="pt-3 mt-3 border-t border-white/10">
                            <div className="flex items-baseline gap-2">
                              <span className="text-gray-400">Total Landed Cost:</span>
                              <span className="text-2xl font-bold text-[#dcff1a]">
                                {bookmark.savedResponse.totalLandedCost} {bookmark.savedResponse.currency}
                              </span>
                            </div>
                          </div>
                        </div>
                      ) : (
                        <p className="text-red-400 text-sm">⚠️ Invalid or missing data</p>
                      )}
                    </div>
                    
                    {/* <button
                      className="p-2 text-gray-400 hover:text-red-400 hover:bg-red-900/20 rounded-lg transition-all"
                      title="Delete bookmark"
                    >
                      <Trash2 className="w-5 h-5" />
                    </button> */}
                  </div>
                </div>
              ))}
            </div>
          )}
        </CardContent>
      </Card>
    </div>
  );
};

export default BookmarksPage;
