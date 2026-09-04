import { API_URL } from './config';
import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import type { Listing } from './Homepage.tsx';
import type { UserDto } from './App.tsx';

type ListingDetailsProps = {
  currentUser: UserDto | null;
  token: string | null;
};

export default function ListingDetails(props: ListingDetailsProps) {
  const params = useParams();
  const navigate = useNavigate();
  const [listing, setListing] = useState<Listing>();

  function handleDelete() {
    const confirmed = window.confirm('Are you sure you want to delete this item?');
    if (!confirmed) return;

    fetch(`${API_URL}/listings/${params.id}`, {
      method: 'DELETE',
      headers: { Authorization: `Bearer ${props.token}` },
    }).then((response) => {
      if (response.ok) navigate('/');
      else console.error('Delete failed');
    });
  }

  useEffect(() => {
    fetch(`${API_URL}/listings/${params.id}`)
      .then((response) => response.json())
      .then((data) => setListing(data));
  }, [params.id]);

  if (!listing) return <div className="loading-state">Loading listing…</div>;

  const isOwner = props.currentUser !== null && props.currentUser.id === listing.seller.id;

  return (
    <main className="page">
      <div className="details-card">
        <div className="details-media">
          <img className="details-image" src={listing.imageUrl} alt={listing.title} />
        </div>
        <section className="details-content">
          <p className="eyebrow">Marketplace listing</p>
          <h1 className="details-title">{listing.title}</h1>
          <p className="details-price">${listing.price.toFixed(2)}</p>
          <h2>Description</h2>
          <p className="details-description">{listing.description}</p>
          {listing.pickupLocation && (
            <p className="details-location">📍 Pickup at <strong>{listing.pickupLocation}</strong></p>
          )}
          <p className="details-seller">Listed by <strong>{listing.seller.username}</strong></p>

          {isOwner && (
            <div className="details-actions">
              <button className="button button-secondary" onClick={() => navigate(`/listings/${params.id}/edit`)}>
                Edit listing
              </button>
              <button className="button button-danger" onClick={handleDelete}>
                Delete listing
              </button>
            </div>
          )}
        </section>
      </div>
    </main>
  );
}
