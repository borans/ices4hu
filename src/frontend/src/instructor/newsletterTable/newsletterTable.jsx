import React from "react";
import { useNavigate } from "react-router-dom";

function NewsletterTable_Instructor(props) {

const {title, postId, creationDatetime} = props;

let navigate = useNavigate();

return(
<tr>
    <td>{title}</td>
    <td>{new Date(creationDatetime).toLocaleTimeString([], {year: 'numeric', month: 'numeric', day: 'numeric', hour: '2-digit', minute: '2-digit'})}</td>
    <td><button onClick={(event) => {event.preventDefault(); navigate("/viewPostInstructor/" + postId)}}>SEE POST</button></td>

</tr>
)
}

export default NewsletterTable_Instructor;