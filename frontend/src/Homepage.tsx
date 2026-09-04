import { API_URL } from './config';
import ListingCard from './ListingCard.tsx';
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';

export interface Seller { id: number; username: string; }
export interface Listing { id: number; title: string; price: number; description: string; seller: Seller; imageUrl: string; pickupLocation: string; }

export function HomePage() {
  const [listings, setListings] = useState<Listing[]>([]);
  const [searchTerm, setSearchTerm] = useState('');
  const params = new URLSearchParams({ title: searchTerm });

  useEffect(() => {
    const url = searchTerm === ''
      ? `${API_URL}/listings`
      : `${API_URL}/listings/search?${params}`;

    fetch(url).then((response) => response.json()).then((data) => setListings(data));
  }, [searchTerm]);

  return (
    <main className="page">
      <div className="page-header">
        <div>
          <p className="eyebrow">Student marketplace</p>
          <h1>Find what you need on campus.</h1>
          <p className="page-subtitle">Buy and sell furniture, tech, textbooks, and more within the Cornell community.</p>
        </div>
        <div className="search-bar">
          <span className="search-icon">⌕</span>
          <input
            type="search"
            id="Search"
            aria-label="Search listings"
            placeholder="Search listings"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
          />
        </div>
      </div>

      {listings.length === 0 ? (
        <div className="empty-state">No listings found.</div>
      ) : (
        <div className="listings-grid">
          {listings.map((listing) => (
            <Link className="listing-link" key={listing.id} to={`/listings/${listing.id}`}>
              <ListingCard {...listing} />
            </Link>
          ))}
        </div>
      )}
    </main>
  );
}
