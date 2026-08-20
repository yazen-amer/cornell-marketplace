import { API_URL } from './config';
import { useParams } from "react-router-dom";
import { useNavigate } from "react-router-dom";

type DeleteListingProps = {
  token: string | null
}

export default function DeleteListing(props: DeleteListingProps) {
    let params = useParams();
    let navigate = useNavigate();

    const confirmed = window.confirm("Are you sure you want to delete this item?");

    if (confirmed) {
        return (
            fetch(`${API_URL}/listings/${params.id}`, {
                        method: "DELETE",
                        headers: { 'Authorization': `Bearer ${props.token}` },
                    }).then((Response) => {if (Response.ok) {
                                                navigate("/");
                                            }
                                        else {
                                            console.error("Delete failed");
                                    }
                    })
        );
    }

}