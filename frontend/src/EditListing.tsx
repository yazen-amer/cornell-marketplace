import { API_URL } from './config';
import { useParams, useNavigate } from 'react-router-dom';
import { useState, useEffect } from 'react';
import { SUGGESTED_PICKUP_LOCATIONS } from './pickupLocations';

type EditListingProps = { token: string | null };

export default function EditListing(props: EditListingProps) {
  const params = useParams();
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState(0);
  const [pickupLocation, setPickupLocation] = useState('');
  const [image, setImage] = useState<File | null>(null);
  const [existingImageUrl, setExistingImageUrl] = useState('');
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetch(`${API_URL}/listings/${params.id}`)
      .then((response) => {
        if (!response.ok) throw new Error('Failed to fetch listing');
        return response.json();
      })
      .then((data) => {
        setTitle(data.title);
        setDescription(data.description);
        setPrice(data.price);
        setPickupLocation(data.pickupLocation ?? '');
        setExistingImageUrl(data.imageUrl);
        setLoading(false);
      });
  }, [params.id]);

  if (loading) return <div className="loading-state">Loading listing…</div>;

  const listingEditRequest = { title, description, price, pickupLocation };

  return (
    <main className="page page-narrow">
      <form className="form-card" onSubmit={(e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('listingEditRequest', new Blob([JSON.stringify(listingEditRequest)], { type: 'application/json' }));
        if (image) formData.append('imageUpload', image);

        fetch(`${API_URL}/listings/${params.id}`, {
          method: 'PATCH',
          headers: { Authorization: `Bearer ${props.token}` },
          body: formData,
        }).then((response) => {
          if (response.ok) navigate(`/listings/${params.id}`);
          else console.error('Edit failed');
        });
      }}>
        <div className="form-header">
          <p className="eyebrow">Manage listing</p>
          <h1>Edit listing</h1>
          <p className="page-subtitle">Update the details buyers see.</p>
        </div>
        <div className="form-grid">
          <div className="form-field">
            <label htmlFor="title">Title</label>
            <input id="title" type="text" value={title} onChange={(e) => setTitle(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="description">Description</label>
            <textarea id="description" value={description} onChange={(e) => setDescription(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="price">Price</label>
            <input id="price" type="number" min="0" step="0.01" value={price} onChange={(e) => setPrice(Number(e.target.value))} />
          </div>
          <div className="form-field">
            <label htmlFor="pickupLocation">Pickup location</label>
            <input
              id="pickupLocation"
              type="text"
              list="pickup-location-options"
              placeholder="e.g. Ho Plaza"
              value={pickupLocation}
              onChange={(e) => setPickupLocation(e.target.value)}
            />
            <datalist id="pickup-location-options">
              {SUGGESTED_PICKUP_LOCATIONS.map((location) => (
                <option key={location} value={location} />
              ))}
            </datalist>
          </div>
          <div className="form-field">
            <label htmlFor="image">Photo</label>
            <img className="current-image" src={existingImageUrl} alt="Current listing" />
            <input id="image" type="file" accept="image/*" onChange={(e) => setImage(e.target.files?.[0] ?? null)} />
            <span className="file-help">Leave this empty to keep the current photo.</span>
          </div>
          <div className="form-actions">
            <button className="button button-ghost" type="button" onClick={() => navigate(`/listings/${params.id}`)}>Cancel</button>
            <button className="button button-primary" type="submit">Save changes</button>
          </div>
        </div>
      </form>
    </main>
  );
}
