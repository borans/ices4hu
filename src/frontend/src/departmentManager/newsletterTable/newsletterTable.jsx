import React from "react";
import { useNavigate } from "react-router-dom";

function NewsletterTable_DM(props) {

    const {onDelete, postId, creationDatetime, title} = props;
    let navigate = useNavigate();

    return(
        <tr>
            <td>{title}</td>
            <td>{new Date(creationDatetime).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
            <td><button className="action-button" onClick={(event) => { event.preventDefault(); navigate("/editPost/" + postId)}}>EDIT</button></td>
            <td><button className="action-button deny-button" onClick={(event)=> onDelete(event, postId)}>DELETE</button></td>

        </tr>
    )
}
export default NewsletterTable_DM;