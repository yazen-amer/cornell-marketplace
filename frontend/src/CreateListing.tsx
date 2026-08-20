import { API_URL } from './config';
import { useState } from 'react';
import { useNavigate } from 'react-router-dom';

type CreateListingProps = { token: string | null };

export default function CreateListing(props: CreateListingProps) {
  const navigate = useNavigate();
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [price, setPrice] = useState(0);
  const [image, setImage] = useState<File | null>(null);

  const listingRequest = { title, description, price };
  const canSubmit = title.trim() !== '' && description.trim() !== '' && image !== null;

  return (
    <main className="page page-narrow">
      <form className="form-card" onSubmit={(e) => {
        e.preventDefault();
        const formData = new FormData();
        formData.append('listingRequest', new Blob([JSON.stringify(listingRequest)], { type: 'application/json' }));
        if (image) formData.append('imageUpload', image);

        fetch(`${API_URL}/listings`, {
          method: 'POST',
          headers: { Authorization: `Bearer ${props.token}` },
          body: formData,
        }).then((response) => {
          if (response.ok) navigate('/');
          else console.error('Create listing failed');
        });
      }}>
        <div className="form-header">
          <p className="eyebrow">New listing</p>
          <h1>Sell an item</h1>
          <p className="page-subtitle">Add the essentials now. You can update the listing later.</p>
        </div>
        <div className="form-grid">
          <div className="form-field">
            <label htmlFor="title">Title</label>
            <input id="title" type="text" placeholder="e.g. Mini fridge" value={title} onChange={(e) => setTitle(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="description">Description</label>
            <textarea id="description" placeholder="Describe the condition, pickup location, and anything buyers should know." value={description} onChange={(e) => setDescription(e.target.value)} />
          </div>
          <div className="form-field">
            <label htmlFor="price">Price</label>
            <input id="price" type="number" min="0" step="0.01" placeholder="0.00" value={price || ''} onChange={(e) => setPrice(Number(e.target.value))} />
          </div>
          <div className="form-field">
            <label htmlFor="image">Photo</label>
            <input id="image" type="file" accept="image/*" onChange={(e) => setImage(e.target.files?.[0] ?? null)} />
            <span className="file-help">Choose a clear photo with good lighting.</span>
          </div>
          <div className="form-actions">
            <button className="button button-ghost" type="button" onClick={() => navigate('/')}>Cancel</button>
            <button className="button button-primary" type="submit" disabled={!canSubmit}>Publish listing</button>
          </div>
        </div>
      </form>
    </main>
  );
}
