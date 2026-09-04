import type { Seller } from './Homepage.tsx';

type ListingCardProps = {
  id: number;
  title: string;
  price: number;
  description: string;
  seller: Seller;
  imageUrl: string;
  pickupLocation: string;
};

export default function ListingCard({ title, price, imageUrl, pickupLocation }: ListingCardProps) {
  return (
    <article className="listing-card">
      <div className="listing-card-image-wrap">
        <img className="listing-card-image" src={imageUrl} alt={title} />
      </div>
      <div className="listing-card-body">
        <p className="listing-card-title">{title}</p>
        <p className="listing-card-price">${price.toFixed(2)}</p>
        {pickupLocation && <p className="listing-card-location">📍 {pickupLocation}</p>}
      </div>
    </article>
  );
}
